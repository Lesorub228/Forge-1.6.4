// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.liquids;

@Deprecated
public interface IBlockLiquid extends ILiquid
{
    boolean willGenerateSources();
    
    int getFlowDistance();
    
    byte[] getLiquidRGB();
    
    String getLiquidBlockTextureFile();
    
    by getLiquidProperties();
    
    public enum BlockType
    {
        NONE, 
        VANILLA, 
        FINITE;
    }
}
