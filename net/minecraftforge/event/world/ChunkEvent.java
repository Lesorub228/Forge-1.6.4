// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.world;

public class ChunkEvent extends WorldEvent
{
    private final adr chunk;
    
    public ChunkEvent(final adr chunk) {
        super(chunk.e);
        this.chunk = chunk;
    }
    
    public adr getChunk() {
        return this.chunk;
    }
    
    public static class Load extends ChunkEvent
    {
        public Load(final adr chunk) {
            super(chunk);
        }
    }
    
    public static class Unload extends ChunkEvent
    {
        public Unload(final adr chunk) {
            super(chunk);
        }
    }
}
