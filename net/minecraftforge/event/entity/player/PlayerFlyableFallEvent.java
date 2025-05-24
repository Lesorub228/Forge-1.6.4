// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

public class PlayerFlyableFallEvent extends PlayerEvent
{
    public float distance;
    
    public PlayerFlyableFallEvent(final uf player, final float f) {
        super(player);
        this.distance = f;
    }
}
