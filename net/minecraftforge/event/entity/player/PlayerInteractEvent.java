// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlayerInteractEvent extends PlayerEvent
{
    public final Action action;
    public final int x;
    public final int y;
    public final int z;
    public final int face;
    public Result useBlock;
    public Result useItem;
    
    public PlayerInteractEvent(final uf player, final Action action, final int x, final int y, final int z, final int face) {
        super(player);
        this.useBlock = Result.DEFAULT;
        this.useItem = Result.DEFAULT;
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
        if (face == -1) {
            this.useBlock = Result.DENY;
        }
    }
    
    @Override
    public void setCanceled(final boolean cancel) {
        super.setCanceled(cancel);
        this.useBlock = (cancel ? Result.DENY : ((this.useBlock == Result.DENY) ? Result.DEFAULT : this.useBlock));
        this.useItem = (cancel ? Result.DENY : ((this.useItem == Result.DENY) ? Result.DEFAULT : this.useItem));
    }
    
    public enum Action
    {
        RIGHT_CLICK_AIR, 
        RIGHT_CLICK_BLOCK, 
        LEFT_CLICK_BLOCK;
    }
}
