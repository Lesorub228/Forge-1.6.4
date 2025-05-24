// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.client.FMLFolderResourcePack;
import net.minecraftforge.classloading.FMLForgePlugin;
import java.util.Map;
import net.minecraftforge.server.command.ForgeCommand;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.oredict.RecipeSorter;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import com.google.common.eventbus.Subscribe;
import java.util.logging.Level;
import cpw.mods.fml.common.network.NetworkModHandler;
import net.minecraftforge.common.network.ForgeNetworkHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.LoadController;
import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.FMLLog;
import java.io.File;
import cpw.mods.fml.common.Loader;
import java.util.Arrays;
import cpw.mods.fml.common.ModMetadata;
import net.minecraftforge.common.network.ForgeTinyPacketHandler;
import net.minecraftforge.common.network.ForgePacketHandler;
import net.minecraftforge.common.network.ForgeConnectionHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.WorldAccessContainer;
import cpw.mods.fml.common.DummyModContainer;

@NetworkMod(channels = { "FORGE" }, connectionHandler = ForgeConnectionHandler.class, packetHandler = ForgePacketHandler.class, tinyPacketHandler = ForgeTinyPacketHandler.class)
public class ForgeDummyContainer extends DummyModContainer implements WorldAccessContainer
{
    public static int clumpingThreshold;
    public static boolean removeErroringEntities;
    public static boolean removeErroringTileEntities;
    public static boolean disableStitchedFileSaving;
    public static boolean forceDuplicateFluidBlockCrash;
    public static boolean fullBoundingBoxLadders;
    public static double zombieSummonBaseChance;
    public static int[] blendRanges;
    public static float zombieBabyChance;
    public static boolean shouldSortRecipies;
    
    public ForgeDummyContainer() {
        super(new ModMetadata());
        final ModMetadata meta = this.getMetadata();
        meta.modId = "Forge";
        meta.name = "Minecraft Forge";
        meta.version = String.format("%d.%d.%d.%d", 9, 11, 1, 965);
        meta.credits = "Made possible with help from many people";
        meta.authorList = Arrays.asList("LexManos", "Eloraam", "Spacetoad");
        meta.description = "Minecraft Forge is a common open source API allowing a broad range of mods to work cooperatively together. It allows many mods to be created without them editing the main Minecraft code.";
        meta.url = "http://MinecraftForge.net";
        meta.updateUrl = "http://MinecraftForge.net/forum/index.php/topic,5.0.html";
        meta.screenshots = new String[0];
        meta.logoFile = "/forge_logo.png";
        Configuration config = null;
        final File cfgFile = new File(Loader.instance().getConfigDir(), "forge.cfg");
        try {
            config = new Configuration(cfgFile);
        }
        catch (final Exception e) {
            System.out.println("Error loading forge.cfg, deleting file and resetting: ");
            e.printStackTrace();
            if (cfgFile.exists()) {
                cfgFile.delete();
            }
            config = new Configuration(cfgFile);
        }
        if (!config.isChild) {
            config.load();
            final Property enableGlobalCfg = config.get("general", "enableGlobalConfig", false);
            if (enableGlobalCfg.getBoolean(false)) {
                Configuration.enableGlobalConfig();
            }
        }
        Property prop = config.get("general", "clumpingThreshold", 64);
        prop.comment = "Controls the number threshold at which Packet51 is preferred over Packet52, default and minimum 64, maximum 1024";
        ForgeDummyContainer.clumpingThreshold = prop.getInt(64);
        if (ForgeDummyContainer.clumpingThreshold > 1024 || ForgeDummyContainer.clumpingThreshold < 64) {
            prop.set(ForgeDummyContainer.clumpingThreshold = 64);
        }
        prop = config.get("general", "removeErroringEntities", false);
        prop.comment = "Set this to just remove any TileEntity that throws a error in there update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.";
        ForgeDummyContainer.removeErroringEntities = prop.getBoolean(false);
        if (ForgeDummyContainer.removeErroringEntities) {
            FMLLog.warning("Enabling removal of erroring Entities - USE AT YOUR OWN RISK", new Object[0]);
        }
        prop = config.get("general", "removeErroringTileEntities", false);
        prop.comment = "Set this to just remove any TileEntity that throws a error in there update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.";
        ForgeDummyContainer.removeErroringTileEntities = prop.getBoolean(false);
        if (ForgeDummyContainer.removeErroringTileEntities) {
            FMLLog.warning("Enabling removal of erroring Tile Entities - USE AT YOUR OWN RISK", new Object[0]);
        }
        prop = config.get("general", "fullBoundingBoxLadders", false);
        prop.comment = "Set this to check the entire entity's collision bounding box for ladders instead of just the block they are in. Causes noticable differences in mechanics so default is vanilla behavior. Default: false";
        ForgeDummyContainer.fullBoundingBoxLadders = prop.getBoolean(false);
        prop = config.get("general", "forceDuplicateFluidBlockCrash", true);
        prop.comment = "Set this to force a crash if more than one block attempts to link back to the same Fluid. Enabled by default.";
        if (!(ForgeDummyContainer.forceDuplicateFluidBlockCrash = prop.getBoolean(true))) {
            FMLLog.warning("Disabling forced crashes on duplicate Fluid Blocks - USE AT YOUR OWN RISK", new Object[0]);
        }
        prop = config.get("general", "biomeSkyBlendRange", new int[] { 20, 15, 10, 5 });
        prop.comment = "Control the range of sky blending for colored skies in biomes.";
        ForgeDummyContainer.blendRanges = prop.getIntList();
        prop = config.get("general", "zombieBaseSummonChance", 0.1);
        prop.comment = "Base zombie summoning spawn chance. Allows changing the bonus zombie summoning mechanic.";
        ForgeDummyContainer.zombieSummonBaseChance = prop.getDouble(0.1);
        prop = config.get("general", "zombieBabyChance", 0.05);
        prop.comment = "Chance that a zombie (or subclass) is a baby. Allows changing the zombie spawning mechanic.";
        ForgeDummyContainer.zombieBabyChance = (float)prop.getDouble(0.05);
        prop = config.get("general", "sortRecipies", ForgeDummyContainer.shouldSortRecipies);
        prop.comment = "Set to true to enable the post initlization sorting of crafting recipes using Froge's sorter. May cause desyncing on conflicting recipies. ToDo: Set to true by default in 1.7";
        ForgeDummyContainer.shouldSortRecipies = prop.getBoolean(ForgeDummyContainer.shouldSortRecipies);
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    @Override
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        bus.register((Object)this);
        return true;
    }
    
    @Subscribe
    public void modConstruction(final FMLConstructionEvent evt) {
        FMLLog.info("Registering Forge Packet Handler", new Object[0]);
        try {
            FMLNetworkHandler.instance().registerNetworkMod(new ForgeNetworkHandler(this));
            FMLLog.info("Succeeded registering Forge Packet Handler", new Object[0]);
        }
        catch (final Exception e) {
            FMLLog.log(Level.SEVERE, e, "Failed to register packet handler for Forge", new Object[0]);
        }
    }
    
    @Subscribe
    public void preInit(final FMLPreInitializationEvent evt) {
        ForgeChunkManager.captureConfig(evt.getModConfigurationDirectory());
    }
    
    @Subscribe
    public void postInit(final FMLPostInitializationEvent evt) {
        BiomeDictionary.registerAllBiomesAndGenerateEvents();
        ForgeChunkManager.loadConfiguration();
    }
    
    @Subscribe
    public void onAvalible(final FMLLoadCompleteEvent evt) {
        if (ForgeDummyContainer.shouldSortRecipies) {
            RecipeSorter.sortCraftManager();
        }
    }
    
    @Subscribe
    public void serverStarting(final FMLServerStartingEvent evt) {
        evt.registerServerCommand((ab)new ForgeCommand(evt.getServer()));
    }
    
    @Override
    public by getDataForWriting(final alq handler, final als info) {
        final by forgeData = new by();
        final by dimData = DimensionManager.saveDimensionDataMap();
        forgeData.a("DimensionData", dimData);
        return forgeData;
    }
    
    @Override
    public void readData(final alq handler, final als info, final Map<String, cl> propertyMap, final by tag) {
        if (tag.b("DimensionData")) {
            DimensionManager.loadDimensionDataMap(tag.b("DimensionData") ? tag.l("DimensionData") : null);
        }
    }
    
    @Override
    public File getSource() {
        return FMLForgePlugin.forgeLocation;
    }
    
    @Override
    public Class<?> getCustomResourcePackClass() {
        if (this.getSource().isDirectory()) {
            return FMLFolderResourcePack.class;
        }
        return FMLFileResourcePack.class;
    }
    
    static {
        ForgeDummyContainer.clumpingThreshold = 64;
        ForgeDummyContainer.removeErroringEntities = false;
        ForgeDummyContainer.removeErroringTileEntities = false;
        ForgeDummyContainer.disableStitchedFileSaving = false;
        ForgeDummyContainer.forceDuplicateFluidBlockCrash = true;
        ForgeDummyContainer.fullBoundingBoxLadders = false;
        ForgeDummyContainer.zombieSummonBaseChance = 0.1;
        ForgeDummyContainer.blendRanges = new int[] { 20, 15, 10, 5 };
        ForgeDummyContainer.zombieBabyChance = 0.05f;
        ForgeDummyContainer.shouldSortRecipies = false;
    }
}
