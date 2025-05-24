// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

public class ModMethodVisitor extends MethodVisitor
{
    private String methodName;
    private String methodDescriptor;
    private ASMModParser discoverer;
    
    public ModMethodVisitor(final String name, final String desc, final ASMModParser discoverer) {
        super(262144);
        this.methodName = name;
        this.methodDescriptor = desc;
        this.discoverer = discoverer;
    }
    
    public AnnotationVisitor visitAnnotation(final String annotationName, final boolean runtimeVisible) {
        this.discoverer.startMethodAnnotation(this.methodName, this.methodDescriptor, annotationName);
        return new ModAnnotationVisitor(this.discoverer);
    }
}
