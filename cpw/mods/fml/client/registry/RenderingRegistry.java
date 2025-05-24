// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client.registry;

import java.util.Iterator;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

public class RenderingRegistry
{
    private static final RenderingRegistry INSTANCE;
    private int nextRenderId;
    private Map<Integer, ISimpleBlockRenderingHandler> blockRenderers;
    private List<EntityRendererInfo> entityRenderers;
    
    public RenderingRegistry() {
        this.nextRenderId = 40;
        this.blockRenderers = Maps.newHashMap();
        this.entityRenderers = Lists.newArrayList();
    }
    
    public static int addNewArmourRendererPrefix(final String armor) {
        bgu.l = (String[])ObjectArrays.concat((Object[])bgu.l, (Object)armor);
        return bgu.l.length - 1;
    }
    
    public static void registerEntityRenderingHandler(final Class<? extends nn> entityClass, final bgm renderer) {
        instance().entityRenderers.add(new EntityRendererInfo(entityClass, renderer));
    }
    
    public static void registerBlockHandler(final ISimpleBlockRenderingHandler handler) {
        instance().blockRenderers.put(handler.getRenderId(), handler);
    }
    
    public static void registerBlockHandler(final int renderId, final ISimpleBlockRenderingHandler handler) {
        instance().blockRenderers.put(renderId, handler);
    }
    
    public static int getNextAvailableRenderId() {
        return instance().nextRenderId++;
    }
    
    @Deprecated
    public static int addTextureOverride(final String fileToOverride, final String fileToAdd) {
        return -1;
    }
    
    public static void addTextureOverride(final String path, final String overlayPath, final int index) {
    }
    
    @Deprecated
    public static int getUniqueTextureIndex(final String path) {
        return -1;
    }
    
    @Deprecated
    public static RenderingRegistry instance() {
        return RenderingRegistry.INSTANCE;
    }
    
    public boolean renderWorldBlock(final bfr renderer, final acf world, final int x, final int y, final int z, final aqz block, final int modelId) {
        if (!this.blockRenderers.containsKey(modelId)) {
            return false;
        }
        final ISimpleBlockRenderingHandler bri = this.blockRenderers.get(modelId);
        return bri.renderWorldBlock(world, x, y, z, block, modelId, renderer);
    }
    
    public void renderInventoryBlock(final bfr renderer, final aqz block, final int metadata, final int modelID) {
        if (!this.blockRenderers.containsKey(modelID)) {
            return;
        }
        final ISimpleBlockRenderingHandler bri = this.blockRenderers.get(modelID);
        bri.renderInventoryBlock(block, metadata, modelID, renderer);
    }
    
    public boolean renderItemAsFull3DBlock(final int modelId) {
        final ISimpleBlockRenderingHandler bri = this.blockRenderers.get(modelId);
        return bri != null && bri.shouldRender3DInInventory();
    }
    
    public void loadEntityRenderers(final Map<Class<? extends nn>, bgm> rendererMap) {
        for (final EntityRendererInfo info : this.entityRenderers) {
            rendererMap.put(info.target, info.renderer);
            info.renderer.a(bgl.a);
        }
    }
    
    static {
        INSTANCE = new RenderingRegistry();
    }
    
    private static class EntityRendererInfo
    {
        private Class<? extends nn> target;
        private bgm renderer;
        
        public EntityRendererInfo(final Class<? extends nn> target, final bgm renderer) {
            this.target = target;
            this.renderer = renderer;
        }
    }
}
