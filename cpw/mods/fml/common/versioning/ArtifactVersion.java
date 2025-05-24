// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.versioning;

public interface ArtifactVersion extends Comparable<ArtifactVersion>
{
    String getLabel();
    
    String getVersionString();
    
    boolean containsVersion(final ArtifactVersion p0);
    
    String getRangeString();
}
