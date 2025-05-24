// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.MapMaker;
import java.io.File;
import java.util.logging.Level;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraft.server.MinecraftServer;
import java.util.ListIterator;
import java.util.List;
import cpw.mods.fml.common.FMLLog;
import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;
import com.google.common.collect.Multiset;
import java.util.concurrent.ConcurrentMap;
import java.util.BitSet;
import java.util.ArrayList;
import java.util.Hashtable;

public class DimensionManager
{
    private static Hashtable<Integer, Class<? extends aei>> providers;
    private static Hashtable<Integer, Boolean> spawnSettings;
    private static Hashtable<Integer, js> worlds;
    private static boolean hasInit;
    private static Hashtable<Integer, Integer> dimensions;
    private static ArrayList<Integer> unloadQueue;
    private static BitSet dimensionMap;
    private static ConcurrentMap<abw, abw> weakWorldMap;
    private static Multiset<Integer> leakedWorlds;
    
    public static boolean registerProviderType(final int id, final Class<? extends aei> provider, final boolean keepLoaded) {
        if (DimensionManager.providers.containsKey(id)) {
            return false;
        }
        DimensionManager.providers.put(id, provider);
        DimensionManager.spawnSettings.put(id, keepLoaded);
        return true;
    }
    
    public static int[] unregisterProviderType(final int id) {
        if (!DimensionManager.providers.containsKey(id)) {
            return new int[0];
        }
        DimensionManager.providers.remove(id);
        DimensionManager.spawnSettings.remove(id);
        final int[] ret = new int[DimensionManager.dimensions.size()];
        int x = 0;
        for (final Map.Entry<Integer, Integer> ent : DimensionManager.dimensions.entrySet()) {
            if (ent.getValue() == id) {
                ret[x++] = ent.getKey();
            }
        }
        return Arrays.copyOf(ret, x);
    }
    
    public static void init() {
        if (DimensionManager.hasInit) {
            return;
        }
        registerProviderType(0, (Class<? extends aei>)aek.class, DimensionManager.hasInit = true);
        registerProviderType(-1, (Class<? extends aei>)aej.class, true);
        registerProviderType(1, (Class<? extends aei>)ael.class, false);
        registerDimension(0, 0);
        registerDimension(-1, -1);
        registerDimension(1, 1);
    }
    
    public static void registerDimension(final int id, final int providerType) {
        if (!DimensionManager.providers.containsKey(providerType)) {
            throw new IllegalArgumentException(String.format("Failed to register dimension for id %d, provider type %d does not exist", id, providerType));
        }
        if (DimensionManager.dimensions.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Failed to register dimension for id %d, One is already registered", id));
        }
        DimensionManager.dimensions.put(id, providerType);
        if (id >= 0) {
            DimensionManager.dimensionMap.set(id);
        }
    }
    
    public static void unregisterDimension(final int id) {
        if (!DimensionManager.dimensions.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Failed to unregister dimension for id %d; No provider registered", id));
        }
        DimensionManager.dimensions.remove(id);
    }
    
    public static boolean isDimensionRegistered(final int dim) {
        return DimensionManager.dimensions.containsKey(dim);
    }
    
    public static int getProviderType(final int dim) {
        if (!DimensionManager.dimensions.containsKey(dim)) {
            throw new IllegalArgumentException(String.format("Could not get provider type for dimension %d, does not exist", dim));
        }
        return DimensionManager.dimensions.get(dim);
    }
    
    public static aei getProvider(final int dim) {
        return getWorld(dim).t;
    }
    
    public static Integer[] getIDs(final boolean check) {
        if (check) {
            final List<abw> allWorlds = Lists.newArrayList((Iterable)DimensionManager.weakWorldMap.keySet());
            allWorlds.removeAll(DimensionManager.worlds.values());
            final ListIterator<abw> li = allWorlds.listIterator();
            while (li.hasNext()) {
                final abw w = li.next();
                DimensionManager.leakedWorlds.add((Object)System.identityHashCode(w));
            }
            final Iterator i$ = allWorlds.iterator();
            while (i$.hasNext()) {
                final abw w = i$.next();
                final int leakCount = DimensionManager.leakedWorlds.count((Object)System.identityHashCode(w));
                if (leakCount == 5) {
                    FMLLog.fine("The world %x (%s) may have leaked: first encounter (5 occurences).\n", System.identityHashCode(w), w.N().k());
                }
                else {
                    if (leakCount % 5 != 0) {
                        continue;
                    }
                    FMLLog.fine("The world %x (%s) may have leaked: seen %d times.\n", System.identityHashCode(w), w.N().k(), leakCount);
                }
            }
        }
        return getIDs();
    }
    
    public static Integer[] getIDs() {
        return DimensionManager.worlds.keySet().toArray(new Integer[DimensionManager.worlds.size()]);
    }
    
    public static void setWorld(final int id, final js world) {
        if (world != null) {
            DimensionManager.worlds.put(id, world);
            DimensionManager.weakWorldMap.put((abw)world, (abw)world);
            MinecraftServer.F().worldTickTimes.put(id, new long[100]);
            FMLLog.info("Loading dimension %d (%s) (%s)", id, world.N().k(), world.p());
        }
        else {
            DimensionManager.worlds.remove(id);
            MinecraftServer.F().worldTickTimes.remove(id);
            FMLLog.info("Unloading dimension %d", id);
        }
        final ArrayList<js> tmp = new ArrayList<js>();
        if (DimensionManager.worlds.get(0) != null) {
            tmp.add(DimensionManager.worlds.get(0));
        }
        if (DimensionManager.worlds.get(-1) != null) {
            tmp.add(DimensionManager.worlds.get(-1));
        }
        if (DimensionManager.worlds.get(1) != null) {
            tmp.add(DimensionManager.worlds.get(1));
        }
        for (final Map.Entry<Integer, js> entry : DimensionManager.worlds.entrySet()) {
            final int dim = entry.getKey();
            if (dim >= -1 && dim <= 1) {
                continue;
            }
            tmp.add(entry.getValue());
        }
        MinecraftServer.F().b = tmp.toArray(new js[tmp.size()]);
    }
    
    public static void initDimension(final int dim) {
        final js overworld = getWorld(0);
        if (overworld == null) {
            throw new RuntimeException("Cannot Hotload Dim: Overworld is not Loaded!");
        }
        try {
            getProviderType(dim);
        }
        catch (final Exception e) {
            System.err.println("Cannot Hotload Dim: " + e.getMessage());
            return;
        }
        final MinecraftServer mcServer = overworld.p();
        final amc savehandler = overworld.M();
        final acd worldSettings = new acd(overworld.N());
        final js world = (js)((dim == 0) ? overworld : new jl(mcServer, savehandler, overworld.N().k(), dim, worldSettings, overworld, mcServer.a, overworld.Y()));
        world.a((acb)new jo(mcServer, world));
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load((abw)world));
        if (!mcServer.K()) {
            world.N().a(mcServer.h());
        }
        mcServer.c(mcServer.i());
    }
    
    public static js getWorld(final int id) {
        return DimensionManager.worlds.get(id);
    }
    
    public static js[] getWorlds() {
        return DimensionManager.worlds.values().toArray(new js[DimensionManager.worlds.size()]);
    }
    
    public static boolean shouldLoadSpawn(final int dim) {
        final int id = getProviderType(dim);
        return DimensionManager.spawnSettings.containsKey(id) && DimensionManager.spawnSettings.get(id);
    }
    
    public static Integer[] getStaticDimensionIDs() {
        return DimensionManager.dimensions.keySet().toArray(new Integer[DimensionManager.dimensions.keySet().size()]);
    }
    
    public static aei createProviderFor(final int dim) {
        try {
            if (DimensionManager.dimensions.containsKey(dim)) {
                final aei provider = DimensionManager.providers.get(getProviderType(dim)).newInstance();
                provider.setDimension(dim);
                return provider;
            }
            throw new RuntimeException(String.format("No WorldProvider bound for dimension %d", dim));
        }
        catch (final Exception e) {
            FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, String.format("An error occured trying to create an instance of WorldProvider %d (%s)", dim, DimensionManager.providers.get(getProviderType(dim)).getSimpleName()), e);
            throw new RuntimeException(e);
        }
    }
    
    public static void unloadWorld(final int id) {
        DimensionManager.unloadQueue.add(id);
    }
    
    public static void unloadWorlds(final Hashtable<Integer, long[]> worldTickTimes) {
        for (final int id : DimensionManager.unloadQueue) {
            final js w = DimensionManager.worlds.get(id);
            try {
                if (w != null) {
                    w.a(true, (lx)null);
                }
                else {
                    FMLLog.warning("Unexpected world unload - world %d is already unloaded", id);
                }
            }
            catch (final aca e) {
                e.printStackTrace();
            }
            finally {
                if (w != null) {
                    MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload((abw)w));
                    w.n();
                    setWorld(id, null);
                }
            }
        }
        DimensionManager.unloadQueue.clear();
    }
    
    public static int getNextFreeDimId() {
        int next = 0;
        while (true) {
            next = DimensionManager.dimensionMap.nextClearBit(next);
            if (!DimensionManager.dimensions.containsKey(next)) {
                break;
            }
            DimensionManager.dimensionMap.set(next);
        }
        return next;
    }
    
    public static by saveDimensionDataMap() {
        final int[] data = new int[(DimensionManager.dimensionMap.length() + 32 - 1) / 32];
        final by dimMap = new by();
        for (int i = 0; i < data.length; ++i) {
            int val = 0;
            for (int j = 0; j < 32; ++j) {
                val |= (DimensionManager.dimensionMap.get(i * 32 + j) ? (1 << j) : 0);
            }
            data[i] = val;
        }
        dimMap.a("DimensionArray", data);
        return dimMap;
    }
    
    public static void loadDimensionDataMap(final by compoundTag) {
        if (compoundTag == null) {
            DimensionManager.dimensionMap.clear();
            for (final Integer id : DimensionManager.dimensions.keySet()) {
                if (id >= 0) {
                    DimensionManager.dimensionMap.set(id);
                }
            }
        }
        else {
            final int[] intArray = compoundTag.k("DimensionArray");
            for (int i = 0; i < intArray.length; ++i) {
                for (int j = 0; j < 32; ++j) {
                    DimensionManager.dimensionMap.set(i * 32 + j, (intArray[i] & 1 << j) != 0x0);
                }
            }
        }
    }
    
    public static File getCurrentSaveRootDirectory() {
        if (getWorld(0) != null) {
            return ((alq)getWorld(0).M()).b();
        }
        if (MinecraftServer.F() != null) {
            final MinecraftServer srv = MinecraftServer.F();
            final alq saveHandler = (alq)srv.P().a(srv.L(), false);
            return saveHandler.b();
        }
        return null;
    }
    
    static {
        DimensionManager.providers = new Hashtable<Integer, Class<? extends aei>>();
        DimensionManager.spawnSettings = new Hashtable<Integer, Boolean>();
        DimensionManager.worlds = new Hashtable<Integer, js>();
        DimensionManager.hasInit = false;
        DimensionManager.dimensions = new Hashtable<Integer, Integer>();
        DimensionManager.unloadQueue = new ArrayList<Integer>();
        DimensionManager.dimensionMap = new BitSet(1024);
        DimensionManager.weakWorldMap = new MapMaker().weakKeys().weakValues().makeMap();
        DimensionManager.leakedWorlds = (Multiset<Integer>)HashMultiset.create();
        init();
    }
}
