// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm.transformers.deobf;

import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.RemappingClassAdapter;

public class FMLRemappingAdapter extends RemappingClassAdapter
{
    public FMLRemappingAdapter(final ClassVisitor cv) {
        super(cv, (Remapper)FMLDeobfuscatingRemapper.INSTANCE);
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, String[] interfaces) {
        if (interfaces == null) {
            interfaces = new String[0];
        }
        FMLDeobfuscatingRemapper.INSTANCE.mergeSuperMaps(name, superName, interfaces);
        super.visit(version, access, name, signature, superName, interfaces);
    }
}
