// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client.registry;

public interface ISimpleBlockRenderingHandler
{
    void renderInventoryBlock(final aqz p0, final int p1, final int p2, final bfr p3);
    
    boolean renderWorldBlock(final acf p0, final int p1, final int p2, final int p3, final aqz p4, final int p5, final bfr p6);
    
    boolean shouldRender3DInInventory();
    
    int getRenderId();
}
