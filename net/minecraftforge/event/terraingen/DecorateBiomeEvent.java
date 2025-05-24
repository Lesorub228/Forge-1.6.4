// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import java.util.Random;
import net.minecraftforge.event.Event;

public class DecorateBiomeEvent extends Event
{
    public final abw world;
    public final Random rand;
    public final int chunkX;
    public final int chunkZ;
    
    public DecorateBiomeEvent(final abw world, final Random rand, final int worldX, final int worldZ) {
        this.world = world;
        this.rand = rand;
        this.chunkX = worldX;
        this.chunkZ = worldZ;
    }
    
    public static class Pre extends DecorateBiomeEvent
    {
        public Pre(final abw world, final Random rand, final int worldX, final int worldZ) {
            super(world, rand, worldX, worldZ);
        }
    }
    
    public static class Post extends DecorateBiomeEvent
    {
        public Post(final abw world, final Random rand, final int worldX, final int worldZ) {
            super(world, rand, worldX, worldZ);
        }
    }
    
    @HasResult
    public static class Decorate extends DecorateBiomeEvent
    {
        public final EventType type;
        
        public Decorate(final abw world, final Random rand, final int worldX, final int worldZ, final EventType type) {
            super(world, rand, worldX, worldZ);
            this.type = type;
        }
        
        public enum EventType
        {
            BIG_SHROOM, 
            CACTUS, 
            CLAY, 
            DEAD_BUSH, 
            LILYPAD, 
            FLOWERS, 
            GRASS, 
            LAKE, 
            PUMPKIN, 
            REED, 
            SAND, 
            SAND_PASS2, 
            SHROOM, 
            TREE, 
            CUSTOM;
        }
    }
}
