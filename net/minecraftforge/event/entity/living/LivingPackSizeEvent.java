// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.living;

import net.minecraftforge.event.Event;

@HasResult
public class LivingPackSizeEvent extends LivingEvent
{
    public int maxPackSize;
    
    public LivingPackSizeEvent(final og entity) {
        super((of)entity);
    }
}
