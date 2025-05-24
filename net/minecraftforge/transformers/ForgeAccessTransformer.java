// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.transformers;

import java.io.IOException;
import cpw.mods.fml.common.asm.transformers.AccessTransformer;

public class ForgeAccessTransformer extends AccessTransformer
{
    public ForgeAccessTransformer() throws IOException {
        super("forge_at.cfg");
    }
}
