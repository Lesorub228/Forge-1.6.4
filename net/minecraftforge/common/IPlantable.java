// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

public interface IPlantable
{
    EnumPlantType getPlantType(final abw p0, final int p1, final int p2, final int p3);
    
    int getPlantID(final abw p0, final int p1, final int p2, final int p3);
    
    int getPlantMetadata(final abw p0, final int p1, final int p2, final int p3);
}
