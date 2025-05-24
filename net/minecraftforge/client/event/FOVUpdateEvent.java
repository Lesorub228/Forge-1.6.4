// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event;

import net.minecraftforge.event.Event;

public class FOVUpdateEvent extends Event
{
    public final bex entity;
    public final float fov;
    public float newfov;
    
    public FOVUpdateEvent(final bex entity, final float fov) {
        this.entity = entity;
        this.fov = fov;
        this.newfov = fov;
    }
}
