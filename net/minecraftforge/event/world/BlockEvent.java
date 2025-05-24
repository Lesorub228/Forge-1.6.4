// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.world;

import net.minecraftforge.event.Cancelable;
import java.util.ArrayList;
import net.minecraftforge.event.Event;

public class BlockEvent extends Event
{
    public final int x;
    public final int y;
    public final int z;
    public final abw world;
    public final aqz block;
    public final int blockMetadata;
    
    public BlockEvent(final int x, final int y, final int z, final abw world, final aqz block, final int blockMetadata) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.block = block;
        this.blockMetadata = blockMetadata;
    }
    
    public static class HarvestDropsEvent extends BlockEvent
    {
        public final int fortuneLevel;
        public final ArrayList<ye> drops;
        public final boolean isSilkTouching;
        public float dropChance;
        public final uf harvester;
        
        public HarvestDropsEvent(final int x, final int y, final int z, final abw world, final aqz block, final int blockMetadata, final int fortuneLevel, final float dropChance, final ArrayList<ye> drops, final uf harvester, final boolean isSilkTouching) {
            super(x, y, z, world, block, blockMetadata);
            this.fortuneLevel = fortuneLevel;
            this.dropChance = dropChance;
            this.drops = drops;
            this.isSilkTouching = isSilkTouching;
            this.harvester = harvester;
        }
    }
    
    @Cancelable
    public static class BreakEvent extends BlockEvent
    {
        private final uf player;
        private int exp;
        
        public BreakEvent(final int x, final int y, final int z, final abw world, final aqz block, final int blockMetadata, final uf player) {
            super(x, y, z, world, block, blockMetadata);
            this.player = player;
            if (block == null || !player.a(block) || (block.canSilkHarvest(world, player, x, y, z, blockMetadata) && aaw.e((of)player))) {
                this.exp = 0;
            }
            else {
                final int meta = block.h(world, x, y, z);
                final int bonusLevel = aaw.f((of)player);
                this.exp = block.getExpDrop(world, meta, bonusLevel);
            }
        }
        
        public uf getPlayer() {
            return this.player;
        }
        
        public int getExpToDrop() {
            return this.isCanceled() ? 0 : this.exp;
        }
        
        public void setExpToDrop(final int exp) {
            this.exp = exp;
        }
    }
}
