// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm.transformers;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.Type;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.AnnotationNode;
import java.util.List;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import net.minecraft.launchwrapper.IClassTransformer;

public class SideTransformer implements IClassTransformer
{
    private static String SIDE;
    private static final boolean DEBUG = false;
    
    public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader(bytes);
        classReader.accept((ClassVisitor)classNode, 0);
        if (this.remove(classNode.visibleAnnotations, SideTransformer.SIDE)) {
            throw new RuntimeException(String.format("Attempted to load class %s for invalid side %s", classNode.name, SideTransformer.SIDE));
        }
        final Iterator<FieldNode> fields = classNode.fields.iterator();
        while (fields.hasNext()) {
            final FieldNode field = fields.next();
            if (this.remove(field.visibleAnnotations, SideTransformer.SIDE)) {
                fields.remove();
            }
        }
        final Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext()) {
            final MethodNode method = methods.next();
            if (this.remove(method.visibleAnnotations, SideTransformer.SIDE)) {
                methods.remove();
            }
        }
        final ClassWriter writer = new ClassWriter(1);
        classNode.accept((ClassVisitor)writer);
        return writer.toByteArray();
    }
    
    private boolean remove(final List<AnnotationNode> anns, final String side) {
        if (anns == null) {
            return false;
        }
        for (final AnnotationNode ann : anns) {
            if (ann.desc.equals(Type.getDescriptor((Class)SideOnly.class)) && ann.values != null) {
                for (int x = 0; x < ann.values.size() - 1; x += 2) {
                    final Object key = ann.values.get(x);
                    final Object value = ann.values.get(x + 1);
                    if (key instanceof String && key.equals("value") && value instanceof String[] && !((String[])value)[1].equals(side)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    static {
        SideTransformer.SIDE = FMLLaunchHandler.side().name();
    }
}
