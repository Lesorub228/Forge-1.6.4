// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.Cancelable;

@Cancelable
@HasResult
public class UseHoeEvent extends PlayerEvent
{
    public final ye current;
    public final abw world;
    public final int x;
    public final int y;
    public final int z;
    private boolean handeled;
    
    public UseHoeEvent(final uf player, final ye current, final abw world, final int x, final int y, final int z) {
        super(player);
        this.handeled = false;
        this.current = current;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
