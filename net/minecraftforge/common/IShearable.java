// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.ArrayList;

public interface IShearable
{
    boolean isShearable(final ye p0, final abw p1, final int p2, final int p3, final int p4);
    
    ArrayList<ye> onSheared(final ye p0, final abw p1, final int p2, final int p3, final int p4, final int p5);
}
