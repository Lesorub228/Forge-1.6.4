// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.classloading;

import java.util.Map;
import java.io.File;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class FMLForgePlugin implements IFMLLoadingPlugin
{
    public static boolean RUNTIME_DEOBF;
    public static File forgeLocation;
    
    @Override
    public String[] getLibraryRequestClass() {
        return null;
    }
    
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "net.minecraftforge.transformers.ForgeAccessTransformer", "net.minecraftforge.transformers.EventTransformer" };
    }
    
    @Override
    public String getModContainerClass() {
        return "net.minecraftforge.common.ForgeDummyContainer";
    }
    
    @Override
    public String getSetupClass() {
        return null;
    }
    
    @Override
    public void injectData(final Map<String, Object> data) {
        FMLForgePlugin.RUNTIME_DEOBF = data.get("runtimeDeobfuscationEnabled");
        FMLForgePlugin.forgeLocation = data.get("coremodLocation");
    }
    
    static {
        FMLForgePlugin.RUNTIME_DEOBF = false;
    }
}
