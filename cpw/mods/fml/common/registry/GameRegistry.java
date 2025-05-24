// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ArrayListMultimap;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import java.util.Map;
import java.lang.reflect.Constructor;
import cpw.mods.fml.common.LoaderException;
import java.util.logging.Level;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import java.util.Iterator;
import java.util.Random;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IFuelHandler;
import java.util.List;
import cpw.mods.fml.common.IWorldGenerator;
import java.util.Set;
import cpw.mods.fml.common.ModContainer;
import com.google.common.collect.Multimap;

public class GameRegistry
{
    private static Multimap<ModContainer, BlockProxy> blockRegistry;
    private static Set<IWorldGenerator> worldGenerators;
    private static List<IFuelHandler> fuelHandlers;
    private static List<ICraftingHandler> craftingHandlers;
    private static List<IPickupNotifier> pickupHandlers;
    private static List<IPlayerTracker> playerTrackers;
    
    public static void registerWorldGenerator(final IWorldGenerator generator) {
        GameRegistry.worldGenerators.add(generator);
    }
    
    public static void generateWorld(final int chunkX, final int chunkZ, final abw world, final ado chunkGenerator, final ado chunkProvider) {
        final long worldSeed = world.H();
        final Random fmlRandom = new Random(worldSeed);
        final long xSeed = fmlRandom.nextLong() >> 3;
        final long zSeed = fmlRandom.nextLong() >> 3;
        final long chunkSeed = xSeed * chunkX + zSeed * chunkZ ^ worldSeed;
        for (final IWorldGenerator generator : GameRegistry.worldGenerators) {
            fmlRandom.setSeed(chunkSeed);
            generator.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }
    
    public static Object buildBlock(final ModContainer container, final Class<?> type, final Mod.Block annotation) throws Exception {
        final Object o = type.getConstructor(Integer.TYPE).newInstance(findSpareBlockId());
        registerBlock((aqz)o);
        return o;
    }
    
    private static int findSpareBlockId() {
        return BlockTracker.nextBlockId();
    }
    
    public static void registerItem(final yc item, final String name) {
        registerItem(item, name, null);
    }
    
    public static void registerItem(final yc item, final String name, final String modId) {
        GameData.setName(item, name, modId);
    }
    
    @Deprecated
    public static void registerBlock(final aqz block) {
        registerBlock(block, zh.class);
    }
    
    public static void registerBlock(final aqz block, final String name) {
        registerBlock(block, zh.class, name);
    }
    
    @Deprecated
    public static void registerBlock(final aqz block, final Class<? extends zh> itemclass) {
        registerBlock(block, itemclass, null);
    }
    
    public static void registerBlock(final aqz block, final Class<? extends zh> itemclass, final String name) {
        registerBlock(block, itemclass, name, null);
    }
    
    public static void registerBlock(final aqz block, final Class<? extends zh> itemclass, final String name, final String modId) {
        if (Loader.instance().isInState(LoaderState.CONSTRUCTING)) {
            FMLLog.warning("The mod %s is attempting to register a block whilst it it being constructed. This is bad modding practice - please use a proper mod lifecycle event.", Loader.instance().activeModContainer());
        }
        try {
            assert block != null : "registerBlock: block cannot be null";
            assert itemclass != null : "registerBlock: itemclass cannot be null";
            final int blockItemId = block.cF - 256;
            yc i;
            try {
                final Constructor<? extends zh> itemCtor = itemclass.getConstructor(Integer.TYPE);
                i = (yc)itemCtor.newInstance(blockItemId);
            }
            catch (final NoSuchMethodException e) {
                final Constructor<? extends zh> itemCtor = itemclass.getConstructor(Integer.TYPE, aqz.class);
                i = (yc)itemCtor.newInstance(blockItemId, block);
            }
            registerItem(i, name, modId);
        }
        catch (final Exception e2) {
            FMLLog.log(Level.SEVERE, e2, "Caught an exception during block registration", new Object[0]);
            throw new LoaderException(e2);
        }
        GameRegistry.blockRegistry.put((Object)Loader.instance().activeModContainer(), (Object)block);
    }
    
    public static void addRecipe(final ye output, final Object... params) {
        addShapedRecipe(output, params);
    }
    
    public static aah addShapedRecipe(final ye output, final Object... params) {
        return (aah)aaf.a().a(output, params);
    }
    
    public static void addShapelessRecipe(final ye output, final Object... params) {
        aaf.a().b(output, params);
    }
    
    public static void addRecipe(final aah recipe) {
        aaf.a().b().add(recipe);
    }
    
    public static void addSmelting(final int input, final ye output, final float xp) {
        aab.a().a(input, output, xp);
    }
    
    public static void registerTileEntity(final Class<? extends asp> tileEntityClass, final String id) {
        asp.a((Class)tileEntityClass, id);
    }
    
    public static void registerTileEntityWithAlternatives(final Class<? extends asp> tileEntityClass, final String id, final String... alternatives) {
        asp.a((Class)tileEntityClass, id);
        final Map<String, Class> teMappings = ObfuscationReflectionHelper.getPrivateValue((Class<? super Object>)asp.class, (Object)null, "field_70326_a", "nameToClassMap", "a");
        for (final String s : alternatives) {
            if (!teMappings.containsKey(s)) {
                teMappings.put(s, tileEntityClass);
            }
        }
    }
    
    public static void addBiome(final acq biome) {
        acg.b.addNewBiome(biome);
    }
    
    public static void removeBiome(final acq biome) {
        acg.b.removeBiome(biome);
    }
    
    public static void registerFuelHandler(final IFuelHandler handler) {
        GameRegistry.fuelHandlers.add(handler);
    }
    
    public static int getFuelValue(final ye itemStack) {
        int fuelValue = 0;
        for (final IFuelHandler handler : GameRegistry.fuelHandlers) {
            fuelValue = Math.max(fuelValue, handler.getBurnTime(itemStack));
        }
        return fuelValue;
    }
    
    public static void registerCraftingHandler(final ICraftingHandler handler) {
        GameRegistry.craftingHandlers.add(handler);
    }
    
    public static void onItemCrafted(final uf player, final ye item, final mo craftMatrix) {
        for (final ICraftingHandler handler : GameRegistry.craftingHandlers) {
            handler.onCrafting(player, item, craftMatrix);
        }
    }
    
    public static void onItemSmelted(final uf player, final ye item) {
        for (final ICraftingHandler handler : GameRegistry.craftingHandlers) {
            handler.onSmelting(player, item);
        }
    }
    
    public static void registerPickupHandler(final IPickupNotifier handler) {
        GameRegistry.pickupHandlers.add(handler);
    }
    
    public static void onPickupNotification(final uf player, final ss item) {
        for (final IPickupNotifier notify : GameRegistry.pickupHandlers) {
            notify.notifyPickup(item, player);
        }
    }
    
    public static void registerPlayerTracker(final IPlayerTracker tracker) {
        GameRegistry.playerTrackers.add(tracker);
    }
    
    public static void onPlayerLogin(final uf player) {
        for (final IPlayerTracker tracker : GameRegistry.playerTrackers) {
            try {
                tracker.onPlayerLogin(player);
            }
            catch (final Exception e) {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerLogin event with player tracker %s", tracker.getClass().getName());
            }
        }
    }
    
    public static void onPlayerLogout(final uf player) {
        for (final IPlayerTracker tracker : GameRegistry.playerTrackers) {
            try {
                tracker.onPlayerLogout(player);
            }
            catch (final Exception e) {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerLogout event with player tracker %s", tracker.getClass().getName());
            }
        }
    }
    
    public static void onPlayerChangedDimension(final uf player) {
        for (final IPlayerTracker tracker : GameRegistry.playerTrackers) {
            try {
                tracker.onPlayerChangedDimension(player);
            }
            catch (final Exception e) {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerChangedDimension event with player tracker %s", tracker.getClass().getName());
            }
        }
    }
    
    public static void onPlayerRespawn(final uf player) {
        for (final IPlayerTracker tracker : GameRegistry.playerTrackers) {
            try {
                tracker.onPlayerRespawn(player);
            }
            catch (final Exception e) {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerRespawn event with player tracker %s", tracker.getClass().getName());
            }
        }
    }
    
    public static aqz findBlock(final String modId, final String name) {
        return GameData.findBlock(modId, name);
    }
    
    public static yc findItem(final String modId, final String name) {
        return GameData.findItem(modId, name);
    }
    
    public static void registerCustomItemStack(final String name, final ye itemStack) {
        GameData.registerCustomItemStack(name, itemStack);
    }
    
    public static ye findItemStack(final String modId, final String name, final int stackSize) {
        final ye foundStack = GameData.findItemStack(modId, name);
        if (foundStack != null) {
            final ye is = foundStack.m();
            is.b = Math.min(stackSize, is.e());
            return is;
        }
        return null;
    }
    
    public static UniqueIdentifier findUniqueIdentifierFor(final aqz block) {
        return GameData.getUniqueName(block);
    }
    
    public static UniqueIdentifier findUniqueIdentifierFor(final yc item) {
        return GameData.getUniqueName(item);
    }
    
    static {
        GameRegistry.blockRegistry = (Multimap<ModContainer, BlockProxy>)ArrayListMultimap.create();
        GameRegistry.worldGenerators = Sets.newHashSet();
        GameRegistry.fuelHandlers = Lists.newArrayList();
        GameRegistry.craftingHandlers = Lists.newArrayList();
        GameRegistry.pickupHandlers = Lists.newArrayList();
        GameRegistry.playerTrackers = Lists.newArrayList();
    }
    
    public static class UniqueIdentifier
    {
        public final String modId;
        public final String name;
        
        UniqueIdentifier(final String modId, final String name) {
            this.modId = modId;
            this.name = name;
        }
    }
}
