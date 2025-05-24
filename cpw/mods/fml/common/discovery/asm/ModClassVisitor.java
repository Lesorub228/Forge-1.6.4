// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery.asm;

import org.objectweb.asm.Type;
import java.util.Collections;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

public class ModClassVisitor extends ClassVisitor
{
    private ASMModParser discoverer;
    
    public ModClassVisitor(final ASMModParser discoverer) {
        super(262144);
        this.discoverer = discoverer;
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.discoverer.beginNewTypeName(name, version, superName);
    }
    
    public AnnotationVisitor visitAnnotation(final String annotationName, final boolean runtimeVisible) {
        this.discoverer.startClassAnnotation(annotationName);
        return new ModAnnotationVisitor(this.discoverer);
    }
    
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        return new ModFieldVisitor(name, this.discoverer);
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        if (this.discoverer.isBaseMod(Collections.emptyList()) && name.equals("getPriorities") && desc.equals(Type.getMethodDescriptor(Type.getType((Class)String.class), new Type[0]))) {
            return new ModLoaderPropertiesMethodVisitor(name, this.discoverer);
        }
        return new ModMethodVisitor(name, desc, this.discoverer);
    }
}
