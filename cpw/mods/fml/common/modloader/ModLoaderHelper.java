// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import com.google.common.base.Function;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.network.IPacketHandler;
import java.util.EnumSet;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import java.util.Map;

public class ModLoaderHelper
{
    public static IModLoaderSidedHelper sidedHelper;
    private static Map<BaseModProxy, ModLoaderGuiHelper> guiHelpers;
    private static Map<Integer, ModLoaderGuiHelper> guiIDs;
    private static ModLoaderVillageTradeHandler[] tradeHelpers;
    
    public static void updateStandardTicks(final BaseModProxy mod, final boolean enable, final boolean useClock) {
        ModLoaderModContainer mlmc = (ModLoaderModContainer)Loader.instance().getReversedModObjectList().get((Object)mod);
        if (mlmc == null) {
            mlmc = (ModLoaderModContainer)Loader.instance().activeModContainer();
        }
        if (mlmc == null) {
            FMLLog.severe("Attempted to register ModLoader ticking for invalid BaseMod %s", mod);
            return;
        }
        final BaseModTicker ticker = mlmc.getGameTickHandler();
        final EnumSet<TickType> ticks = ticker.ticks();
        if (enable && !useClock) {
            ticks.add(TickType.RENDER);
        }
        else {
            ticks.remove(TickType.RENDER);
        }
        if (enable && (useClock || FMLCommonHandler.instance().getSide().isServer())) {
            ticks.add(TickType.CLIENT);
            ticks.add(TickType.WORLDLOAD);
        }
        else {
            ticks.remove(TickType.CLIENT);
            ticks.remove(TickType.WORLDLOAD);
        }
    }
    
    public static void updateGUITicks(final BaseModProxy mod, final boolean enable, final boolean useClock) {
        ModLoaderModContainer mlmc = (ModLoaderModContainer)Loader.instance().getReversedModObjectList().get((Object)mod);
        if (mlmc == null) {
            mlmc = (ModLoaderModContainer)Loader.instance().activeModContainer();
        }
        if (mlmc == null) {
            FMLLog.severe("Attempted to register ModLoader ticking for invalid BaseMod %s", mod);
            return;
        }
        final EnumSet<TickType> ticks = mlmc.getGUITickHandler().ticks();
        if (enable && !useClock) {
            ticks.add(TickType.RENDER);
        }
        else {
            ticks.remove(TickType.RENDER);
        }
        if (enable && useClock) {
            ticks.add(TickType.CLIENT);
            ticks.add(TickType.WORLDLOAD);
        }
        else {
            ticks.remove(TickType.CLIENT);
            ticks.remove(TickType.WORLDLOAD);
        }
    }
    
    public static IPacketHandler buildPacketHandlerFor(final BaseModProxy mod) {
        return new ModLoaderPacketHandler(mod);
    }
    
    public static IWorldGenerator buildWorldGenHelper(final BaseModProxy mod) {
        return new ModLoaderWorldGenerator(mod);
    }
    
    public static IFuelHandler buildFuelHelper(final BaseModProxy mod) {
        return new ModLoaderFuelHelper(mod);
    }
    
    public static ICraftingHandler buildCraftingHelper(final BaseModProxy mod) {
        return new ModLoaderCraftingHelper(mod);
    }
    
    public static void finishModLoading(final ModLoaderModContainer mc) {
        if (ModLoaderHelper.sidedHelper != null) {
            ModLoaderHelper.sidedHelper.finishModLoading(mc);
        }
    }
    
    public static IConnectionHandler buildConnectionHelper(final BaseModProxy mod) {
        return new ModLoaderConnectionHandler(mod);
    }
    
    public static IPickupNotifier buildPickupHelper(final BaseModProxy mod) {
        return new ModLoaderPickupNotifier(mod);
    }
    
    public static void buildGuiHelper(final BaseModProxy mod, final int id) {
        ModLoaderGuiHelper handler = ModLoaderHelper.guiHelpers.get(mod);
        if (handler == null) {
            handler = new ModLoaderGuiHelper(mod);
            ModLoaderHelper.guiHelpers.put(mod, handler);
            NetworkRegistry.instance().registerGuiHandler(mod, handler);
        }
        handler.associateId(id);
        ModLoaderHelper.guiIDs.put(id, handler);
    }
    
    public static void openGui(final int id, final uf player, final uy container, final int x, final int y, final int z) {
        final ModLoaderGuiHelper helper = ModLoaderHelper.guiIDs.get(id);
        helper.injectContainerAndID(container, id);
        player.openGui(helper.getMod(), id, player.q, x, y, z);
    }
    
    public static Object getClientSideGui(final BaseModProxy mod, final uf player, final int ID, final int x, final int y, final int z) {
        if (ModLoaderHelper.sidedHelper != null) {
            return ModLoaderHelper.sidedHelper.getClientGui(mod, player, ID, x, y, z);
        }
        return null;
    }
    
    public static void buildEntityTracker(final BaseModProxy mod, final Class<? extends nn> entityClass, final int entityTypeId, final int updateRange, final int updateInterval, final boolean sendVelocityInfo) {
        final EntityRegistry.EntityRegistration er = EntityRegistry.registerModLoaderEntity(mod, entityClass, entityTypeId, updateRange, updateInterval, sendVelocityInfo);
        er.setCustomSpawning((Function<EntitySpawnPacket, nn>)new ModLoaderEntitySpawnCallback(mod, er), sk.class.isAssignableFrom(entityClass) || nl.class.isAssignableFrom(entityClass));
    }
    
    public static void registerTrade(final int profession, final TradeEntry entry) {
        assert profession < ModLoaderHelper.tradeHelpers.length : "The profession is out of bounds";
        if (ModLoaderHelper.tradeHelpers[profession] == null) {
            ModLoaderHelper.tradeHelpers[profession] = new ModLoaderVillageTradeHandler();
            VillagerRegistry.instance().registerVillageTradeHandler(profession, ModLoaderHelper.tradeHelpers[profession]);
        }
        ModLoaderHelper.tradeHelpers[profession].addTrade(entry);
    }
    
    public static void addCommand(final ab command) {
        final ModLoaderModContainer mlmc = (ModLoaderModContainer)Loader.instance().activeModContainer();
        if (mlmc != null) {
            mlmc.addServerCommand(command);
        }
    }
    
    public static IChatListener buildChatListener(final BaseModProxy mod) {
        return new ModLoaderChatListener(mod);
    }
    
    static {
        ModLoaderHelper.guiHelpers = Maps.newHashMap();
        ModLoaderHelper.guiIDs = Maps.newHashMap();
        ModLoaderHelper.tradeHelpers = new ModLoaderVillageTradeHandler[6];
    }
}
