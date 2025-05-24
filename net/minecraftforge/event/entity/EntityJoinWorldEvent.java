// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityJoinWorldEvent extends EntityEvent
{
    public final abw world;
    
    public EntityJoinWorldEvent(final nn entity, final abw world) {
        super(entity);
        this.world = world;
    }
}
