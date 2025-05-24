// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client;

import java.util.BitSet;

public class MinecraftForgeClient
{
    private static IItemRenderer[] customItemRenderers;
    private static BitSet stencilBits;
    
    public static void registerItemRenderer(final int itemID, final IItemRenderer renderer) {
        MinecraftForgeClient.customItemRenderers[itemID] = renderer;
    }
    
    public static IItemRenderer getItemRenderer(final ye item, final IItemRenderer.ItemRenderType type) {
        final IItemRenderer renderer = MinecraftForgeClient.customItemRenderers[item.d];
        if (renderer != null && renderer.handleRenderType(item, type)) {
            return MinecraftForgeClient.customItemRenderers[item.d];
        }
        return null;
    }
    
    public static int getRenderPass() {
        return ForgeHooksClient.renderPass;
    }
    
    public static int getStencilBits() {
        return ForgeHooksClient.stencilBits;
    }
    
    public static int reserveStencilBit() {
        final int bit = MinecraftForgeClient.stencilBits.nextSetBit(0);
        if (bit >= 0) {
            MinecraftForgeClient.stencilBits.clear(bit);
        }
        return bit;
    }
    
    public static void releaseStencilBit(final int bit) {
        if (bit >= 0 && bit < getStencilBits()) {
            MinecraftForgeClient.stencilBits.set(bit);
        }
    }
    
    static {
        MinecraftForgeClient.customItemRenderers = new IItemRenderer[yc.g.length];
        (MinecraftForgeClient.stencilBits = new BitSet(getStencilBits())).set(0, getStencilBits());
    }
}
