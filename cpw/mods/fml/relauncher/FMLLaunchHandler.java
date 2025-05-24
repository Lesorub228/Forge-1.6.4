// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.relauncher;

import com.google.common.base.Throwables;
import java.util.logging.Level;
import java.io.File;
import cpw.mods.fml.common.launcher.FMLTweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class FMLLaunchHandler
{
    private static FMLLaunchHandler INSTANCE;
    static Side side;
    private LaunchClassLoader classLoader;
    private FMLTweaker tweaker;
    private File minecraftHome;
    
    public static void configureForClientLaunch(final LaunchClassLoader loader, final FMLTweaker tweaker) {
        instance(loader, tweaker).setupClient();
    }
    
    public static void configureForServerLaunch(final LaunchClassLoader loader, final FMLTweaker tweaker) {
        instance(loader, tweaker).setupServer();
    }
    
    private static FMLLaunchHandler instance(final LaunchClassLoader launchLoader, final FMLTweaker tweaker) {
        if (FMLLaunchHandler.INSTANCE == null) {
            FMLLaunchHandler.INSTANCE = new FMLLaunchHandler(launchLoader, tweaker);
        }
        return FMLLaunchHandler.INSTANCE;
    }
    
    private FMLLaunchHandler(final LaunchClassLoader launchLoader, final FMLTweaker tweaker) {
        this.classLoader = launchLoader;
        this.tweaker = tweaker;
        this.minecraftHome = tweaker.getGameDir();
        this.classLoader.addClassLoaderExclusion("cpw.mods.fml.relauncher.");
        this.classLoader.addClassLoaderExclusion("net.minecraftforge.classloading.");
        this.classLoader.addTransformerExclusion("cpw.mods.fml.common.asm.transformers.deobf.");
        this.classLoader.addTransformerExclusion("cpw.mods.fml.common.patcher.");
    }
    
    private void setupClient() {
        FMLRelaunchLog.logFileNamePattern = "ForgeModLoader-client-%g.log";
        FMLLaunchHandler.side = Side.CLIENT;
        this.setupHome();
    }
    
    private void setupServer() {
        FMLRelaunchLog.logFileNamePattern = "ForgeModLoader-server-%g.log";
        FMLLaunchHandler.side = Side.SERVER;
        this.setupHome();
    }
    
    private void setupHome() {
        FMLInjectionData.build(this.minecraftHome, this.classLoader);
        FMLRelaunchLog.minecraftHome = this.minecraftHome;
        FMLRelaunchLog.info("Forge Mod Loader version %s.%s.%s.%s for Minecraft %s loading", FMLInjectionData.major, FMLInjectionData.minor, FMLInjectionData.rev, FMLInjectionData.build, FMLInjectionData.mccversion, FMLInjectionData.mcpversion);
        FMLRelaunchLog.info("Java is %s, version %s, running on %s:%s:%s, installed at %s", System.getProperty("java.vm.name"), System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"), System.getProperty("java.home"));
        FMLRelaunchLog.fine("Java classpath at launch is %s", System.getProperty("java.class.path"));
        FMLRelaunchLog.fine("Java library path at launch is %s", System.getProperty("java.library.path"));
        try {
            CoreModManager.handleLaunch(this.minecraftHome, this.classLoader, this.tweaker);
        }
        catch (final Throwable t) {
            t.printStackTrace();
            FMLRelaunchLog.log(Level.SEVERE, t, "An error occurred trying to configure the minecraft home at %s for Forge Mod Loader", this.minecraftHome.getAbsolutePath());
            throw Throwables.propagate(t);
        }
    }
    
    public static Side side() {
        return FMLLaunchHandler.side;
    }
    
    private void injectPostfixTransformers() {
        CoreModManager.injectTransformers(this.classLoader);
    }
    
    public static void appendCoreMods() {
        FMLLaunchHandler.INSTANCE.injectPostfixTransformers();
    }
}
