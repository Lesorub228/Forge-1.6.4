// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

public class PlayerDestroyItemEvent extends PlayerEvent
{
    public final ye original;
    
    public PlayerDestroyItemEvent(final uf player, final ye original) {
        super(player);
        this.original = original;
    }
}
