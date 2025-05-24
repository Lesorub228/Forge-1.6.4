// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.relauncher;

import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Properties;
import net.minecraft.launchwrapper.LaunchClassLoader;
import java.util.List;
import java.io.File;

public class FMLInjectionData
{
    static File minecraftHome;
    static String major;
    static String minor;
    static String rev;
    static String build;
    static String mccversion;
    static String mcpversion;
    static String deobfuscationDataHash;
    public static List<String> containers;
    
    static void build(final File mcHome, final LaunchClassLoader classLoader) {
        FMLInjectionData.minecraftHome = mcHome;
        final InputStream stream = classLoader.getResourceAsStream("fmlversion.properties");
        final Properties properties = new Properties();
        if (stream != null) {
            try {
                properties.load(stream);
            }
            catch (final IOException ex) {
                FMLRelaunchLog.log(Level.SEVERE, ex, "Could not get FML version information - corrupted installation detected!", new Object[0]);
            }
        }
        FMLInjectionData.major = properties.getProperty("fmlbuild.major.number", "missing");
        FMLInjectionData.minor = properties.getProperty("fmlbuild.minor.number", "missing");
        FMLInjectionData.rev = properties.getProperty("fmlbuild.revision.number", "missing");
        FMLInjectionData.build = properties.getProperty("fmlbuild.build.number", "missing");
        FMLInjectionData.mccversion = properties.getProperty("fmlbuild.mcversion", "missing");
        FMLInjectionData.mcpversion = properties.getProperty("fmlbuild.mcpversion", "missing");
        FMLInjectionData.deobfuscationDataHash = properties.getProperty("fmlbuild.deobfuscation.hash", "deadbeef");
    }
    
    static String debfuscationDataName() {
        return "/deobfuscation_data-" + FMLInjectionData.mccversion + ".lzma";
    }
    
    public static Object[] data() {
        return new Object[] { FMLInjectionData.major, FMLInjectionData.minor, FMLInjectionData.rev, FMLInjectionData.build, FMLInjectionData.mccversion, FMLInjectionData.mcpversion, FMLInjectionData.minecraftHome, FMLInjectionData.containers };
    }
    
    static {
        FMLInjectionData.containers = new ArrayList<String>();
    }
}
