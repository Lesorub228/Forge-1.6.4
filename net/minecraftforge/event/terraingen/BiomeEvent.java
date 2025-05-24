// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import net.minecraftforge.event.Event;

public class BiomeEvent extends Event
{
    public final acq biome;
    
    public BiomeEvent(final acq biome) {
        this.biome = biome;
    }
    
    public static class CreateDecorator extends BiomeEvent
    {
        public final acu originalBiomeDecorator;
        public acu newBiomeDecorator;
        
        public CreateDecorator(final acq biome, final acu original) {
            super(biome);
            this.originalBiomeDecorator = original;
            this.newBiomeDecorator = original;
        }
    }
    
    public static class BlockReplacement extends BiomeEvent
    {
        public final int original;
        public int replacement;
        
        public BlockReplacement(final acq biome, final int original, final int replacement) {
            super(biome);
            this.original = original;
            this.replacement = replacement;
        }
    }
    
    public static class BiomeColor extends BiomeEvent
    {
        public final int originalColor;
        public int newColor;
        
        public BiomeColor(final acq biome, final int original) {
            super(biome);
            this.originalColor = original;
            this.newColor = original;
        }
    }
    
    @HasResult
    public static class GetVillageBlockID extends BlockReplacement
    {
        public GetVillageBlockID(final acq biome, final int original, final int replacement) {
            super(biome, original, replacement);
        }
    }
    
    @HasResult
    public static class GetVillageBlockMeta extends BlockReplacement
    {
        public GetVillageBlockMeta(final acq biome, final int original, final int replacement) {
            super(biome, original, replacement);
        }
    }
    
    public static class GetGrassColor extends BiomeColor
    {
        public GetGrassColor(final acq biome, final int original) {
            super(biome, original);
        }
    }
    
    public static class GetFoliageColor extends BiomeColor
    {
        public GetFoliageColor(final acq biome, final int original) {
            super(biome, original);
        }
    }
    
    public static class GetWaterColor extends BiomeColor
    {
        public GetWaterColor(final acq biome, final int original) {
            super(biome, original);
        }
    }
}
