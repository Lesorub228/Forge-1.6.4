import java.util.Collections;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.client.modloader.ModLoaderClientHelper;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Logger;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import java.util.List;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.modloader.BaseModProxy;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Map;

// 
// Decompiled by Procyon v0.6.0
// 

@Deprecated
public class ModLoader
{
    public static final String fmlMarker = "This is an FML marker";
    @Deprecated
    public static final Map<String, Map<String, String>> localizedStrings;
    
    @Deprecated
    public static void addAchievementDesc(final ko achievement, final String name, final String description) {
        final String achName = achievement.i();
        addLocalization(achName, name);
        addLocalization(achName + ".desc", description);
    }
    
    @Deprecated
    public static int addAllFuel(final int id, final int metadata) {
        return 0;
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void addAllRenderers(final Map<Class<? extends nn>, bgm> renderers) {
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static int addArmor(final String armor) {
        return RenderingRegistry.addNewArmourRendererPrefix(armor);
    }
    
    @Deprecated
    public static void addBiome(final acq biome) {
        GameRegistry.addBiome(biome);
    }
    
    @Deprecated
    public static void addEntityTracker(final BaseMod mod, final Class<? extends nn> entityClass, final int entityTypeId, final int updateRange, final int updateInterval, final boolean sendVelocityInfo) {
        ModLoaderHelper.buildEntityTracker(mod, entityClass, entityTypeId, updateRange, updateInterval, sendVelocityInfo);
    }
    
    @Deprecated
    public static void addCommand(final ab command) {
        ModLoaderHelper.addCommand(command);
    }
    
    @Deprecated
    public static void addDispenserBehavior(final yc item, final bj behavior) {
        any.a.a((Object)item, (Object)behavior);
    }
    
    @Deprecated
    public static void addLocalization(final String key, final String value) {
        addLocalization(key, "en_US", value);
    }
    
    @Deprecated
    public static void addLocalization(final String key, final String lang, final String value) {
        LanguageRegistry.instance().addStringLocalization(key, lang, value);
    }
    
    @Deprecated
    public static void addName(final Object instance, final String name) {
        addName(instance, "en_US", name);
    }
    
    @Deprecated
    public static void addName(final Object instance, final String lang, final String name) {
        LanguageRegistry.instance().addNameForObject(instance, lang, name);
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static int addOverride(final String fileToOverride, final String fileToAdd) {
        return RenderingRegistry.addTextureOverride(fileToOverride, fileToAdd);
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void addOverride(final String path, final String overlayPath, final int index) {
        RenderingRegistry.addTextureOverride(path, overlayPath, index);
    }
    
    @Deprecated
    public static void addRecipe(final ye output, final Object... params) {
        GameRegistry.addRecipe(output, params);
    }
    
    @Deprecated
    public static void addShapelessRecipe(final ye output, final Object... params) {
        GameRegistry.addShapelessRecipe(output, params);
    }
    
    @Deprecated
    public static void addSmelting(final int input, final ye output) {
        GameRegistry.addSmelting(input, output, 1.0f);
    }
    
    @Deprecated
    public static void addSmelting(final int input, final ye output, final float experience) {
        GameRegistry.addSmelting(input, output, experience);
    }
    
    @Deprecated
    public static void addSpawn(final Class<? extends og> entityClass, final int weightedProb, final int min, final int max, final oh spawnList) {
        EntityRegistry.addSpawn(entityClass, weightedProb, min, max, spawnList, acg.base12Biomes);
    }
    
    @Deprecated
    public static void addSpawn(final Class<? extends og> entityClass, final int weightedProb, final int min, final int max, final oh spawnList, final acq... biomes) {
        EntityRegistry.addSpawn(entityClass, weightedProb, min, max, spawnList, biomes);
    }
    
    @Deprecated
    public static void addSpawn(final String entityName, final int weightedProb, final int min, final int max, final oh spawnList) {
        EntityRegistry.addSpawn(entityName, weightedProb, min, max, spawnList, acg.base12Biomes);
    }
    
    @Deprecated
    public static void addSpawn(final String entityName, final int weightedProb, final int min, final int max, final oh spawnList, final acq... biomes) {
        EntityRegistry.addSpawn(entityName, weightedProb, min, max, spawnList, biomes);
    }
    
    @Deprecated
    public static void addTrade(final int profession, final TradeEntry entry) {
        ModLoaderHelper.registerTrade(profession, entry);
    }
    
    @Deprecated
    public static void clientSendPacket(final ey packet) {
        PacketDispatcher.sendPacketToServer(packet);
    }
    
    @Deprecated
    public static boolean dispenseEntity(final abw world, final double x, final double y, final double z, final int xVel, final int zVel, final ye item) {
        return false;
    }
    
    @Deprecated
    public static void genericContainerRemoval(final abw world, final int x, final int y, final int z) {
    }
    
    @Deprecated
    public static List<BaseMod> getLoadedMods() {
        return ModLoaderModContainer.findAll(BaseMod.class);
    }
    
    @Deprecated
    public static Logger getLogger() {
        return FMLLog.getLogger();
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public static atv getMinecraftInstance() {
        return FMLClientHandler.instance().getClient();
    }
    
    @Deprecated
    public static MinecraftServer getMinecraftServerInstance() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }
    
    @Deprecated
    public static <T, E> T getPrivateValue(final Class<? super E> instanceclass, final E instance, final int fieldindex) {
        return ObfuscationReflectionHelper.getPrivateValue(instanceclass, instance, fieldindex);
    }
    
    @Deprecated
    public static <T, E> T getPrivateValue(final Class<? super E> instanceclass, final E instance, final String field) {
        return ObfuscationReflectionHelper.getPrivateValue(instanceclass, instance, field);
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public static int getUniqueBlockModelID(final BaseMod mod, final boolean inventoryRenderer) {
        return ModLoaderClientHelper.obtainBlockModelIdFor(mod, inventoryRenderer);
    }
    
    @Deprecated
    public static int getUniqueEntityId() {
        return EntityRegistry.findGlobalUniqueEntityId();
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static int getUniqueSpriteIndex(final String path) {
        return -1;
    }
    
    @Deprecated
    public static boolean isChannelActive(final uf player, final String channel) {
        return NetworkRegistry.instance().isChannelActive(channel, (Player)player);
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public static boolean isGUIOpen(final Class<? extends awe> gui) {
        return FMLClientHandler.instance().isGUIOpen(gui);
    }
    
    @Deprecated
    public static boolean isModLoaded(final String modname) {
        return Loader.isModLoaded(modname);
    }
    
    @Deprecated
    public static void loadConfig() {
    }
    
    @Deprecated
    public static void onItemPickup(final uf player, final ye item) {
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void onTick(final float tick, final atv game) {
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void openGUI(final uf player, final awe gui) {
        FMLClientHandler.instance().displayGuiScreen(player, gui);
    }
    
    @Deprecated
    public static void populateChunk(final ado generator, final int chunkX, final int chunkZ, final abw world) {
    }
    
    @Deprecated
    public static void receivePacket(final ea packet) {
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static ats[] registerAllKeys(final ats[] keys) {
        return keys;
    }
    
    @Deprecated
    public static void registerBlock(final aqz block) {
        GameRegistry.registerBlock(block);
    }
    
    @Deprecated
    public static void registerBlock(final aqz block, final Class<? extends zh> itemclass) {
        GameRegistry.registerBlock(block, itemclass);
    }
    
    @Deprecated
    public static void registerContainerID(final BaseMod mod, final int id) {
        ModLoaderHelper.buildGuiHelper(mod, id);
    }
    
    @Deprecated
    public static void registerEntityID(final Class<? extends nn> entityClass, final String entityName, final int id) {
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
    }
    
    @Deprecated
    public static void registerEntityID(final Class<? extends nn> entityClass, final String entityName, final int id, final int background, final int foreground) {
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, id, background, foreground);
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public static void registerKey(final BaseMod mod, final ats keyHandler, final boolean allowRepeat) {
        ModLoaderClientHelper.registerKeyBinding(mod, keyHandler, allowRepeat);
    }
    
    @Deprecated
    public static void registerPacketChannel(final BaseMod mod, final String channel) {
        NetworkRegistry.instance().registerChannel(ModLoaderHelper.buildPacketHandlerFor(mod), channel);
    }
    
    @Deprecated
    public static void registerTileEntity(final Class<? extends asp> tileEntityClass, final String id) {
        GameRegistry.registerTileEntity(tileEntityClass, id);
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public static void registerTileEntity(final Class<? extends asp> tileEntityClass, final String id, final bje renderer) {
        ClientRegistry.registerTileEntity(tileEntityClass, id, renderer);
    }
    
    @Deprecated
    public static void removeBiome(final acq biome) {
        GameRegistry.removeBiome(biome);
    }
    
    @Deprecated
    public static void removeSpawn(final Class<? extends og> entityClass, final oh spawnList) {
        EntityRegistry.removeSpawn(entityClass, spawnList, acg.base12Biomes);
    }
    
    @Deprecated
    public static void removeSpawn(final Class<? extends og> entityClass, final oh spawnList, final acq... biomes) {
        EntityRegistry.removeSpawn(entityClass, spawnList, biomes);
    }
    
    @Deprecated
    public static void removeSpawn(final String entityName, final oh spawnList) {
        EntityRegistry.removeSpawn(entityName, spawnList, acg.base12Biomes);
    }
    
    @Deprecated
    public static void removeSpawn(final String entityName, final oh spawnList, final acq... biomes) {
        EntityRegistry.removeSpawn(entityName, spawnList, biomes);
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static boolean renderBlockIsItemFull3D(final int modelID) {
        return RenderingRegistry.instance().renderItemAsFull3DBlock(modelID);
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void renderInvBlock(final bfr renderer, final aqz block, final int metadata, final int modelID) {
        RenderingRegistry.instance().renderInventoryBlock(renderer, block, metadata, modelID);
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static boolean renderWorldBlock(final bfr renderer, final acf world, final int x, final int y, final int z, final aqz block, final int modelID) {
        return RenderingRegistry.instance().renderWorldBlock(renderer, world, x, y, z, block, modelID);
    }
    
    @Deprecated
    public static void saveConfig() {
    }
    
    @Deprecated
    public static void sendPacket(final ey packet) {
        PacketDispatcher.sendPacketToServer(packet);
    }
    
    @Deprecated
    public static void serverChat(final String text) {
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void serverLogin(final bcw handler, final ep loginPacket) {
    }
    
    @Deprecated
    public static void serverSendPacket(final ka handler, final ey packet) {
        if (handler != null) {
            PacketDispatcher.sendPacketToPlayer(packet, (Player)handler.getPlayer());
        }
    }
    
    @Deprecated
    public static void serverOpenWindow(final jv player, final uy container, final int ID, final int x, final int y, final int z) {
        ModLoaderHelper.openGui(ID, (uf)player, container, x, y, z);
    }
    
    @Deprecated
    public static void setInGameHook(final BaseMod mod, final boolean enable, final boolean useClock) {
        ModLoaderHelper.updateStandardTicks(mod, enable, useClock);
    }
    
    @Deprecated
    public static void setInGUIHook(final BaseMod mod, final boolean enable, final boolean useClock) {
        ModLoaderHelper.updateGUITicks(mod, enable, useClock);
    }
    
    @Deprecated
    public static <T, E> void setPrivateValue(final Class<? super T> instanceclass, final T instance, final int fieldindex, final E value) {
        ObfuscationReflectionHelper.setPrivateValue(instanceclass, instance, value, fieldindex);
    }
    
    @Deprecated
    public static <T, E> void setPrivateValue(final Class<? super T> instanceclass, final T instance, final String field, final E value) {
        ObfuscationReflectionHelper.setPrivateValue(instanceclass, instance, value, field);
    }
    
    @Deprecated
    public static void takenFromCrafting(final uf player, final ye item, final mo matrix) {
    }
    
    @Deprecated
    public static void takenFromFurnace(final uf player, final ye item) {
    }
    
    @Deprecated
    public static void throwException(final String message, final Throwable e) {
        FMLCommonHandler.instance().raiseException(e, message, true);
    }
    
    @Deprecated
    public static void throwException(final Throwable e) {
        throwException("Exception in ModLoader", e);
    }
    
    static {
        localizedStrings = Collections.emptyMap();
    }
}
