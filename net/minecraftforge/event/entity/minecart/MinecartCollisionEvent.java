// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.minecart;

public class MinecartCollisionEvent extends MinecartEvent
{
    public final nn collider;
    
    public MinecartCollisionEvent(final st minecart, final nn collider) {
        super(minecart);
        this.collider = collider;
    }
}
