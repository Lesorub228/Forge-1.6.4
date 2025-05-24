// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.versioning;

public class DefaultArtifactVersion implements ArtifactVersion
{
    private ComparableVersion comparableVersion;
    private String label;
    private boolean unbounded;
    private VersionRange range;
    
    public DefaultArtifactVersion(final String versionNumber) {
        this.comparableVersion = new ComparableVersion(versionNumber);
        this.range = VersionRange.createFromVersion(versionNumber, this);
    }
    
    public DefaultArtifactVersion(final String label, final VersionRange range) {
        this.label = label;
        this.range = range;
    }
    
    public DefaultArtifactVersion(final String label, final String version) {
        this(version);
        this.label = label;
    }
    
    public DefaultArtifactVersion(final String string, final boolean unbounded) {
        this.label = string;
        this.unbounded = true;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return ((DefaultArtifactVersion)obj).containsVersion(this);
    }
    
    @Override
    public int compareTo(final ArtifactVersion o) {
        return this.unbounded ? 0 : this.comparableVersion.compareTo(((DefaultArtifactVersion)o).comparableVersion);
    }
    
    @Override
    public String getLabel() {
        return this.label;
    }
    
    @Override
    public boolean containsVersion(final ArtifactVersion source) {
        return source.getLabel().equals(this.getLabel()) && (this.unbounded || (this.range != null && this.range.containsVersion(source)));
    }
    
    @Override
    public String getVersionString() {
        return (this.comparableVersion == null) ? "unknown" : this.comparableVersion.toString();
    }
    
    @Override
    public String getRangeString() {
        return (this.range == null) ? "any" : this.range.toString();
    }
    
    @Override
    public String toString() {
        return (this.label == null) ? this.comparableVersion.toString() : (this.label + (this.unbounded ? "" : ("@" + this.range)));
    }
    
    public VersionRange getRange() {
        return this.range;
    }
}
