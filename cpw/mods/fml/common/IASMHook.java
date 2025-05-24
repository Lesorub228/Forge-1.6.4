// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import org.objectweb.asm.tree.ClassNode;

public interface IASMHook
{
    ClassNode[] inject(final ClassNode p0);
    
    void modifyClass(final String p0, final ClassNode p1);
}
