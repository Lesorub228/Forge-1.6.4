// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class RenderPlayerEvent extends PlayerEvent
{
    public final bhj renderer;
    public final float partialRenderTick;
    
    public RenderPlayerEvent(final uf player, final bhj renderer, final float partialRenderTick) {
        super(player);
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
    }
    
    @Cancelable
    public static class Pre extends RenderPlayerEvent
    {
        public Pre(final uf player, final bhj renderer, final float tick) {
            super(player, renderer, tick);
        }
    }
    
    public static class Post extends RenderPlayerEvent
    {
        public Post(final uf player, final bhj renderer, final float tick) {
            super(player, renderer, tick);
        }
    }
    
    public abstract static class Specials extends RenderPlayerEvent
    {
        @Deprecated
        public final float partialTicks;
        
        public Specials(final uf player, final bhj renderer, final float partialTicks) {
            super(player, renderer, partialTicks);
            this.partialTicks = partialTicks;
        }
        
        @Cancelable
        public static class Pre extends Specials
        {
            public boolean renderHelmet;
            public boolean renderCape;
            public boolean renderItem;
            
            public Pre(final uf player, final bhj renderer, final float partialTicks) {
                super(player, renderer, partialTicks);
                this.renderHelmet = true;
                this.renderCape = true;
                this.renderItem = true;
            }
        }
        
        public static class Post extends Specials
        {
            public Post(final uf player, final bhj renderer, final float partialTicks) {
                super(player, renderer, partialTicks);
            }
        }
    }
    
    public static class SetArmorModel extends RenderPlayerEvent
    {
        public int result;
        public final int slot;
        @Deprecated
        public final float partialTick;
        public final ye stack;
        
        public SetArmorModel(final uf player, final bhj renderer, final int slot, final float partialTick, final ye stack) {
            super(player, renderer, partialTick);
            this.result = -1;
            this.slot = slot;
            this.partialTick = partialTick;
            this.stack = stack;
        }
    }
}
