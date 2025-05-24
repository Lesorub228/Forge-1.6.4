// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Mouse;
import java.util.List;

public abstract class GuiScrollingList
{
    private final atv client;
    protected final int listWidth;
    protected final int listHeight;
    protected final int top;
    protected final int bottom;
    private final int right;
    protected final int left;
    protected final int slotHeight;
    private int scrollUpActionId;
    private int scrollDownActionId;
    protected int mouseX;
    protected int mouseY;
    private float initialMouseClickY;
    private float scrollFactor;
    private float scrollDistance;
    private int selectedIndex;
    private long lastClickTime;
    private boolean field_25123_p;
    private boolean field_27262_q;
    private int field_27261_r;
    
    public GuiScrollingList(final atv client, final int width, final int height, final int top, final int bottom, final int left, final int entryHeight) {
        this.initialMouseClickY = -2.0f;
        this.selectedIndex = -1;
        this.lastClickTime = 0L;
        this.field_25123_p = true;
        this.client = client;
        this.listWidth = width;
        this.listHeight = height;
        this.top = top;
        this.bottom = bottom;
        this.slotHeight = entryHeight;
        this.left = left;
        this.right = width + this.left;
    }
    
    public void func_27258_a(final boolean p_27258_1_) {
        this.field_25123_p = p_27258_1_;
    }
    
    protected void func_27259_a(final boolean p_27259_1_, final int p_27259_2_) {
        this.field_27262_q = p_27259_1_;
        this.field_27261_r = p_27259_2_;
        if (!p_27259_1_) {
            this.field_27261_r = 0;
        }
    }
    
    protected abstract int getSize();
    
    protected abstract void elementClicked(final int p0, final boolean p1);
    
    protected abstract boolean isSelected(final int p0);
    
    protected int getContentHeight() {
        return this.getSize() * this.slotHeight + this.field_27261_r;
    }
    
    protected abstract void drawBackground();
    
    protected abstract void drawSlot(final int p0, final int p1, final int p2, final int p3, final bfq p4);
    
    protected void func_27260_a(final int p_27260_1_, final int p_27260_2_, final bfq p_27260_3_) {
    }
    
    protected void func_27255_a(final int p_27255_1_, final int p_27255_2_) {
    }
    
    protected void func_27257_b(final int p_27257_1_, final int p_27257_2_) {
    }
    
    public int func_27256_c(final int p_27256_1_, final int p_27256_2_) {
        final int var3 = this.left + 1;
        final int var4 = this.left + this.listWidth - 7;
        final int var5 = p_27256_2_ - this.top - this.field_27261_r + (int)this.scrollDistance - 4;
        final int var6 = var5 / this.slotHeight;
        return (p_27256_1_ >= var3 && p_27256_1_ <= var4 && var6 >= 0 && var5 >= 0 && var6 < this.getSize()) ? var6 : -1;
    }
    
    public void registerScrollButtons(final List p_22240_1_, final int p_22240_2_, final int p_22240_3_) {
        this.scrollUpActionId = p_22240_2_;
        this.scrollDownActionId = p_22240_3_;
    }
    
    private void applyScrollLimits() {
        int var1 = this.getContentHeight() - (this.bottom - this.top - 4);
        if (var1 < 0) {
            var1 /= 2;
        }
        if (this.scrollDistance < 0.0f) {
            this.scrollDistance = 0.0f;
        }
        if (this.scrollDistance > var1) {
            this.scrollDistance = (float)var1;
        }
    }
    
    public void actionPerformed(final aut button) {
        if (button.h) {
            if (button.g == this.scrollUpActionId) {
                this.scrollDistance -= this.slotHeight * 2 / 3;
                this.initialMouseClickY = -2.0f;
                this.applyScrollLimits();
            }
            else if (button.g == this.scrollDownActionId) {
                this.scrollDistance += this.slotHeight * 2 / 3;
                this.initialMouseClickY = -2.0f;
                this.applyScrollLimits();
            }
        }
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float p_22243_3_) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.drawBackground();
        final int listLength = this.getSize();
        final int scrollBarXStart = this.left + this.listWidth - 6;
        final int scrollBarXEnd = scrollBarXStart + 6;
        final int boxLeft = this.left;
        final int boxRight = scrollBarXStart - 1;
        if (Mouse.isButtonDown(0)) {
            if (this.initialMouseClickY == -1.0f) {
                boolean var7 = true;
                if (mouseY >= this.top && mouseY <= this.bottom) {
                    final int var8 = mouseY - this.top - this.field_27261_r + (int)this.scrollDistance - 4;
                    final int var9 = var8 / this.slotHeight;
                    if (mouseX >= boxLeft && mouseX <= boxRight && var9 >= 0 && var8 >= 0 && var9 < listLength) {
                        final boolean var10 = var9 == this.selectedIndex && System.currentTimeMillis() - this.lastClickTime < 250L;
                        this.elementClicked(var9, var10);
                        this.selectedIndex = var9;
                        this.lastClickTime = System.currentTimeMillis();
                    }
                    else if (mouseX >= boxLeft && mouseX <= boxRight && var8 < 0) {
                        this.func_27255_a(mouseX - boxLeft, mouseY - this.top + (int)this.scrollDistance - 4);
                        var7 = false;
                    }
                    if (mouseX >= scrollBarXStart && mouseX <= scrollBarXEnd) {
                        this.scrollFactor = -1.0f;
                        int var11 = this.getContentHeight() - (this.bottom - this.top - 4);
                        if (var11 < 1) {
                            var11 = 1;
                        }
                        int var12 = (int)((this.bottom - this.top) * (this.bottom - this.top) / (float)this.getContentHeight());
                        if (var12 < 32) {
                            var12 = 32;
                        }
                        if (var12 > this.bottom - this.top - 8) {
                            var12 = this.bottom - this.top - 8;
                        }
                        this.scrollFactor /= (this.bottom - this.top - var12) / (float)var11;
                    }
                    else {
                        this.scrollFactor = 1.0f;
                    }
                    if (var7) {
                        this.initialMouseClickY = (float)mouseY;
                    }
                    else {
                        this.initialMouseClickY = -2.0f;
                    }
                }
                else {
                    this.initialMouseClickY = -2.0f;
                }
            }
            else if (this.initialMouseClickY >= 0.0f) {
                this.scrollDistance -= (mouseY - this.initialMouseClickY) * this.scrollFactor;
                this.initialMouseClickY = (float)mouseY;
            }
        }
        else {
            while (Mouse.next()) {
                int var13 = Mouse.getEventDWheel();
                if (var13 != 0) {
                    if (var13 > 0) {
                        var13 = -1;
                    }
                    else if (var13 < 0) {
                        var13 = 1;
                    }
                    this.scrollDistance += var13 * this.slotHeight / 2;
                }
            }
            this.initialMouseClickY = -1.0f;
        }
        this.applyScrollLimits();
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        final bfq var14 = bfq.a;
        this.client.N.a(avk.k);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float var15 = 32.0f;
        var14.b();
        var14.d(2105376);
        var14.a((double)this.left, (double)this.bottom, 0.0, (double)(this.left / var15), (double)((this.bottom + (int)this.scrollDistance) / var15));
        var14.a((double)this.right, (double)this.bottom, 0.0, (double)(this.right / var15), (double)((this.bottom + (int)this.scrollDistance) / var15));
        var14.a((double)this.right, (double)this.top, 0.0, (double)(this.right / var15), (double)((this.top + (int)this.scrollDistance) / var15));
        var14.a((double)this.left, (double)this.top, 0.0, (double)(this.left / var15), (double)((this.top + (int)this.scrollDistance) / var15));
        var14.a();
        final int var8 = this.top + 4 - (int)this.scrollDistance;
        if (this.field_27262_q) {
            this.func_27260_a(boxRight, var8, var14);
        }
        for (int var9 = 0; var9 < listLength; ++var9) {
            final int var11 = var8 + var9 * this.slotHeight + this.field_27261_r;
            final int var12 = this.slotHeight - 4;
            if (var11 <= this.bottom && var11 + var12 >= this.top) {
                if (this.field_25123_p && this.isSelected(var9)) {
                    final int var16 = boxLeft;
                    final int var17 = boxRight;
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glDisable(3553);
                    var14.b();
                    var14.d(8421504);
                    var14.a((double)var16, (double)(var11 + var12 + 2), 0.0, 0.0, 1.0);
                    var14.a((double)var17, (double)(var11 + var12 + 2), 0.0, 1.0, 1.0);
                    var14.a((double)var17, (double)(var11 - 2), 0.0, 1.0, 0.0);
                    var14.a((double)var16, (double)(var11 - 2), 0.0, 0.0, 0.0);
                    var14.d(0);
                    var14.a((double)(var16 + 1), (double)(var11 + var12 + 1), 0.0, 0.0, 1.0);
                    var14.a((double)(var17 - 1), (double)(var11 + var12 + 1), 0.0, 1.0, 1.0);
                    var14.a((double)(var17 - 1), (double)(var11 - 1), 0.0, 1.0, 0.0);
                    var14.a((double)(var16 + 1), (double)(var11 - 1), 0.0, 0.0, 0.0);
                    var14.a();
                    GL11.glEnable(3553);
                }
                this.drawSlot(var9, boxRight, var11, var12, var14);
            }
        }
        GL11.glDisable(2929);
        final byte var18 = 4;
        this.overlayBackground(0, this.top, 255, 255);
        this.overlayBackground(this.bottom, this.listHeight, 255, 255);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        var14.b();
        var14.a(0, 0);
        var14.a((double)this.left, (double)(this.top + var18), 0.0, 0.0, 1.0);
        var14.a((double)this.right, (double)(this.top + var18), 0.0, 1.0, 1.0);
        var14.a(0, 255);
        var14.a((double)this.right, (double)this.top, 0.0, 1.0, 0.0);
        var14.a((double)this.left, (double)this.top, 0.0, 0.0, 0.0);
        var14.a();
        var14.b();
        var14.a(0, 255);
        var14.a((double)this.left, (double)this.bottom, 0.0, 0.0, 1.0);
        var14.a((double)this.right, (double)this.bottom, 0.0, 1.0, 1.0);
        var14.a(0, 0);
        var14.a((double)this.right, (double)(this.bottom - var18), 0.0, 1.0, 0.0);
        var14.a((double)this.left, (double)(this.bottom - var18), 0.0, 0.0, 0.0);
        var14.a();
        int var11 = this.getContentHeight() - (this.bottom - this.top - 4);
        if (var11 > 0) {
            int var12 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            if (var12 < 32) {
                var12 = 32;
            }
            if (var12 > this.bottom - this.top - 8) {
                var12 = this.bottom - this.top - 8;
            }
            int var16 = (int)this.scrollDistance * (this.bottom - this.top - var12) / var11 + this.top;
            if (var16 < this.top) {
                var16 = this.top;
            }
            var14.b();
            var14.a(0, 255);
            var14.a((double)scrollBarXStart, (double)this.bottom, 0.0, 0.0, 1.0);
            var14.a((double)scrollBarXEnd, (double)this.bottom, 0.0, 1.0, 1.0);
            var14.a((double)scrollBarXEnd, (double)this.top, 0.0, 1.0, 0.0);
            var14.a((double)scrollBarXStart, (double)this.top, 0.0, 0.0, 0.0);
            var14.a();
            var14.b();
            var14.a(8421504, 255);
            var14.a((double)scrollBarXStart, (double)(var16 + var12), 0.0, 0.0, 1.0);
            var14.a((double)scrollBarXEnd, (double)(var16 + var12), 0.0, 1.0, 1.0);
            var14.a((double)scrollBarXEnd, (double)var16, 0.0, 1.0, 0.0);
            var14.a((double)scrollBarXStart, (double)var16, 0.0, 0.0, 0.0);
            var14.a();
            var14.b();
            var14.a(12632256, 255);
            var14.a((double)scrollBarXStart, (double)(var16 + var12 - 1), 0.0, 0.0, 1.0);
            var14.a((double)(scrollBarXEnd - 1), (double)(var16 + var12 - 1), 0.0, 1.0, 1.0);
            var14.a((double)(scrollBarXEnd - 1), (double)var16, 0.0, 1.0, 0.0);
            var14.a((double)scrollBarXStart, (double)var16, 0.0, 0.0, 0.0);
            var14.a();
        }
        this.func_27257_b(mouseX, mouseY);
        GL11.glEnable(3553);
        GL11.glShadeModel(7424);
        GL11.glEnable(3008);
        GL11.glDisable(3042);
    }
    
    private void overlayBackground(final int p_22239_1_, final int p_22239_2_, final int p_22239_3_, final int p_22239_4_) {
        final bfq var5 = bfq.a;
        this.client.N.a(avk.k);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float var6 = 32.0f;
        var5.b();
        var5.a(4210752, p_22239_4_);
        var5.a(0.0, (double)p_22239_2_, 0.0, 0.0, (double)(p_22239_2_ / var6));
        var5.a(this.listWidth + 30.0, (double)p_22239_2_, 0.0, (double)((this.listWidth + 30) / var6), (double)(p_22239_2_ / var6));
        var5.a(4210752, p_22239_3_);
        var5.a(this.listWidth + 30.0, (double)p_22239_1_, 0.0, (double)((this.listWidth + 30) / var6), (double)(p_22239_1_ / var6));
        var5.a(0.0, (double)p_22239_1_, 0.0, 0.0, (double)(p_22239_1_ / var6));
        var5.a();
    }
}
