// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

public class PlayerSleepInBedEvent extends PlayerEvent
{
    public ug result;
    public final int x;
    public final int y;
    public final int z;
    
    public PlayerSleepInBedEvent(final uf player, final int x, final int y, final int z) {
        super(player);
        this.result = null;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
