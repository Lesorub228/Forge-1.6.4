// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.util.Map;
import java.security.cert.Certificate;
import cpw.mods.fml.common.versioning.VersionRange;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import com.google.common.eventbus.EventBus;
import java.io.File;
import java.util.Set;
import java.util.Collections;
import java.util.List;
import cpw.mods.fml.common.versioning.ArtifactVersion;

public class DummyModContainer implements ModContainer
{
    private ModMetadata md;
    private ArtifactVersion processedVersion;
    private String label;
    
    public DummyModContainer(final ModMetadata md) {
        this.md = md;
    }
    
    public DummyModContainer(final String label) {
        this.label = label;
    }
    
    public DummyModContainer() {
    }
    
    @Override
    public void bindMetadata(final MetadataCollection mc) {
    }
    
    @Override
    public List<ArtifactVersion> getDependants() {
        return Collections.emptyList();
    }
    
    @Override
    public List<ArtifactVersion> getDependencies() {
        return Collections.emptyList();
    }
    
    @Override
    public Set<ArtifactVersion> getRequirements() {
        return Collections.emptySet();
    }
    
    @Override
    public ModMetadata getMetadata() {
        return this.md;
    }
    
    @Override
    public Object getMod() {
        return null;
    }
    
    @Override
    public String getModId() {
        return this.md.modId;
    }
    
    @Override
    public String getName() {
        return this.md.name;
    }
    
    @Override
    public String getSortingRules() {
        return "";
    }
    
    @Override
    public File getSource() {
        return null;
    }
    
    @Override
    public String getVersion() {
        return this.md.version;
    }
    
    @Override
    public boolean matches(final Object mod) {
        return false;
    }
    
    @Override
    public void setEnabledState(final boolean enabled) {
    }
    
    @Override
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        return false;
    }
    
    @Override
    public ArtifactVersion getProcessedVersion() {
        if (this.processedVersion == null) {
            this.processedVersion = new DefaultArtifactVersion(this.getModId(), this.getVersion());
        }
        return this.processedVersion;
    }
    
    @Override
    public boolean isImmutable() {
        return false;
    }
    
    @Override
    public boolean isNetworkMod() {
        return false;
    }
    
    @Override
    public String getDisplayVersion() {
        return this.md.version;
    }
    
    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        return Loader.instance().getMinecraftModContainer().getStaticVersionRange();
    }
    
    @Override
    public Certificate getSigningCertificate() {
        return null;
    }
    
    @Override
    public String toString() {
        return (this.md != null) ? this.getModId() : ("Dummy Container (" + this.label + ") @" + System.identityHashCode(this));
    }
    
    @Override
    public Map<String, String> getCustomModProperties() {
        return DummyModContainer.EMPTY_PROPERTIES;
    }
    
    @Override
    public Class<?> getCustomResourcePackClass() {
        return null;
    }
    
    @Override
    public Map<String, String> getSharedModDescriptor() {
        return null;
    }
}
