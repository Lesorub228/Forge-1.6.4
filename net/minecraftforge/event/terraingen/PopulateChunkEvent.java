// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import net.minecraftforge.event.Event;
import java.util.Random;

public class PopulateChunkEvent extends ChunkProviderEvent
{
    public final abw world;
    public final Random rand;
    public final int chunkX;
    public final int chunkZ;
    public final boolean hasVillageGenerated;
    
    public PopulateChunkEvent(final ado chunkProvider, final abw world, final Random rand, final int chunkX, final int chunkZ, final boolean hasVillageGenerated) {
        super(chunkProvider);
        this.world = world;
        this.rand = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.hasVillageGenerated = hasVillageGenerated;
    }
    
    public static class Pre extends PopulateChunkEvent
    {
        public Pre(final ado chunkProvider, final abw world, final Random rand, final int chunkX, final int chunkZ, final boolean hasVillageGenerated) {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }
    
    public static class Post extends PopulateChunkEvent
    {
        public Post(final ado chunkProvider, final abw world, final Random rand, final int chunkX, final int chunkZ, final boolean hasVillageGenerated) {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }
    
    @HasResult
    public static class Populate extends PopulateChunkEvent
    {
        public final EventType type;
        
        public Populate(final ado chunkProvider, final abw world, final Random rand, final int chunkX, final int chunkZ, final boolean hasVillageGenerated, final EventType type) {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
            this.type = type;
        }
        
        public enum EventType
        {
            DUNGEON, 
            FIRE, 
            GLOWSTONE, 
            ICE, 
            LAKE, 
            LAVA, 
            NETHER_LAVA, 
            CUSTOM;
        }
    }
}
