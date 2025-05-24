// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.living;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingDeathEvent extends LivingEvent
{
    public final nb source;
    
    public LivingDeathEvent(final of entity, final nb source) {
        super(entity);
        this.source = source;
    }
}
