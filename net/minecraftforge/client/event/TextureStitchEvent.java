// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event;

import net.minecraftforge.event.Event;

public class TextureStitchEvent extends Event
{
    public final bik map;
    
    public TextureStitchEvent(final bik map) {
        this.map = map;
    }
    
    public static class Pre extends TextureStitchEvent
    {
        public Pre(final bik map) {
            super(map);
        }
    }
    
    public static class Post extends TextureStitchEvent
    {
        public Post(final bik map) {
            super(map);
        }
    }
}
