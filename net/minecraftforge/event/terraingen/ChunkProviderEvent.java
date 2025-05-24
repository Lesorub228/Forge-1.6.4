// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import net.minecraftforge.event.Event;

public class ChunkProviderEvent extends Event
{
    public final ado chunkProvider;
    
    public ChunkProviderEvent(final ado chunkProvider) {
        this.chunkProvider = chunkProvider;
    }
    
    @HasResult
    public static class ReplaceBiomeBlocks extends ChunkProviderEvent
    {
        public final int chunkX;
        public final int chunkZ;
        public final byte[] blockArray;
        public final acq[] biomeArray;
        
        public ReplaceBiomeBlocks(final ado chunkProvider, final int chunkX, final int chunkZ, final byte[] blockArray, final acq[] biomeArray) {
            super(chunkProvider);
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.blockArray = blockArray;
            this.biomeArray = biomeArray;
        }
    }
    
    @HasResult
    public static class InitNoiseField extends ChunkProviderEvent
    {
        public double[] noisefield;
        public final int posX;
        public final int posY;
        public final int posZ;
        public final int sizeX;
        public final int sizeY;
        public final int sizeZ;
        
        public InitNoiseField(final ado chunkProvider, final double[] noisefield, final int posX, final int posY, final int posZ, final int sizeX, final int sizeY, final int sizeZ) {
            super(chunkProvider);
            this.noisefield = noisefield;
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
        }
    }
}
