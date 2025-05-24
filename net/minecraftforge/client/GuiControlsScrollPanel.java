// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Mouse;

public class GuiControlsScrollPanel extends awg
{
    protected static final bjo WIDGITS;
    private auy controls;
    private aul options;
    private atv a;
    private String[] message;
    private int _mouseX;
    private int _mouseY;
    private int selected;
    
    public GuiControlsScrollPanel(final auy controls, final aul options, final atv mc) {
        super(mc, controls.g, controls.h, 16, controls.h - 32 + 4, 25);
        this.selected = -1;
        this.controls = controls;
        this.options = options;
        this.a = mc;
    }
    
    protected int a() {
        return this.options.W.length;
    }
    
    protected void a(final int i, final boolean flag) {
        if (!flag) {
            if (this.selected == -1) {
                this.selected = i;
            }
            else {
                this.options.a(this.selected, -100);
                this.selected = -1;
                ats.b();
            }
        }
    }
    
    protected boolean a(final int i) {
        return false;
    }
    
    protected void b() {
    }
    
    public void a(final int mX, final int mY, final float f) {
        this._mouseX = mX;
        this._mouseY = mY;
        if (this.selected != -1 && !Mouse.isButtonDown(0) && Mouse.getDWheel() == 0 && Mouse.next() && Mouse.getEventButtonState()) {
            this.options.a(this.selected, -100 + Mouse.getEventButton());
            this.selected = -1;
            ats.b();
        }
        super.a(mX, mY, f);
    }
    
    protected void a(final int index, int xPosition, final int yPosition, final int l, final bfq tessellator) {
        final int width = 70;
        final int height = 20;
        xPosition -= 20;
        final boolean flag = this._mouseX >= xPosition && this._mouseY >= yPosition && this._mouseX < xPosition + width && this._mouseY < yPosition + height;
        final int k = flag ? 2 : 1;
        this.a.N.a(GuiControlsScrollPanel.WIDGITS);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.controls.b(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
        this.controls.b(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);
        this.controls.b(this.a.l, this.options.a(index), xPosition + width + 4, yPosition + 6, -1);
        boolean conflict = false;
        for (int x = 0; x < this.options.W.length; ++x) {
            if (x != index && this.options.W[x].d == this.options.W[index].d) {
                conflict = true;
                break;
            }
        }
        String str = (conflict ? a.m : "") + this.options.b(index);
        str = ((index == this.selected) ? (a.p + "> " + a.o + "??? " + a.p + "<") : str);
        this.controls.a(this.a.l, str, xPosition + width / 2, yPosition + (height - 8) / 2, -1);
    }
    
    public boolean keyTyped(final char c, final int i) {
        if (this.selected != -1) {
            this.options.a(this.selected, i);
            this.selected = -1;
            ats.b();
            return false;
        }
        return true;
    }
    
    static {
        WIDGITS = new bjo("textures/gui/widgets.png");
    }
}
