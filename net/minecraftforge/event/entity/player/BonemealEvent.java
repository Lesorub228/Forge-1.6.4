// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.Cancelable;

@Cancelable
@HasResult
public class BonemealEvent extends PlayerEvent
{
    public final abw world;
    public final int ID;
    public final int X;
    public final int Y;
    public final int Z;
    
    public BonemealEvent(final uf player, final abw world, final int id, final int x, final int y, final int z) {
        super(player);
        this.world = world;
        this.ID = id;
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
}
