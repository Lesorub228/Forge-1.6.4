// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

public interface IGuiHandler
{
    Object getServerGuiElement(final int p0, final uf p1, final abw p2, final int p3, final int p4, final int p5);
    
    Object getClientGuiElement(final int p0, final uf p1, final abw p2, final int p3, final int p4, final int p5);
}
