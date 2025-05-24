// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

public abstract class RenderLivingEvent extends Event
{
    public final of entity;
    public final bhb renderer;
    
    public RenderLivingEvent(final of entity, final bhb renderer) {
        this.entity = entity;
        this.renderer = renderer;
    }
    
    @Cancelable
    public static class Pre extends RenderLivingEvent
    {
        public Pre(final of entity, final bhb renderer) {
            super(entity, renderer);
        }
    }
    
    public static class Post extends RenderLivingEvent
    {
        public Post(final of entity, final bhb renderer) {
            super(entity, renderer);
        }
    }
    
    public abstract static class Specials extends RenderLivingEvent
    {
        public Specials(final of entity, final bhb renderer) {
            super(entity, renderer);
        }
        
        @Cancelable
        public static class Pre extends Specials
        {
            public Pre(final of entity, final bhb renderer) {
                super(entity, renderer);
            }
        }
        
        public static class Post extends Specials
        {
            public Post(final of entity, final bhb renderer) {
                super(entity, renderer);
            }
        }
    }
}
