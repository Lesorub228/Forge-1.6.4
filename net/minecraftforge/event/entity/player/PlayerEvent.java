// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PlayerEvent extends LivingEvent
{
    public final uf entityPlayer;
    
    public PlayerEvent(final uf player) {
        super((of)player);
        this.entityPlayer = player;
    }
    
    public static class HarvestCheck extends PlayerEvent
    {
        public final aqz block;
        public boolean success;
        
        public HarvestCheck(final uf player, final aqz block, final boolean success) {
            super(player);
            this.block = block;
            this.success = success;
        }
    }
    
    @Cancelable
    public static class BreakSpeed extends PlayerEvent
    {
        public final aqz block;
        public final int metadata;
        public final float originalSpeed;
        public float newSpeed;
        
        public BreakSpeed(final uf player, final aqz block, final int metadata, final float original) {
            super(player);
            this.newSpeed = 0.0f;
            this.block = block;
            this.metadata = metadata;
            this.originalSpeed = original;
            this.newSpeed = original;
        }
    }
    
    public static class NameFormat extends PlayerEvent
    {
        public final String username;
        public String displayname;
        
        public NameFormat(final uf player, final String username) {
            super(player);
            this.username = username;
            this.displayname = username;
        }
    }
}
