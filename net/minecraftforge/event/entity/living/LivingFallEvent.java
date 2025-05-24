// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.living;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingFallEvent extends LivingEvent
{
    public float distance;
    
    public LivingFallEvent(final of entity, final float distance) {
        super(entity);
        this.distance = distance;
    }
}
