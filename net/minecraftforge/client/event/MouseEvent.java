// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event;

import org.lwjgl.input.Mouse;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class MouseEvent extends Event
{
    public final int x;
    public final int y;
    public final int dx;
    public final int dy;
    public final int dwheel;
    public final int button;
    public final boolean buttonstate;
    public final long nanoseconds;
    
    public MouseEvent() {
        this.x = Mouse.getEventX();
        this.y = Mouse.getEventY();
        this.dx = Mouse.getEventDX();
        this.dy = Mouse.getEventDY();
        this.dwheel = Mouse.getEventDWheel();
        this.button = Mouse.getEventButton();
        this.buttonstate = Mouse.getEventButtonState();
        this.nanoseconds = Mouse.getEventNanoseconds();
    }
}
