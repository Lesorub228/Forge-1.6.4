// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class IRenderHandler
{
    @SideOnly(Side.CLIENT)
    public abstract void render(final float p0, final bdd p1, final atv p2);
}
