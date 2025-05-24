// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import net.minecraftforge.event.Event;

public class WorldTypeEvent extends Event
{
    public final acg worldType;
    
    public WorldTypeEvent(final acg worldType) {
        this.worldType = worldType;
    }
    
    public static class BiomeSize extends WorldTypeEvent
    {
        public final byte originalSize;
        public byte newSize;
        
        public BiomeSize(final acg worldType, final byte original) {
            super(worldType);
            this.originalSize = original;
            this.newSize = original;
        }
    }
    
    public static class InitBiomeGens extends WorldTypeEvent
    {
        public final long seed;
        public final akq[] originalBiomeGens;
        public akq[] newBiomeGens;
        
        public InitBiomeGens(final acg worldType, final long seed, final akq[] original) {
            super(worldType);
            this.seed = seed;
            this.originalBiomeGens = original;
            this.newBiomeGens = original.clone();
        }
    }
}
