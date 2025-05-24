// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery.asm;

import com.google.common.collect.Lists;
import org.objectweb.asm.Label;
import java.util.LinkedList;
import org.objectweb.asm.MethodVisitor;

public class ModLoaderPropertiesMethodVisitor extends MethodVisitor
{
    private ASMModParser discoverer;
    private boolean inCode;
    private LinkedList<Label> labels;
    private String foundProperties;
    private boolean validProperties;
    
    public ModLoaderPropertiesMethodVisitor(final String name, final ASMModParser discoverer) {
        super(262144);
        this.labels = Lists.newLinkedList();
        this.discoverer = discoverer;
    }
    
    public void visitCode() {
        this.labels.clear();
    }
    
    public void visitLdcInsn(final Object cst) {
        if (cst instanceof String && this.labels.size() == 1) {
            this.foundProperties = (String)cst;
        }
    }
    
    public void visitInsn(final int opcode) {
        if (176 == opcode && this.labels.size() == 1 && this.foundProperties != null) {
            this.validProperties = true;
        }
    }
    
    public void visitLabel(final Label label) {
        this.labels.push(label);
    }
    
    public void visitEnd() {
        if (this.validProperties) {
            this.discoverer.setBaseModProperties(this.foundProperties);
        }
    }
}
