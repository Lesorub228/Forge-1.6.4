// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.launcher;

import net.minecraft.launchwrapper.LaunchClassLoader;
import java.io.File;
import java.util.List;
import cpw.mods.fml.relauncher.CoreModManager;
import net.minecraft.launchwrapper.ITweaker;

public class FMLInjectionAndSortingTweaker implements ITweaker
{
    private boolean run;
    
    public FMLInjectionAndSortingTweaker() {
        CoreModManager.injectCoreModTweaks(this);
        this.run = false;
    }
    
    public void acceptOptions(final List<String> args, final File gameDir, final File assetsDir, final String profile) {
        if (!this.run) {
            CoreModManager.sortTweakList();
        }
        this.run = true;
    }
    
    public void injectIntoClassLoader(final LaunchClassLoader classLoader) {
    }
    
    public String getLaunchTarget() {
        return "";
    }
    
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
