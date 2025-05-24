// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.model;

public interface IModelCustom
{
    String getType();
    
    void renderAll();
    
    void renderOnly(final String... p0);
    
    void renderPart(final String p0);
    
    void renderAllExcept(final String... p0);
}
