// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.relauncher;

import java.util.Map;

public class FMLCorePlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getLibraryRequestClass() {
        return null;
    }
    
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "cpw.mods.fml.common.asm.transformers.AccessTransformer", "cpw.mods.fml.common.asm.transformers.MarkerTransformer", "cpw.mods.fml.common.asm.transformers.SideTransformer" };
    }
    
    @Override
    public String getModContainerClass() {
        return "cpw.mods.fml.common.FMLDummyContainer";
    }
    
    @Override
    public String getSetupClass() {
        return "cpw.mods.fml.common.asm.FMLSanityChecker";
    }
    
    @Override
    public void injectData(final Map<String, Object> data) {
    }
}
