// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.registry;

import java.util.BitSet;

class BlockTracker
{
    private static final BlockTracker INSTANCE;
    private BitSet allocatedBlocks;
    
    private BlockTracker() {
        (this.allocatedBlocks = new BitSet(4096)).set(0, 4096);
        for (int i = 0; i < aqz.s.length; ++i) {
            if (aqz.s[i] != null) {
                this.allocatedBlocks.clear(i);
            }
        }
    }
    
    public static int nextBlockId() {
        return instance().getNextBlockId();
    }
    
    private int getNextBlockId() {
        final int idx = this.allocatedBlocks.nextSetBit(0);
        this.allocatedBlocks.clear(idx);
        return idx;
    }
    
    private static BlockTracker instance() {
        return BlockTracker.INSTANCE;
    }
    
    public static void reserveBlockId(final int id) {
        instance().doReserveId(id);
    }
    
    private void doReserveId(final int id) {
        this.allocatedBlocks.clear(id);
    }
    
    static {
        INSTANCE = new BlockTracker();
    }
}
