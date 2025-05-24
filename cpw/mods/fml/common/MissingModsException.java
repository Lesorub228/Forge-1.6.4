// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import cpw.mods.fml.common.versioning.ArtifactVersion;
import java.util.Set;

public class MissingModsException extends RuntimeException
{
    public Set<ArtifactVersion> missingMods;
    
    public MissingModsException(final Set<ArtifactVersion> missingMods) {
        this.missingMods = missingMods;
    }
}
