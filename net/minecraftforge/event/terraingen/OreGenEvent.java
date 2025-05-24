// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import java.util.Random;
import net.minecraftforge.event.Event;

public class OreGenEvent extends Event
{
    public final abw world;
    public final Random rand;
    public final int worldX;
    public final int worldZ;
    
    public OreGenEvent(final abw world, final Random rand, final int worldX, final int worldZ) {
        this.world = world;
        this.rand = rand;
        this.worldX = worldX;
        this.worldZ = worldZ;
    }
    
    public static class Pre extends OreGenEvent
    {
        public Pre(final abw world, final Random rand, final int worldX, final int worldZ) {
            super(world, rand, worldX, worldZ);
        }
    }
    
    public static class Post extends OreGenEvent
    {
        public Post(final abw world, final Random rand, final int worldX, final int worldZ) {
            super(world, rand, worldX, worldZ);
        }
    }
    
    @HasResult
    public static class GenerateMinable extends OreGenEvent
    {
        public final EventType type;
        public final afe generator;
        
        public GenerateMinable(final abw world, final Random rand, final afe generator, final int worldX, final int worldZ, final EventType type) {
            super(world, rand, worldX, worldZ);
            this.generator = generator;
            this.type = type;
        }
        
        public enum EventType
        {
            COAL, 
            DIAMOND, 
            DIRT, 
            GOLD, 
            GRAVEL, 
            IRON, 
            LAPIS, 
            REDSTONE, 
            CUSTOM;
        }
    }
}
