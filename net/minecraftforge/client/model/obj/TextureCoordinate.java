// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.model.obj;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureCoordinate
{
    public float u;
    public float v;
    public float w;
    
    public TextureCoordinate(final float u, final float v) {
        this(u, v, 0.0f);
    }
    
    public TextureCoordinate(final float u, final float v, final float w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }
}
