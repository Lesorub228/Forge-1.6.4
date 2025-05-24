// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.world;

public class ChunkDataEvent extends ChunkEvent
{
    private final by data;
    
    public ChunkDataEvent(final adr chunk, final by data) {
        super(chunk);
        this.data = data;
    }
    
    public by getData() {
        return this.data;
    }
    
    public static class Load extends ChunkDataEvent
    {
        public Load(final adr chunk, final by data) {
            super(chunk, data);
        }
    }
    
    public static class Save extends ChunkDataEvent
    {
        public Save(final adr chunk, final by data) {
            super(chunk, data);
        }
    }
}
