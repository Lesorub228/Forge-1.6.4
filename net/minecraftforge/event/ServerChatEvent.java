// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event;

@Cancelable
public class ServerChatEvent extends Event
{
    public final String message;
    public final String username;
    public final jv player;
    public cv component;
    
    public ServerChatEvent(final jv player, final String message, final cv component) {
        this.message = message;
        this.player = player;
        this.username = player.bu;
        this.component = component;
    }
}
