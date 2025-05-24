// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class GuiOpenEvent extends Event
{
    public awe gui;
    
    public GuiOpenEvent(final awe gui) {
        this.gui = gui;
    }
}
