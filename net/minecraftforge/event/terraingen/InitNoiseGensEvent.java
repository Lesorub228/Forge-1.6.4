// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import java.util.Random;
import net.minecraftforge.event.world.WorldEvent;

public class InitNoiseGensEvent extends WorldEvent
{
    public final Random rand;
    public final ajt[] originalNoiseGens;
    public ajt[] newNoiseGens;
    
    public InitNoiseGensEvent(final abw world, final Random rand, final ajt[] original) {
        super(world);
        this.rand = rand;
        this.originalNoiseGens = original;
        this.newNoiseGens = original.clone();
    }
}
