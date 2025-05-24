// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery.asm;

import org.objectweb.asm.AnnotationVisitor;

public class ModAnnotationVisitor extends AnnotationVisitor
{
    private ASMModParser discoverer;
    private boolean array;
    private String name;
    private boolean isSubAnnotation;
    
    public ModAnnotationVisitor(final ASMModParser discoverer) {
        super(262144);
        this.discoverer = discoverer;
    }
    
    public ModAnnotationVisitor(final ASMModParser discoverer, final String name) {
        this(discoverer);
        this.array = true;
        discoverer.addAnnotationArray(this.name = name);
    }
    
    public ModAnnotationVisitor(final ASMModParser discoverer, final boolean isSubAnnotation) {
        this(discoverer);
        this.isSubAnnotation = true;
    }
    
    public void visit(final String key, final Object value) {
        this.discoverer.addAnnotationProperty(key, value);
    }
    
    public void visitEnum(final String name, final String desc, final String value) {
        this.discoverer.addAnnotationEnumProperty(name, desc, value);
    }
    
    public AnnotationVisitor visitArray(final String name) {
        return new ModAnnotationVisitor(this.discoverer, name);
    }
    
    public AnnotationVisitor visitAnnotation(final String name, final String desc) {
        this.discoverer.addSubAnnotation(name, desc);
        return new ModAnnotationVisitor(this.discoverer, true);
    }
    
    public void visitEnd() {
        if (this.array) {
            this.discoverer.endArray();
        }
        if (this.isSubAnnotation) {
            this.discoverer.endSubAnnotation();
        }
    }
}
