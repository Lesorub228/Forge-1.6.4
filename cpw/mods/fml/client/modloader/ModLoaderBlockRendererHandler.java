// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client.modloader;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class ModLoaderBlockRendererHandler implements ISimpleBlockRenderingHandler
{
    private int renderId;
    private boolean render3dInInventory;
    private BaseMod mod;
    
    public ModLoaderBlockRendererHandler(final int renderId, final boolean render3dInInventory, final BaseMod mod) {
        this.renderId = renderId;
        this.render3dInInventory = render3dInInventory;
        this.mod = mod;
    }
    
    @Override
    public int getRenderId() {
        return this.renderId;
    }
    
    @Override
    public boolean shouldRender3DInInventory() {
        return this.render3dInInventory;
    }
    
    @Override
    public boolean renderWorldBlock(final acf world, final int x, final int y, final int z, final aqz block, final int modelId, final bfr renderer) {
        return this.mod.renderWorldBlock(renderer, world, x, y, z, block, modelId);
    }
    
    @Override
    public void renderInventoryBlock(final aqz block, final int metadata, final int modelID, final bfr renderer) {
        this.mod.renderInvBlock(renderer, block, metadata, modelID);
    }
}
