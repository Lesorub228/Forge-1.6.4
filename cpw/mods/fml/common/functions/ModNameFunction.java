// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.functions;

import cpw.mods.fml.common.ModContainer;
import com.google.common.base.Function;

public class ModNameFunction implements Function<ModContainer, String>
{
    public String apply(final ModContainer input) {
        return input.getName();
    }
}
