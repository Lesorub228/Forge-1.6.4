// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event;

@Cancelable
public class CommandEvent extends Event
{
    public final ab command;
    public final ad sender;
    public String[] parameters;
    public Throwable exception;
    
    public CommandEvent(final ab command, final ad sender, final String[] parameters) {
        this.command = command;
        this.sender = sender;
        this.parameters = parameters;
    }
}
