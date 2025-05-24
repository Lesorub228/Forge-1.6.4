// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import java.util.Random;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.world.WorldEvent;

@HasResult
public class SaplingGrowTreeEvent extends WorldEvent
{
    public final int x;
    public final int y;
    public final int z;
    public final Random rand;
    
    public SaplingGrowTreeEvent(final abw world, final Random rand, final int x, final int y, final int z) {
        super(world);
        this.rand = rand;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
