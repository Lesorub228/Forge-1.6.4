// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm.transformers;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.ClassVisitor;
import cpw.mods.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassReader;
import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;

public class DeobfuscationTransformer implements IClassTransformer, IClassNameTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        final ClassReader classReader = new ClassReader(bytes);
        final ClassWriter classWriter = new ClassWriter(1);
        final RemappingClassAdapter remapAdapter = new FMLRemappingAdapter((ClassVisitor)classWriter);
        classReader.accept((ClassVisitor)remapAdapter, 8);
        return classWriter.toByteArray();
    }
    
    public String remapClassName(final String name) {
        return FMLDeobfuscatingRemapper.INSTANCE.map(name.replace('.', '/')).replace('/', '.');
    }
    
    public String unmapClassName(final String name) {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/', '.');
    }
}
