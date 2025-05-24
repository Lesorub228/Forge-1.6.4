// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm.transformers;

import cpw.mods.fml.common.patcher.ClassPatchManager;
import net.minecraft.launchwrapper.IClassTransformer;

public class PatchingTransformer implements IClassTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
        return ClassPatchManager.INSTANCE.applyPatch(name, transformedName, bytes);
    }
}
