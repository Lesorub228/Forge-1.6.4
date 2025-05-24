// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.functions;

import cpw.mods.fml.common.versioning.ArtifactVersion;
import com.google.common.base.Function;

public class ArtifactVersionNameFunction implements Function<ArtifactVersion, String>
{
    public String apply(final ArtifactVersion v) {
        return v.getLabel();
    }
}
