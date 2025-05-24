// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.registry;

import com.google.common.collect.HashBasedTable;
import java.io.IOException;
import com.google.common.io.Files;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Tables;
import com.google.common.collect.Sets;
import java.util.Iterator;
import com.google.common.base.Function;
import java.util.Set;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.LoaderState;
import com.google.common.collect.ImmutableMap;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import com.google.common.collect.Maps;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import cpw.mods.fml.common.Loader;
import com.google.common.collect.Table;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.MapDifference;
import java.util.concurrent.CountDownLatch;
import java.util.Map;

public class GameData
{
    private static Map<Integer, ItemData> idMap;
    private static CountDownLatch serverValidationLatch;
    private static CountDownLatch clientValidationLatch;
    private static MapDifference<Integer, ItemData> difference;
    private static boolean shouldContinue;
    private static boolean isSaveValid;
    private static ImmutableTable<String, String, Integer> modObjectTable;
    private static Table<String, String, ye> customItemStacks;
    private static Map<String, String> ignoredMods;
    private static boolean validated;
    
    private static boolean isModIgnoredForIdValidation(final String modId) {
        if (GameData.ignoredMods == null) {
            final File f = new File(Loader.instance().getConfigDir(), "fmlIDChecking.properties");
            if (f.exists()) {
                final Properties p = new Properties();
                try {
                    p.load(new FileInputStream(f));
                    GameData.ignoredMods = (Map<String, String>)Maps.fromProperties(p);
                    if (GameData.ignoredMods.size() > 0) {
                        FMLLog.log("fml.ItemTracker", Level.WARNING, "Using non-empty ignored mods configuration file %s", GameData.ignoredMods.keySet());
                    }
                }
                catch (final Exception e) {
                    Throwables.propagateIfPossible((Throwable)e);
                    FMLLog.log("fml.ItemTracker", Level.SEVERE, e, "Failed to read ignored ID checker mods properties file", new Object[0]);
                    GameData.ignoredMods = (Map<String, String>)ImmutableMap.of();
                }
            }
            else {
                GameData.ignoredMods = (Map<String, String>)ImmutableMap.of();
            }
        }
        return GameData.ignoredMods.containsKey(modId);
    }
    
    public static void newItemAdded(final yc item) {
        ModContainer mc = Loader.instance().activeModContainer();
        if (mc == null) {
            mc = Loader.instance().getMinecraftModContainer();
            if (Loader.instance().hasReachedState(LoaderState.INITIALIZATION) || GameData.validated) {
                FMLLog.severe("It appears something has tried to allocate an Item or Block outside of the preinitialization phase for mods. This will NOT work in 1.7 and beyond!", new Object[0]);
            }
        }
        final String itemType = item.getClass().getName();
        final ItemData itemData = new ItemData(item, mc);
        if (GameData.idMap.containsKey(item.cv)) {
            final ItemData id = GameData.idMap.get(item.cv);
            FMLLog.log("fml.ItemTracker", Level.INFO, "The mod %s is overwriting existing item at %d (%s from %s) with %s", mc.getModId(), id.getItemId(), id.getItemType(), id.getModId(), itemType);
        }
        GameData.idMap.put(item.cv, itemData);
        if (!"Minecraft".equals(mc.getModId())) {
            FMLLog.log("fml.ItemTracker", Level.FINE, "Adding item %s(%d) owned by %s", item.getClass().getName(), item.cv, mc.getModId());
        }
    }
    
    public static void validateWorldSave(final Set<ItemData> worldSaveItems) {
        GameData.isSaveValid = true;
        GameData.shouldContinue = true;
        if (worldSaveItems == null) {
            GameData.serverValidationLatch.countDown();
            try {
                GameData.clientValidationLatch.await();
            }
            catch (final InterruptedException ex) {}
            return;
        }
        final Function<? super ItemData, Integer> idMapFunction = (Function<? super ItemData, Integer>)new Function<ItemData, Integer>() {
            public Integer apply(final ItemData input) {
                return input.getItemId();
            }
        };
        final Map<Integer, ItemData> worldMap = (Map<Integer, ItemData>)Maps.uniqueIndex((Iterable)worldSaveItems, (Function)idMapFunction);
        GameData.difference = (MapDifference<Integer, ItemData>)Maps.difference((Map)worldMap, (Map)GameData.idMap);
        FMLLog.log("fml.ItemTracker", Level.FINE, "The difference set is %s", GameData.difference);
        if (!GameData.difference.entriesDiffering().isEmpty() || !GameData.difference.entriesOnlyOnLeft().isEmpty()) {
            FMLLog.log("fml.ItemTracker", Level.SEVERE, "FML has detected item discrepancies", new Object[0]);
            FMLLog.log("fml.ItemTracker", Level.SEVERE, "Missing items : %s", GameData.difference.entriesOnlyOnLeft());
            FMLLog.log("fml.ItemTracker", Level.SEVERE, "Mismatched items : %s", GameData.difference.entriesDiffering());
            boolean foundNonIgnored = false;
            for (final ItemData diff : GameData.difference.entriesOnlyOnLeft().values()) {
                if (!isModIgnoredForIdValidation(diff.getModId())) {
                    foundNonIgnored = true;
                }
            }
            for (final MapDifference.ValueDifference<ItemData> diff2 : GameData.difference.entriesDiffering().values()) {
                if (!isModIgnoredForIdValidation(((ItemData)diff2.leftValue()).getModId()) && !isModIgnoredForIdValidation(((ItemData)diff2.rightValue()).getModId())) {
                    foundNonIgnored = true;
                }
            }
            if (!foundNonIgnored) {
                FMLLog.log("fml.ItemTracker", Level.SEVERE, "FML is ignoring these ID discrepancies because of configuration. YOUR GAME WILL NOW PROBABLY CRASH. HOPEFULLY YOU WON'T HAVE CORRUPTED YOUR WORLD. BLAME %s", GameData.ignoredMods.keySet());
            }
            GameData.isSaveValid = !foundNonIgnored;
            GameData.serverValidationLatch.countDown();
        }
        else {
            GameData.isSaveValid = true;
            GameData.serverValidationLatch.countDown();
        }
        try {
            GameData.clientValidationLatch.await();
            if (!GameData.shouldContinue) {
                throw new RuntimeException("This server instance is going to stop abnormally because of a fatal ID mismatch");
            }
        }
        catch (final InterruptedException ex2) {}
    }
    
    public static void writeItemData(final cg itemList) {
        for (final ItemData dat : GameData.idMap.values()) {
            itemList.a((cl)dat.toNBT());
        }
    }
    
    public static void initializeServerGate(final int gateCount) {
        GameData.serverValidationLatch = new CountDownLatch(gateCount - 1);
        GameData.clientValidationLatch = new CountDownLatch(gateCount - 1);
    }
    
    public static MapDifference<Integer, ItemData> gateWorldLoadingForValidation() {
        try {
            GameData.serverValidationLatch.await();
            if (!GameData.isSaveValid) {
                return GameData.difference;
            }
        }
        catch (final InterruptedException ex) {}
        return GameData.difference = null;
    }
    
    public static void releaseGate(final boolean carryOn) {
        GameData.shouldContinue = carryOn;
        GameData.clientValidationLatch.countDown();
    }
    
    public static Set<ItemData> buildWorldItemData(final cg modList) {
        final Set<ItemData> worldSaveItems = Sets.newHashSet();
        for (int i = 0; i < modList.c(); ++i) {
            final by mod = (by)modList.b(i);
            final ItemData dat = new ItemData(mod);
            worldSaveItems.add(dat);
        }
        return worldSaveItems;
    }
    
    static void setName(final yc item, final String name, final String modId) {
        final int id = item.cv;
        final ItemData itemData = GameData.idMap.get(id);
        itemData.setName(name, modId);
    }
    
    public static void buildModObjectTable() {
        if (GameData.modObjectTable != null) {
            throw new IllegalStateException("Illegal call to buildModObjectTable!");
        }
        final Map<Integer, Table.Cell<String, String, Integer>> map = Maps.transformValues((Map)GameData.idMap, (Function)new Function<ItemData, Table.Cell<String, String, Integer>>() {
            public Table.Cell<String, String, Integer> apply(final ItemData data) {
                if ("Minecraft".equals(data.getModId()) || !data.isOveridden()) {
                    return null;
                }
                return (Table.Cell<String, String, Integer>)Tables.immutableCell((Object)data.getModId(), (Object)data.getItemType(), (Object)data.getItemId());
            }
        });
        final ImmutableTable.Builder<String, String, Integer> tBuilder = (ImmutableTable.Builder<String, String, Integer>)ImmutableTable.builder();
        for (final Table.Cell<String, String, Integer> c : map.values()) {
            if (c != null) {
                tBuilder.put((Table.Cell)c);
            }
        }
        GameData.modObjectTable = (ImmutableTable<String, String, Integer>)tBuilder.build();
    }
    
    static yc findItem(final String modId, final String name) {
        if (GameData.modObjectTable == null || !GameData.modObjectTable.contains((Object)modId, (Object)name)) {
            return null;
        }
        return yc.g[(int)GameData.modObjectTable.get((Object)modId, (Object)name)];
    }
    
    static aqz findBlock(final String modId, final String name) {
        if (GameData.modObjectTable == null) {
            return null;
        }
        final Integer blockId = (Integer)GameData.modObjectTable.get((Object)modId, (Object)name);
        if (blockId == null || blockId >= aqz.s.length) {
            return null;
        }
        return aqz.s[blockId];
    }
    
    static ye findItemStack(final String modId, final String name) {
        ye is = (ye)GameData.customItemStacks.get((Object)modId, (Object)name);
        if (is == null) {
            final yc i = findItem(modId, name);
            if (i != null) {
                is = new ye(i, 0, 0);
            }
        }
        if (is == null) {
            final aqz b = findBlock(modId, name);
            if (b != null) {
                is = new ye(b, 0, 32767);
            }
        }
        return is;
    }
    
    static void registerCustomItemStack(final String name, final ye itemStack) {
        GameData.customItemStacks.put((Object)Loader.instance().activeModContainer().getModId(), (Object)name, (Object)itemStack);
    }
    
    public static void dumpRegistry(final File minecraftDir) {
        if (GameData.customItemStacks == null) {
            return;
        }
        if (Boolean.valueOf(System.getProperty("fml.dumpRegistry", "false"))) {
            final ImmutableListMultimap.Builder<String, String> builder = (ImmutableListMultimap.Builder<String, String>)ImmutableListMultimap.builder();
            for (final String modId : GameData.customItemStacks.rowKeySet()) {
                builder.putAll((Object)modId, (Iterable)GameData.customItemStacks.row((Object)modId).keySet());
            }
            final File f = new File(minecraftDir, "itemStackRegistry.csv");
            final Joiner.MapJoiner mapJoiner = Joiner.on("\n").withKeyValueSeparator(",");
            try {
                Files.write((CharSequence)mapJoiner.join((Iterable)builder.build().entries()), f, Charsets.UTF_8);
                FMLLog.log(Level.INFO, "Dumped item registry data to %s", f.getAbsolutePath());
            }
            catch (final IOException e) {
                FMLLog.log(Level.SEVERE, e, "Failed to write registry data to %s", f.getAbsolutePath());
            }
        }
    }
    
    static GameRegistry.UniqueIdentifier getUniqueName(final aqz block) {
        if (block == null) {
            return null;
        }
        final ItemData itemData = GameData.idMap.get(block.cF);
        if (itemData == null || !itemData.isOveridden() || GameData.customItemStacks.contains((Object)itemData.getModId(), (Object)itemData.getItemType())) {
            return null;
        }
        return new GameRegistry.UniqueIdentifier(itemData.getModId(), itemData.getItemType());
    }
    
    static GameRegistry.UniqueIdentifier getUniqueName(final yc item) {
        if (item == null) {
            return null;
        }
        final ItemData itemData = GameData.idMap.get(item.cv);
        if (itemData == null || !itemData.isOveridden() || GameData.customItemStacks.contains((Object)itemData.getModId(), (Object)itemData.getItemType())) {
            return null;
        }
        return new GameRegistry.UniqueIdentifier(itemData.getModId(), itemData.getItemType());
    }
    
    public static void validateRegistry() {
        for (int i = 0; i < yc.g.length; ++i) {
            if (yc.g[i] != null) {
                final ItemData itemData = GameData.idMap.get(i);
                if (itemData == null) {
                    FMLLog.severe("Found completely unknown item of class %s with ID %d, this will NOT work for a 1.7 upgrade", yc.g[i].getClass().getName(), i);
                }
                else if (!itemData.isOveridden() && !"Minecraft".equals(itemData.getModId())) {
                    FMLLog.severe("Found anonymous item of class %s with ID %d owned by mod %s, this item will NOT survive a 1.7 upgrade!", yc.g[i].getClass().getName(), i, itemData.getModId());
                }
            }
        }
        GameData.validated = true;
    }
    
    static {
        GameData.idMap = Maps.newHashMap();
        GameData.shouldContinue = true;
        GameData.isSaveValid = true;
        GameData.customItemStacks = (Table<String, String, ye>)HashBasedTable.create();
    }
}
