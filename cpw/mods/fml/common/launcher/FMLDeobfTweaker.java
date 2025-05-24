// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.launcher;

import java.lang.reflect.Method;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import java.io.File;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;

public class FMLDeobfTweaker implements ITweaker
{
    public void acceptOptions(final List<String> args, final File gameDir, final File assetsDir, final String profile) {
    }
    
    public void injectIntoClassLoader(final LaunchClassLoader classLoader) {
        if (!Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            classLoader.registerTransformer("cpw.mods.fml.common.asm.transformers.DeobfuscationTransformer");
        }
        try {
            FMLRelaunchLog.fine("Validating minecraft", new Object[0]);
            final Class<?> loaderClazz = Class.forName("cpw.mods.fml.common.Loader", true, (ClassLoader)classLoader);
            Method m = loaderClazz.getMethod("injectData", Object[].class);
            m.invoke(null, FMLInjectionData.data());
            m = loaderClazz.getMethod("instance", (Class<?>[])new Class[0]);
            m.invoke(null, new Object[0]);
            FMLRelaunchLog.fine("Minecraft validated, launching...", new Object[0]);
        }
        catch (final Exception e) {
            System.out.println("A CRITICAL PROBLEM OCCURED INITIALIZING MINECRAFT - LIKELY YOU HAVE AN INCORRECT VERSION FOR THIS FML");
            throw new RuntimeException(e);
        }
    }
    
    public String getLaunchTarget() {
        throw new RuntimeException("Invalid for use as a primary tweaker");
    }
    
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
