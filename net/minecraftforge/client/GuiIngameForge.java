// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client;

import java.util.List;
import java.awt.Color;
import java.util.Iterator;
import net.minecraftforge.event.Event;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import java.util.ArrayList;
import net.minecraftforge.common.ForgeHooks;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class GuiIngameForge extends avj
{
    private static final bjo VIGNETTE;
    private static final bjo WIDGITS;
    private static final bjo PUMPKIN_BLUR;
    private static final int WHITE = 16777215;
    public static boolean renderHelmet;
    public static boolean renderPortal;
    public static boolean renderHotbar;
    public static boolean renderCrosshairs;
    public static boolean renderBossHealth;
    public static boolean renderHealth;
    public static boolean renderArmor;
    public static boolean renderFood;
    public static boolean renderHealthMount;
    public static boolean renderAir;
    public static boolean renderExperiance;
    public static boolean renderJumpBar;
    public static boolean renderObjective;
    public static int left_height;
    public static int right_height;
    private awf res;
    private avi fontrenderer;
    private RenderGameOverlayEvent eventParent;
    private static final String MC_VERSION;
    
    public GuiIngameForge(final atv mc) {
        super(mc);
        this.res = null;
        this.fontrenderer = null;
    }
    
    public void a(final float partialTicks, final boolean hasScreen, final int mouseX, final int mouseY) {
        this.res = new awf(this.g.u, this.g.d, this.g.e);
        this.eventParent = new RenderGameOverlayEvent(partialTicks, this.res, mouseX, mouseY);
        final int width = this.res.a();
        final int height = this.res.b();
        GuiIngameForge.renderHealthMount = (this.g.h.o instanceof of);
        GuiIngameForge.renderFood = (this.g.h.o == null);
        GuiIngameForge.renderJumpBar = this.g.h.u();
        GuiIngameForge.right_height = 39;
        GuiIngameForge.left_height = 39;
        if (this.pre(RenderGameOverlayEvent.ElementType.ALL)) {
            return;
        }
        this.fontrenderer = this.g.l;
        this.g.p.c();
        GL11.glEnable(3042);
        if (atv.s()) {
            this.a(this.g.h.d(partialTicks), width, height);
        }
        else {
            GL11.glBlendFunc(770, 771);
        }
        if (GuiIngameForge.renderHelmet) {
            this.renderHelmet(this.res, partialTicks, hasScreen, mouseX, mouseY);
        }
        if (GuiIngameForge.renderPortal && !this.g.h.a(ni.k)) {
            this.renderPortal(width, height, partialTicks);
        }
        if (!this.g.c.a()) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.n = -90.0f;
            this.f.setSeed(this.i * 312871);
            if (GuiIngameForge.renderCrosshairs) {
                this.renderCrosshairs(width, height);
            }
            if (GuiIngameForge.renderBossHealth) {
                this.d();
            }
            if (this.g.c.b()) {
                if (GuiIngameForge.renderHealth) {
                    this.renderHealth(width, height);
                }
                if (GuiIngameForge.renderArmor) {
                    this.renderArmor(width, height);
                }
                if (GuiIngameForge.renderFood) {
                    this.renderFood(width, height);
                }
                if (GuiIngameForge.renderHealthMount) {
                    this.renderHealthMount(width, height);
                }
                if (GuiIngameForge.renderAir) {
                    this.renderAir(width, height);
                }
            }
            if (GuiIngameForge.renderHotbar) {
                this.renderHotbar(width, height, partialTicks);
            }
        }
        if (GuiIngameForge.renderJumpBar) {
            this.renderJumpBar(width, height);
        }
        else if (GuiIngameForge.renderExperiance) {
            this.renderExperience(width, height);
        }
        this.renderSleepFade(width, height);
        this.renderToolHightlight(width, height);
        this.renderHUDText(width, height);
        this.renderRecordOverlay(width, height, partialTicks);
        final ate objective = this.g.f.X().a(1);
        if (GuiIngameForge.renderObjective && objective != null) {
            this.a(objective, height, width, this.fontrenderer);
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        this.renderChat(width, height);
        this.renderPlayerList(width, height);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(2896);
        GL11.glEnable(3008);
        this.post(RenderGameOverlayEvent.ElementType.ALL);
    }
    
    public awf getResolution() {
        return this.res;
    }
    
    protected void renderHotbar(final int width, final int height, final float partialTicks) {
        if (this.pre(RenderGameOverlayEvent.ElementType.HOTBAR)) {
            return;
        }
        this.g.C.a("actionBar");
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.g.N.a(GuiIngameForge.WIDGITS);
        final ud inv = this.g.h.bn;
        this.b(width / 2 - 91, height - 22, 0, 0, 182, 22);
        this.b(width / 2 - 91 - 1 + inv.c * 20, height - 22 - 1, 0, 22, 24, 22);
        GL11.glDisable(3042);
        GL11.glEnable(32826);
        att.c();
        for (int i = 0; i < 9; ++i) {
            final int x = width / 2 - 90 + i * 20 + 2;
            final int z = height - 16 - 3;
            this.a(i, x, z, partialTicks);
        }
        att.a();
        GL11.glDisable(32826);
        this.g.C.b();
        this.post(RenderGameOverlayEvent.ElementType.HOTBAR);
    }
    
    protected void renderCrosshairs(final int width, final int height) {
        if (this.pre(RenderGameOverlayEvent.ElementType.CROSSHAIRS)) {
            return;
        }
        this.bind(avk.m);
        GL11.glEnable(3042);
        GL11.glBlendFunc(775, 769);
        this.b(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
        GL11.glDisable(3042);
        this.post(RenderGameOverlayEvent.ElementType.CROSSHAIRS);
    }
    
    protected void d() {
        if (this.pre(RenderGameOverlayEvent.ElementType.BOSSHEALTH)) {
            return;
        }
        this.g.C.a("bossHealth");
        super.d();
        this.g.C.b();
        this.post(RenderGameOverlayEvent.ElementType.BOSSHEALTH);
    }
    
    private void renderHelmet(final awf res, final float partialTicks, final boolean hasScreen, final int mouseX, final int mouseY) {
        if (this.pre(RenderGameOverlayEvent.ElementType.HELMET)) {
            return;
        }
        final ye itemstack = this.g.h.bn.f(3);
        if (this.g.u.aa == 0 && itemstack != null && itemstack.b() != null) {
            if (itemstack.d == aqz.bf.cF) {
                this.b(res.a(), res.b());
            }
            else {
                itemstack.b().renderHelmetOverlay(itemstack, (uf)this.g.h, res, partialTicks, hasScreen, mouseX, mouseY);
            }
        }
        this.post(RenderGameOverlayEvent.ElementType.HELMET);
    }
    
    protected void renderArmor(final int width, final int height) {
        if (this.pre(RenderGameOverlayEvent.ElementType.ARMOR)) {
            return;
        }
        this.g.C.a("armor");
        int left = width / 2 - 91;
        final int top = height - GuiIngameForge.left_height;
        for (int level = ForgeHooks.getTotalArmorValue((uf)this.g.h), i = 1; level > 0 && i < 20; i += 2) {
            if (i < level) {
                this.b(left, top, 34, 9, 9, 9);
            }
            else if (i == level) {
                this.b(left, top, 25, 9, 9, 9);
            }
            else if (i > level) {
                this.b(left, top, 16, 9, 9, 9);
            }
            left += 8;
        }
        GuiIngameForge.left_height += 10;
        this.g.C.b();
        this.post(RenderGameOverlayEvent.ElementType.ARMOR);
    }
    
    protected void renderPortal(final int width, final int height, final float partialTicks) {
        if (this.pre(RenderGameOverlayEvent.ElementType.PORTAL)) {
            return;
        }
        final float f1 = this.g.h.bO + (this.g.h.bN - this.g.h.bO) * partialTicks;
        if (f1 > 0.0f) {
            this.b(f1, width, height);
        }
        this.post(RenderGameOverlayEvent.ElementType.PORTAL);
    }
    
    protected void renderAir(final int width, final int height) {
        if (this.pre(RenderGameOverlayEvent.ElementType.AIR)) {
            return;
        }
        this.g.C.a("air");
        final int left = width / 2 + 91;
        final int top = height - GuiIngameForge.right_height;
        if (this.g.h.a(akc.h)) {
            final int air = this.g.h.al();
            for (int full = ls.f((air - 2) * 10.0 / 300.0), partial = ls.f(air * 10.0 / 300.0) - full, i = 0; i < full + partial; ++i) {
                this.b(left - i * 8 - 9, top, (i < full) ? 16 : 25, 18, 9, 9);
            }
            GuiIngameForge.right_height += 10;
        }
        this.g.C.b();
        this.post(RenderGameOverlayEvent.ElementType.AIR);
    }
    
    public void renderHealth(final int width, final int height) {
        this.bind(GuiIngameForge.m);
        if (this.pre(RenderGameOverlayEvent.ElementType.HEALTH)) {
            return;
        }
        this.g.C.a("health");
        boolean highlight = this.g.h.af / 3 % 2 == 1;
        if (this.g.h.af < 10) {
            highlight = false;
        }
        final os attrMaxHealth = this.g.h.a(tp.a);
        final int health = ls.f(this.g.h.aN());
        final int healthLast = ls.f(this.g.h.ax);
        final float healthMax = (float)attrMaxHealth.e();
        final float absorb = this.g.h.bn();
        final int healthRows = ls.f((healthMax + absorb) / 2.0f / 10.0f);
        final int rowHeight = Math.max(10 - (healthRows - 2), 3);
        this.f.setSeed(this.i * 312871);
        final int left = width / 2 - 91;
        final int top = height - GuiIngameForge.left_height;
        GuiIngameForge.left_height += healthRows * rowHeight;
        if (rowHeight != 10) {
            GuiIngameForge.left_height += 10 - rowHeight;
        }
        int regen = -1;
        if (this.g.h.a(ni.l)) {
            regen = this.i % 25;
        }
        final int TOP = 9 * (this.g.f.N().t() ? 5 : 0);
        final int BACKGROUND = highlight ? 25 : 16;
        int MARGIN = 16;
        if (this.g.h.a(ni.u)) {
            MARGIN += 36;
        }
        else if (this.g.h.a(ni.v)) {
            MARGIN += 72;
        }
        float absorbRemaining = absorb;
        for (int i = ls.f((healthMax + absorb) / 2.0f) - 1; i >= 0; --i) {
            final int b0 = highlight ? 1 : 0;
            final int row = ls.f((i + 1) / 10.0f) - 1;
            final int x = left + i % 10 * 8;
            int y = top - row * rowHeight;
            if (health <= 4) {
                y += this.f.nextInt(2);
            }
            if (i == regen) {
                y -= 2;
            }
            this.b(x, y, BACKGROUND, TOP, 9, 9);
            if (highlight) {
                if (i * 2 + 1 < healthLast) {
                    this.b(x, y, MARGIN + 54, TOP, 9, 9);
                }
                else if (i * 2 + 1 == healthLast) {
                    this.b(x, y, MARGIN + 63, TOP, 9, 9);
                }
            }
            if (absorbRemaining > 0.0f) {
                if (absorbRemaining == absorb && absorb % 2.0f == 1.0f) {
                    this.b(x, y, MARGIN + 153, TOP, 9, 9);
                }
                else {
                    this.b(x, y, MARGIN + 144, TOP, 9, 9);
                }
                absorbRemaining -= 2.0f;
            }
            else if (i * 2 + 1 < health) {
                this.b(x, y, MARGIN + 36, TOP, 9, 9);
            }
            else if (i * 2 + 1 == health) {
                this.b(x, y, MARGIN + 45, TOP, 9, 9);
            }
        }
        this.g.C.b();
        this.post(RenderGameOverlayEvent.ElementType.HEALTH);
    }
    
    public void renderFood(final int width, final int height) {
        if (this.pre(RenderGameOverlayEvent.ElementType.FOOD)) {
            return;
        }
        this.g.C.a("food");
        final int left = width / 2 + 91;
        final int top = height - GuiIngameForge.right_height;
        GuiIngameForge.right_height += 10;
        final boolean unused = false;
        final ux stats = this.g.h.bI();
        final int level = stats.a();
        final int levelLast = stats.b();
        for (int i = 0; i < 10; ++i) {
            final int idx = i * 2 + 1;
            final int x = left - i * 8 - 9;
            int y = top;
            int icon = 16;
            byte backgound = 0;
            if (this.g.h.a(ni.s)) {
                icon += 36;
                backgound = 13;
            }
            if (unused) {
                backgound = 1;
            }
            if (this.g.h.bI().e() <= 0.0f && this.i % (level * 3 + 1) == 0) {
                y = top + (this.f.nextInt(3) - 1);
            }
            this.b(x, y, 16 + backgound * 9, 27, 9, 9);
            if (unused) {
                if (idx < levelLast) {
                    this.b(x, y, icon + 54, 27, 9, 9);
                }
                else if (idx == levelLast) {
                    this.b(x, y, icon + 63, 27, 9, 9);
                }
            }
            if (idx < level) {
                this.b(x, y, icon + 36, 27, 9, 9);
            }
            else if (idx == level) {
                this.b(x, y, icon + 45, 27, 9, 9);
            }
        }
        this.g.C.b();
        this.post(RenderGameOverlayEvent.ElementType.FOOD);
    }
    
    protected void renderSleepFade(final int width, final int height) {
        if (this.g.h.bE() > 0) {
            this.g.C.a("sleep");
            GL11.glDisable(2929);
            GL11.glDisable(3008);
            final int sleepTime = this.g.h.bE();
            float opacity = sleepTime / 100.0f;
            if (opacity > 1.0f) {
                opacity = 1.0f - (sleepTime - 100) / 10.0f;
            }
            final int color = (int)(220.0f * opacity) << 24 | 0x101020;
            a(0, 0, width, height, color);
            GL11.glEnable(3008);
            GL11.glEnable(2929);
            this.g.C.b();
        }
    }
    
    protected void renderExperience(final int width, final int height) {
        this.bind(GuiIngameForge.m);
        if (this.pre(RenderGameOverlayEvent.ElementType.EXPERIENCE)) {
            return;
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.g.c.f()) {
            this.g.C.a("expBar");
            final int cap = this.g.h.bH();
            final int left = width / 2 - 91;
            if (cap > 0) {
                final short barWidth = 182;
                final int filled = (int)(this.g.h.bJ * (barWidth + 1));
                final int top = height - 32 + 3;
                this.b(left, top, 0, 64, (int)barWidth, 5);
                if (filled > 0) {
                    this.b(left, top, 0, 69, filled, 5);
                }
            }
            this.g.C.b();
            if (this.g.c.f() && this.g.h.bH > 0) {
                this.g.C.a("expLevel");
                final boolean flag1 = false;
                final int color = flag1 ? 16777215 : 8453920;
                final String text = "" + this.g.h.bH;
                final int x = (width - this.fontrenderer.a(text)) / 2;
                final int y = height - 31 - 4;
                this.fontrenderer.b(text, x + 1, y, 0);
                this.fontrenderer.b(text, x - 1, y, 0);
                this.fontrenderer.b(text, x, y + 1, 0);
                this.fontrenderer.b(text, x, y - 1, 0);
                this.fontrenderer.b(text, x, y, color);
                this.g.C.b();
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.post(RenderGameOverlayEvent.ElementType.EXPERIENCE);
    }
    
    protected void renderJumpBar(final int width, final int height) {
        this.bind(GuiIngameForge.m);
        if (this.pre(RenderGameOverlayEvent.ElementType.JUMPBAR)) {
            return;
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.g.C.a("jumpBar");
        final float charge = this.g.h.bN();
        final int barWidth = 182;
        final int x = width / 2 - 91;
        final int filled = (int)(charge * 183.0f);
        final int top = height - 32 + 3;
        this.b(x, top, 0, 84, 182, 5);
        if (filled > 0) {
            this.b(x, top, 0, 89, filled, 5);
        }
        this.g.C.b();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.post(RenderGameOverlayEvent.ElementType.JUMPBAR);
    }
    
    protected void renderToolHightlight(final int width, final int height) {
        if (this.g.u.D) {
            this.g.C.a("toolHighlight");
            if (this.q > 0 && this.r != null) {
                final String name = this.r.s();
                int opacity = (int)(this.q * 256.0f / 10.0f);
                if (opacity > 255) {
                    opacity = 255;
                }
                if (opacity > 0) {
                    int y = height - 59;
                    if (!this.g.c.b()) {
                        y += 14;
                    }
                    GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    final avi font = this.r.b().getFontRenderer(this.r);
                    if (font != null) {
                        final int x = (width - font.a(name)) / 2;
                        font.a(name, x, y, 0xFFFFFF | opacity << 24);
                    }
                    else {
                        final int x = (width - this.fontrenderer.a(name)) / 2;
                        this.fontrenderer.a(name, x, y, 0xFFFFFF | opacity << 24);
                    }
                    GL11.glDisable(3042);
                    GL11.glPopMatrix();
                }
            }
            this.g.C.b();
        }
    }
    
    protected void renderHUDText(final int width, final int height) {
        this.g.C.a("forgeHudText");
        final ArrayList<String> left = new ArrayList<String>();
        final ArrayList<String> right = new ArrayList<String>();
        if (this.g.p()) {
            final long time = this.g.f.I();
            if (time >= 120500L) {
                right.add(bu.a("demo.demoExpired"));
            }
            else {
                right.add(String.format(bu.a("demo.remainingTime"), ma.a((int)(120500L - time))));
            }
        }
        if (this.g.u.ab) {
            this.g.C.a("debug");
            GL11.glPushMatrix();
            left.add("Minecraft " + GuiIngameForge.MC_VERSION + " (" + this.g.E + ")");
            left.add(this.g.l());
            left.add(this.g.m());
            left.add(this.g.o());
            left.add(this.g.n());
            left.add(null);
            final long max = Runtime.getRuntime().maxMemory();
            final long total = Runtime.getRuntime().totalMemory();
            final long free = Runtime.getRuntime().freeMemory();
            final long used = total - free;
            right.add("Used memory: " + used * 100L / max + "% (" + used / 1024L / 1024L + "MB) of " + max / 1024L / 1024L + "MB");
            right.add("Allocated memory: " + total * 100L / max + "% (" + total / 1024L / 1024L + "MB)");
            final int x = ls.c(this.g.h.u);
            final int y = ls.c(this.g.h.v);
            final int z = ls.c(this.g.h.w);
            final float yaw = this.g.h.A;
            final int heading = ls.c(this.g.h.A * 4.0f / 360.0f + 0.5) & 0x3;
            left.add(String.format("x: %.5f (%d) // c: %d (%d)", this.g.h.u, x, x >> 4, x & 0xF));
            left.add(String.format("y: %.3f (feet pos, %.3f eyes pos)", this.g.h.E.b, this.g.h.v));
            left.add(String.format("z: %.5f (%d) // c: %d (%d)", this.g.h.w, z, z >> 4, z & 0xF));
            left.add(String.format("f: %d (%s) / %f", heading, r.c[heading], ls.g(yaw)));
            if (this.g.f != null && this.g.f.f(x, y, z)) {
                final adr chunk = this.g.f.d(x, z);
                left.add(String.format("lc: %d b: %s bl: %d sl: %d rl: %d", chunk.h() + 15, chunk.a(x & 0xF, z & 0xF, this.g.f.u()).y, chunk.a(ach.b, x & 0xF, y, z & 0xF), chunk.a(ach.a, x & 0xF, y, z & 0xF), chunk.c(x & 0xF, y, z & 0xF, 0)));
            }
            else {
                left.add(null);
            }
            left.add(String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", this.g.h.bG.b(), this.g.h.bG.a(), this.g.h.F, this.g.f.f(x, z)));
            right.add(null);
            for (final String s : FMLCommonHandler.instance().getBrandings().subList(1, FMLCommonHandler.instance().getBrandings().size())) {
                right.add(s);
            }
            GL11.glPopMatrix();
            this.g.C.b();
        }
        final RenderGameOverlayEvent.Text event = new RenderGameOverlayEvent.Text(this.eventParent, left, right);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            for (int x2 = 0; x2 < left.size(); ++x2) {
                final String msg = left.get(x2);
                if (msg != null) {
                    this.fontrenderer.a(msg, 2, 2 + x2 * 10, 16777215);
                }
            }
            for (int x2 = 0; x2 < right.size(); ++x2) {
                final String msg = right.get(x2);
                if (msg != null) {
                    final int w = this.fontrenderer.a(msg);
                    this.fontrenderer.a(msg, width - w - 10, 2 + x2 * 10, 16777215);
                }
            }
        }
        this.g.C.b();
        this.post(RenderGameOverlayEvent.ElementType.TEXT);
    }
    
    protected void renderRecordOverlay(final int width, final int height, final float partialTicks) {
        if (this.o > 0) {
            this.g.C.a("overlayMessage");
            final float hue = this.o - partialTicks;
            int opacity = (int)(hue * 256.0f / 20.0f);
            if (opacity > 255) {
                opacity = 255;
            }
            if (opacity > 0) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(width / 2), (float)(height - 48), 0.0f);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                final int color = this.p ? (Color.HSBtoRGB(hue / 50.0f, 0.7f, 0.6f) & 0xFFFFFF) : 16777215;
                this.fontrenderer.b(this.j, -this.fontrenderer.a(this.j) / 2, -4, color | opacity << 24);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }
            this.g.C.b();
        }
    }
    
    protected void renderChat(final int width, final int height) {
        this.g.C.a("chat");
        final RenderGameOverlayEvent.Chat event = new RenderGameOverlayEvent.Chat(this.eventParent, 0, height - 48);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)event.posX, (float)event.posY, 0.0f);
        this.h.a(this.i);
        GL11.glPopMatrix();
        this.post(RenderGameOverlayEvent.ElementType.CHAT);
        this.g.C.b();
    }
    
    protected void renderPlayerList(final int width, final int height) {
        final ate scoreobjective = this.g.f.X().a(0);
        final bcw handler = this.g.h.a;
        if (this.g.u.T.e && (!this.g.A() || handler.c.size() > 1 || scoreobjective != null)) {
            if (this.pre(RenderGameOverlayEvent.ElementType.PLAYER_LIST)) {
                return;
            }
            this.g.C.a("playerList");
            final List players = handler.c;
            int rows;
            int maxPlayers;
            int columns;
            for (maxPlayers = (rows = handler.d), columns = 1, columns = 1; rows > 20; rows = (maxPlayers + columns - 1) / columns) {
                ++columns;
            }
            int columnWidth = 300 / columns;
            if (columnWidth > 150) {
                columnWidth = 150;
            }
            final int left = (width - columns * columnWidth) / 2;
            final byte border = 10;
            a(left - 1, border - 1, left + columnWidth * columns, border + 9 * rows, Integer.MIN_VALUE);
            for (int i = 0; i < maxPlayers; ++i) {
                final int xPos = left + i % columns * columnWidth;
                final int yPos = border + i / columns * 9;
                a(xPos, yPos, xPos + columnWidth - 1, yPos + 8, 553648127);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glEnable(3008);
                if (i < players.size()) {
                    final bdj player = players.get(i);
                    final atf team = this.g.f.X().i(player.a);
                    final String displayName = atf.a((atl)team, player.a);
                    this.fontrenderer.a(displayName, xPos, yPos, 16777215);
                    if (scoreobjective != null) {
                        final int endX = xPos + this.fontrenderer.a(displayName) + 5;
                        final int maxX = xPos + columnWidth - 12 - 5;
                        if (maxX - endX > 5) {
                            final atg score = scoreobjective.a().a(player.a, scoreobjective);
                            final String scoreDisplay = a.o + "" + score.c();
                            this.fontrenderer.a(scoreDisplay, maxX - this.fontrenderer.a(scoreDisplay), yPos, 16777215);
                        }
                    }
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    this.g.J().a(avk.m);
                    int pingIndex = 4;
                    final int ping = player.b;
                    if (ping < 0) {
                        pingIndex = 5;
                    }
                    else if (ping < 150) {
                        pingIndex = 0;
                    }
                    else if (ping < 300) {
                        pingIndex = 1;
                    }
                    else if (ping < 600) {
                        pingIndex = 2;
                    }
                    else if (ping < 1000) {
                        pingIndex = 3;
                    }
                    this.n += 100.0f;
                    this.b(xPos + columnWidth - 12, yPos, 0, 176 + pingIndex * 8, 10, 8);
                    this.n -= 100.0f;
                }
            }
            this.post(RenderGameOverlayEvent.ElementType.PLAYER_LIST);
        }
    }
    
    protected void renderHealthMount(final int width, final int height) {
        final nn tmp = this.g.h.o;
        if (!(tmp instanceof of)) {
            return;
        }
        this.bind(GuiIngameForge.m);
        if (this.pre(RenderGameOverlayEvent.ElementType.HEALTHMOUNT)) {
            return;
        }
        final boolean unused = false;
        final int left_align = width / 2 + 91;
        this.g.C.c("mountHealth");
        final of mount = (of)tmp;
        final int health = (int)Math.ceil(mount.aN());
        final float healthMax = mount.aT();
        int hearts = (int)(healthMax + 0.5f) / 2;
        if (hearts > 30) {
            hearts = 30;
        }
        final int MARGIN = 52;
        final int BACKGROUND = 52 + (unused ? 1 : 0);
        final int HALF = 97;
        final int FULL = 88;
        int heart = 0;
        while (hearts > 0) {
            final int top = height - GuiIngameForge.right_height;
            final int rowCount = Math.min(hearts, 10);
            hearts -= rowCount;
            for (int i = 0; i < rowCount; ++i) {
                final int x = left_align - i * 8 - 9;
                this.b(x, top, BACKGROUND, 9, 9, 9);
                if (i * 2 + 1 + heart < health) {
                    this.b(x, top, 88, 9, 9, 9);
                }
                else if (i * 2 + 1 + heart == health) {
                    this.b(x, top, 97, 9, 9, 9);
                }
            }
            GuiIngameForge.right_height += 10;
            heart += 20;
        }
        this.post(RenderGameOverlayEvent.ElementType.HEALTHMOUNT);
    }
    
    private boolean pre(final RenderGameOverlayEvent.ElementType type) {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(this.eventParent, type));
    }
    
    private void post(final RenderGameOverlayEvent.ElementType type) {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(this.eventParent, type));
    }
    
    private void bind(final bjo res) {
        this.g.J().a(res);
    }
    
    static {
        VIGNETTE = new bjo("textures/misc/vignette.png");
        WIDGITS = new bjo("textures/gui/widgets.png");
        PUMPKIN_BLUR = new bjo("textures/misc/pumpkinblur.png");
        GuiIngameForge.renderHelmet = true;
        GuiIngameForge.renderPortal = true;
        GuiIngameForge.renderHotbar = true;
        GuiIngameForge.renderCrosshairs = true;
        GuiIngameForge.renderBossHealth = true;
        GuiIngameForge.renderHealth = true;
        GuiIngameForge.renderArmor = true;
        GuiIngameForge.renderFood = true;
        GuiIngameForge.renderHealthMount = true;
        GuiIngameForge.renderAir = true;
        GuiIngameForge.renderExperiance = true;
        GuiIngameForge.renderJumpBar = true;
        GuiIngameForge.renderObjective = true;
        GuiIngameForge.left_height = 39;
        GuiIngameForge.right_height = 39;
        MC_VERSION = new c((b)null).a();
    }
}
