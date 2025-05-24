// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery;

import cpw.mods.fml.common.ModContainer;
import java.util.List;
import java.util.regex.Pattern;

public interface ITypeDiscoverer
{
    public static final Pattern classFile = Pattern.compile("([^\\s$]+).class$");
    
    List<ModContainer> discover(final ModCandidate p0, final ASMDataTable p1);
}
