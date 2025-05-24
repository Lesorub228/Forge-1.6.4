// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.launcher;

import java.util.Collection;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import net.minecraft.launchwrapper.LaunchClassLoader;
import java.util.Iterator;
import java.net.URISyntaxException;
import com.google.common.base.Throwables;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.launchwrapper.Launch;
import java.net.URI;
import java.util.Map;
import java.io.File;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;

public class FMLTweaker implements ITweaker
{
    private List<String> args;
    private File gameDir;
    private File assetsDir;
    private String profile;
    private Map<String, String> launchArgs;
    private List<String> standaloneArgs;
    private static URI jarLocation;
    
    public void acceptOptions(final List<String> args, final File gameDir, final File assetsDir, final String profile) {
        this.gameDir = ((gameDir == null) ? new File(".") : gameDir);
        this.assetsDir = assetsDir;
        this.profile = profile;
        this.args = args;
        this.launchArgs = Launch.blackboard.get("launchArgs");
        this.standaloneArgs = Lists.newArrayList();
        if (this.launchArgs == null) {
            this.launchArgs = Maps.newHashMap();
            Launch.blackboard.put("launchArgs", this.launchArgs);
        }
        String classifier = null;
        for (final String arg : args) {
            if (arg.startsWith("-")) {
                if (classifier != null) {
                    classifier = this.launchArgs.put(classifier, "");
                }
                else if (arg.contains("=")) {
                    classifier = this.launchArgs.put(arg.substring(0, arg.indexOf(61)), arg.substring(arg.indexOf(61) + 1));
                }
                else {
                    classifier = arg;
                }
            }
            else if (classifier != null) {
                classifier = this.launchArgs.put(classifier, arg);
            }
            else {
                this.standaloneArgs.add(arg);
            }
        }
        if (!this.launchArgs.containsKey("--version")) {
            this.launchArgs.put("--version", (profile != null) ? profile : "UnknownFMLProfile");
        }
        if (!this.launchArgs.containsKey("--gameDir") && gameDir != null) {
            this.launchArgs.put("--gameDir", gameDir.getAbsolutePath());
        }
        if (!this.launchArgs.containsKey("--assetsDir") && assetsDir != null) {
            this.launchArgs.put("--assetsDir", assetsDir.getAbsolutePath());
        }
        try {
            FMLTweaker.jarLocation = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        }
        catch (final URISyntaxException e) {
            Logger.getLogger("FMLTWEAK").log(Level.SEVERE, "Missing URI information for FML tweak");
            throw Throwables.propagate((Throwable)e);
        }
    }
    
    public void injectIntoClassLoader(final LaunchClassLoader classLoader) {
        classLoader.addTransformerExclusion("cpw.mods.fml.repackage.");
        classLoader.addTransformerExclusion("cpw.mods.fml.relauncher.");
        classLoader.addTransformerExclusion("cpw.mods.fml.common.asm.transformers.");
        classLoader.addClassLoaderExclusion("LZMA.");
        FMLLaunchHandler.configureForClientLaunch(classLoader, this);
        FMLLaunchHandler.appendCoreMods();
    }
    
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }
    
    public String[] getLaunchArguments() {
        final List<String> args = Lists.newArrayList();
        args.addAll(this.standaloneArgs);
        for (final Map.Entry<String, String> arg : this.launchArgs.entrySet()) {
            args.add(arg.getKey());
            args.add(arg.getValue());
        }
        this.launchArgs.clear();
        return args.toArray(new String[args.size()]);
    }
    
    public File getGameDir() {
        return this.gameDir;
    }
    
    public static URI getJarLocation() {
        return FMLTweaker.jarLocation;
    }
    
    public void injectCascadingTweak(final String tweakClassName) {
        final List<String> tweakClasses = Launch.blackboard.get("TweakClasses");
        tweakClasses.add(tweakClassName);
    }
}
