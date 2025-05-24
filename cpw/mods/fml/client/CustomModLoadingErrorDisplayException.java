// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.IFMLHandledException;

@SideOnly(Side.CLIENT)
public abstract class CustomModLoadingErrorDisplayException extends RuntimeException implements IFMLHandledException
{
    public abstract void initGui(final avh p0, final avi p1);
    
    public abstract void drawScreen(final avh p0, final avi p1, final int p2, final int p3, final float p4);
}
