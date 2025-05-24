// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.util.EnumSet;

public interface ITickHandler
{
    void tickStart(final EnumSet<TickType> p0, final Object... p1);
    
    void tickEnd(final EnumSet<TickType> p0, final Object... p1);
    
    EnumSet<TickType> ticks();
    
    String getLabel();
}
