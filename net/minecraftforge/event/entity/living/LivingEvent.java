// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.living;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;

public class LivingEvent extends EntityEvent
{
    public final of entityLiving;
    
    public LivingEvent(final of entity) {
        super((nn)entity);
        this.entityLiving = entity;
    }
    
    @Cancelable
    public static class LivingUpdateEvent extends LivingEvent
    {
        public LivingUpdateEvent(final of e) {
            super(e);
        }
    }
    
    public static class LivingJumpEvent extends LivingEvent
    {
        public LivingJumpEvent(final of e) {
            super(e);
        }
    }
}
