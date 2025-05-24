// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.patcher;

public class ClassPatch
{
    public final String name;
    public final String sourceClassName;
    public final String targetClassName;
    public final boolean existsAtTarget;
    public final byte[] patch;
    public final int inputChecksum;
    
    public ClassPatch(final String name, final String sourceClassName, final String targetClassName, final boolean existsAtTarget, final int inputChecksum, final byte[] patch) {
        this.name = name;
        this.sourceClassName = sourceClassName;
        this.targetClassName = targetClassName;
        this.existsAtTarget = existsAtTarget;
        this.inputChecksum = inputChecksum;
        this.patch = patch;
    }
    
    @Override
    public String toString() {
        return String.format("%s : %s => %s (%b) size %d", this.name, this.sourceClassName, this.targetClassName, this.existsAtTarget, this.patch.length);
    }
}
