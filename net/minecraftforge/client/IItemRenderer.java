// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client;

public interface IItemRenderer
{
    boolean handleRenderType(final ye p0, final ItemRenderType p1);
    
    boolean shouldUseRenderHelper(final ItemRenderType p0, final ye p1, final ItemRendererHelper p2);
    
    void renderItem(final ItemRenderType p0, final ye p1, final Object... p2);
    
    public enum ItemRenderType
    {
        ENTITY, 
        EQUIPPED, 
        EQUIPPED_FIRST_PERSON, 
        INVENTORY, 
        FIRST_PERSON_MAP;
    }
    
    public enum ItemRendererHelper
    {
        ENTITY_ROTATION, 
        ENTITY_BOBBING, 
        EQUIPPED_BLOCK, 
        BLOCK_3D, 
        INVENTORY_BLOCK;
    }
}
