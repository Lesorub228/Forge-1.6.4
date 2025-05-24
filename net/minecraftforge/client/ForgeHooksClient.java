// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraftforge.fluids.RenderBlockFluid;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.ForgeDummyContainer;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import javax.imageio.ImageIO;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.Event;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import java.util.Random;
import cpw.mods.fml.client.FMLClientHandler;

public class ForgeHooksClient
{
    private static final bjo ITEM_GLINT;
    static int renderPass;
    static int stencilBits;
    private static int skyX;
    private static int skyZ;
    private static boolean skyInit;
    private static int skyRGBMultiplier;
    
    static bim engine() {
        return FMLClientHandler.instance().getClient().N;
    }
    
    @Deprecated
    public static String getArmorTexture(final nn entity, final ye armor, final String _default, final int slot, final int layer, final String type) {
        return getArmorTexture(entity, armor, _default, slot, type);
    }
    
    public static String getArmorTexture(final nn entity, final ye armor, final String _default, final int slot, final String type) {
        final String result = armor.b().getArmorTexture(armor, entity, slot, type);
        return (result != null) ? result : _default;
    }
    
    public static boolean renderEntityItem(final ss entity, final ye item, final float bobing, final float rotation, final Random random, final bim engine, final bfr renderBlocks) {
        final IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, IItemRenderer.ItemRenderType.ENTITY);
        if (customRenderer == null) {
            return false;
        }
        if (customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.ENTITY, item, IItemRenderer.ItemRendererHelper.ENTITY_ROTATION)) {
            GL11.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
        }
        if (!customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.ENTITY, item, IItemRenderer.ItemRendererHelper.ENTITY_BOBBING)) {
            GL11.glTranslatef(0.0f, -bobing, 0.0f);
        }
        final boolean is3D = customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.ENTITY, item, IItemRenderer.ItemRendererHelper.BLOCK_3D);
        engine.a((item.d() == 0) ? bik.b : bik.c);
        final aqz block = (item.d < aqz.s.length) ? aqz.s[item.d] : null;
        if (is3D || (block != null && bfr.a(block.d()))) {
            final int renderType = (block != null) ? block.d() : 1;
            final float scale = (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2) ? 0.5f : 0.25f;
            if (bgw.g) {
                GL11.glScalef(1.25f, 1.25f, 1.25f);
                GL11.glTranslatef(0.0f, 0.05f, 0.0f);
                GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
            }
            GL11.glScalef(scale, scale, scale);
            final int size = item.b;
            for (int count = (size > 40) ? 5 : ((size > 20) ? 4 : ((size > 5) ? 3 : ((size > 1) ? 2 : 1))), j = 0; j < count; ++j) {
                GL11.glPushMatrix();
                if (j > 0) {
                    GL11.glTranslatef((random.nextFloat() * 2.0f - 1.0f) * 0.2f / scale, (random.nextFloat() * 2.0f - 1.0f) * 0.2f / scale, (random.nextFloat() * 2.0f - 1.0f) * 0.2f / scale);
                }
                customRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, item, renderBlocks, entity);
                GL11.glPopMatrix();
            }
        }
        else {
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            customRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, item, renderBlocks, entity);
        }
        return true;
    }
    
    public static boolean renderInventoryItem(final bfr renderBlocks, final bim engine, final ye item, final boolean inColor, final float zLevel, final float x, final float y) {
        final IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, IItemRenderer.ItemRenderType.INVENTORY);
        if (customRenderer == null) {
            return false;
        }
        engine.a((item.d() == 0) ? bik.b : bik.c);
        if (customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.INVENTORY, item, IItemRenderer.ItemRendererHelper.INVENTORY_BLOCK)) {
            GL11.glPushMatrix();
            GL11.glTranslatef(x - 2.0f, y + 3.0f, -3.0f + zLevel);
            GL11.glScalef(10.0f, 10.0f, 10.0f);
            GL11.glTranslatef(1.0f, 0.5f, 1.0f);
            GL11.glScalef(1.0f, 1.0f, -1.0f);
            GL11.glRotatef(210.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            if (inColor) {
                final int color = yc.g[item.d].a(item, 0);
                final float r = (color >> 16 & 0xFF) / 255.0f;
                final float g = (color >> 8 & 0xFF) / 255.0f;
                final float b = (color & 0xFF) / 255.0f;
                GL11.glColor4f(r, g, b, 1.0f);
            }
            GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
            renderBlocks.c = inColor;
            customRenderer.renderItem(IItemRenderer.ItemRenderType.INVENTORY, item, renderBlocks);
            renderBlocks.c = true;
            GL11.glPopMatrix();
        }
        else {
            GL11.glDisable(2896);
            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, -3.0f + zLevel);
            if (inColor) {
                final int color = yc.g[item.d].a(item, 0);
                final float r = (color >> 16 & 0xFF) / 255.0f;
                final float g = (color >> 8 & 0xFF) / 255.0f;
                final float b = (color & 0xFF) / 255.0f;
                GL11.glColor4f(r, g, b, 1.0f);
            }
            customRenderer.renderItem(IItemRenderer.ItemRenderType.INVENTORY, item, renderBlocks);
            GL11.glPopMatrix();
            GL11.glEnable(2896);
        }
        return true;
    }
    
    public static void renderEffectOverlay(final bim manager, final bgw render) {
    }
    
    public static void renderEquippedItem(final IItemRenderer.ItemRenderType type, final IItemRenderer customRenderer, final bfr renderBlocks, final of entity, final ye item) {
        if (customRenderer.shouldUseRenderHelper(type, item, IItemRenderer.ItemRendererHelper.EQUIPPED_BLOCK)) {
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
            customRenderer.renderItem(type, item, renderBlocks, entity);
            GL11.glPopMatrix();
        }
        else {
            GL11.glPushMatrix();
            GL11.glEnable(32826);
            GL11.glTranslatef(0.0f, -0.3f, 0.0f);
            GL11.glScalef(1.5f, 1.5f, 1.5f);
            GL11.glRotatef(50.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(335.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(-0.9375f, -0.0625f, 0.0f);
            customRenderer.renderItem(type, item, renderBlocks, entity);
            GL11.glDisable(32826);
            GL11.glPopMatrix();
        }
    }
    
    public static void orientBedCamera(final atv mc, final of entity) {
        final int x = ls.c(entity.u);
        final int y = ls.c(entity.v);
        final int z = ls.c(entity.w);
        final aqz block = aqz.s[mc.f.a(x, y, z)];
        if (block != null && block.isBed((abw)mc.f, x, y, z, entity)) {
            final int var12 = block.getBedDirection((acf)mc.f, x, y, z);
            GL11.glRotatef((float)(var12 * 90), 0.0f, 1.0f, 0.0f);
        }
    }
    
    public static boolean onDrawBlockHighlight(final bfl context, final uf player, final ata target, final int subID, final ye currentItem, final float partialTicks) {
        return MinecraftForge.EVENT_BUS.post(new DrawBlockHighlightEvent(context, player, target, subID, currentItem, partialTicks));
    }
    
    public static void dispatchRenderLast(final bfl context, final float partialTicks) {
        MinecraftForge.EVENT_BUS.post(new RenderWorldLastEvent(context, partialTicks));
    }
    
    public static void onTextureStitchedPre(final bik map) {
        MinecraftForge.EVENT_BUS.post(new TextureStitchEvent.Pre(map));
    }
    
    public static void onTextureStitchedPost(final bik map) {
        MinecraftForge.EVENT_BUS.post(new TextureStitchEvent.Post(map));
        FluidRegistry.WATER.setIcons(apc.b("water_still"), apc.b("water_flow"));
        FluidRegistry.LAVA.setIcons(apc.b("lava_still"), apc.b("lava_flow"));
    }
    
    public static void onTextureLoadPre(final String texture) {
        if (bfq.renderingWorldRenderer) {
            final String msg = String.format("Warning: Texture %s not preloaded, will cause render glitches!", texture);
            System.out.println(msg);
            if (bfq.class.getPackage() != null && bfq.class.getPackage().getName().startsWith("net.minecraft.")) {
                final atv mc = FMLClientHandler.instance().getClient();
                if (mc.r != null) {
                    mc.r.b().a(msg);
                }
            }
        }
    }
    
    public static void setRenderPass(final int pass) {
        ForgeHooksClient.renderPass = pass;
    }
    
    public static bbj getArmorModel(final of entityLiving, final ye itemStack, final int slotID, final bbj _default) {
        final bbj modelbiped = itemStack.b().getArmorModel(entityLiving, itemStack, slotID);
        return (modelbiped == null) ? _default : modelbiped;
    }
    
    public static void createDisplay() throws LWJGLException {
        ImageIO.setUseCache(false);
        final PixelFormat format = new PixelFormat().withDepthBits(24);
        try {
            Display.create(format.withStencilBits(8));
            ForgeHooksClient.stencilBits = 8;
        }
        catch (final LWJGLException e) {
            Display.create(format);
            ForgeHooksClient.stencilBits = 0;
        }
    }
    
    public static String fixDomain(final String base, final String complex) {
        final int idx = complex.indexOf(58);
        if (idx == -1) {
            return base + complex;
        }
        final String name = complex.substring(idx + 1, complex.length());
        if (idx > 1) {
            final String domain = complex.substring(0, idx);
            return domain + ':' + base + name;
        }
        return base + name;
    }
    
    public static boolean postMouseEvent() {
        return MinecraftForge.EVENT_BUS.post(new MouseEvent());
    }
    
    public static float getOffsetFOV(final bex entity, final float fov) {
        final FOVUpdateEvent fovUpdateEvent = new FOVUpdateEvent(entity, fov);
        MinecraftForge.EVENT_BUS.post(fovUpdateEvent);
        return fovUpdateEvent.newfov;
    }
    
    public static int getSkyBlendColour(final abw world, final int playerX, final int playerZ) {
        if (playerX == ForgeHooksClient.skyX && playerZ == ForgeHooksClient.skyZ && ForgeHooksClient.skyInit) {
            return ForgeHooksClient.skyRGBMultiplier;
        }
        ForgeHooksClient.skyInit = true;
        final int distance = atv.w().u.j ? ForgeDummyContainer.blendRanges[atv.w().u.e] : 0;
        int r = 0;
        int g = 0;
        int b = 0;
        int divider = 0;
        for (int x = -distance; x <= distance; ++x) {
            for (int z = -distance; z <= distance; ++z) {
                final acq biome = world.a(playerX + x, playerZ + z);
                final int colour = biome.a(biome.j());
                r += (colour & 0xFF0000) >> 16;
                g += (colour & 0xFF00) >> 8;
                b += (colour & 0xFF);
                ++divider;
            }
        }
        final int multiplier = (r / divider & 0xFF) << 16 | (g / divider & 0xFF) << 8 | (b / divider & 0xFF);
        ForgeHooksClient.skyX = playerX;
        ForgeHooksClient.skyZ = playerZ;
        return ForgeHooksClient.skyRGBMultiplier = multiplier;
    }
    
    static {
        ITEM_GLINT = new bjo("textures/misc/enchanted_item_glint.png");
        ForgeHooksClient.renderPass = -1;
        ForgeHooksClient.stencilBits = 0;
        FluidRegistry.renderIdFluid = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(RenderBlockFluid.instance);
    }
}
