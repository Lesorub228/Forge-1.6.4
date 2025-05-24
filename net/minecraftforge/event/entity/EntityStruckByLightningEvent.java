// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityStruckByLightningEvent extends EntityEvent
{
    public final sp lightning;
    
    public EntityStruckByLightningEvent(final nn entity, final sp lightning) {
        super(entity);
        this.lightning = lightning;
    }
}
