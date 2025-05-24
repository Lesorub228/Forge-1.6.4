// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.minecart;

public class MinecartUpdateEvent extends MinecartEvent
{
    public final float x;
    public final float y;
    public final float z;
    
    public MinecartUpdateEvent(final st minecart, final float x, final float y, final float z) {
        super(minecart);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
