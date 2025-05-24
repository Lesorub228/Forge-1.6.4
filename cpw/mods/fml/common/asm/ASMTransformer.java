// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import cpw.mods.fml.common.registry.BlockProxy;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.ClassReader;
import net.minecraft.launchwrapper.IClassTransformer;

public class ASMTransformer implements IClassTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
        if ("net.minecraft.src.Block".equals(name)) {
            final ClassReader cr = new ClassReader(bytes);
            final ClassNode cn = new ClassNode(262144);
            cr.accept((ClassVisitor)cn, 8);
            cn.interfaces.add(Type.getInternalName((Class)BlockProxy.class));
            final ClassWriter cw = new ClassWriter(3);
            cn.accept((ClassVisitor)cw);
            return cw.toByteArray();
        }
        return bytes;
    }
}
