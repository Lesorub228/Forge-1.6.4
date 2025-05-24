// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.util.EnumSet;

public class SingleIntervalHandler implements IScheduledTickHandler
{
    private ITickHandler wrapped;
    
    public SingleIntervalHandler(final ITickHandler handler) {
        this.wrapped = handler;
    }
    
    @Override
    public void tickStart(final EnumSet<TickType> type, final Object... tickData) {
        this.wrapped.tickStart(type, tickData);
    }
    
    @Override
    public void tickEnd(final EnumSet<TickType> type, final Object... tickData) {
        this.wrapped.tickEnd(type, tickData);
    }
    
    @Override
    public EnumSet<TickType> ticks() {
        return this.wrapped.ticks();
    }
    
    @Override
    public String getLabel() {
        return this.wrapped.getLabel();
    }
    
    @Override
    public int nextTickSpacing() {
        return 1;
    }
}
