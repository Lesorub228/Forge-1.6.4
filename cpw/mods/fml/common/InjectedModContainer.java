// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.util.Map;
import java.security.cert.Certificate;
import cpw.mods.fml.common.versioning.VersionRange;
import com.google.common.eventbus.EventBus;
import java.util.List;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import java.util.Set;
import java.io.File;

public class InjectedModContainer implements ModContainer
{
    private File source;
    public final ModContainer wrappedContainer;
    
    public InjectedModContainer(final ModContainer mc, final File source) {
        this.source = ((source != null) ? source : new File("minecraft.jar"));
        this.wrappedContainer = mc;
    }
    
    @Override
    public String getModId() {
        return this.wrappedContainer.getModId();
    }
    
    @Override
    public String getName() {
        return this.wrappedContainer.getName();
    }
    
    @Override
    public String getVersion() {
        return this.wrappedContainer.getVersion();
    }
    
    @Override
    public File getSource() {
        return this.source;
    }
    
    @Override
    public ModMetadata getMetadata() {
        return this.wrappedContainer.getMetadata();
    }
    
    @Override
    public void bindMetadata(final MetadataCollection mc) {
        this.wrappedContainer.bindMetadata(mc);
    }
    
    @Override
    public void setEnabledState(final boolean enabled) {
        this.wrappedContainer.setEnabledState(enabled);
    }
    
    @Override
    public Set<ArtifactVersion> getRequirements() {
        return this.wrappedContainer.getRequirements();
    }
    
    @Override
    public List<ArtifactVersion> getDependencies() {
        return this.wrappedContainer.getDependencies();
    }
    
    @Override
    public List<ArtifactVersion> getDependants() {
        return this.wrappedContainer.getDependants();
    }
    
    @Override
    public String getSortingRules() {
        return this.wrappedContainer.getSortingRules();
    }
    
    @Override
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        return this.wrappedContainer.registerBus(bus, controller);
    }
    
    @Override
    public boolean matches(final Object mod) {
        return this.wrappedContainer.matches(mod);
    }
    
    @Override
    public Object getMod() {
        return this.wrappedContainer.getMod();
    }
    
    @Override
    public ArtifactVersion getProcessedVersion() {
        return this.wrappedContainer.getProcessedVersion();
    }
    
    @Override
    public boolean isNetworkMod() {
        return this.wrappedContainer.isNetworkMod();
    }
    
    @Override
    public boolean isImmutable() {
        return true;
    }
    
    @Override
    public String getDisplayVersion() {
        return this.wrappedContainer.getDisplayVersion();
    }
    
    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        return this.wrappedContainer.acceptableMinecraftVersionRange();
    }
    
    public WorldAccessContainer getWrappedWorldAccessContainer() {
        if (this.wrappedContainer instanceof WorldAccessContainer) {
            return (WorldAccessContainer)this.wrappedContainer;
        }
        return null;
    }
    
    @Override
    public Certificate getSigningCertificate() {
        return this.wrappedContainer.getSigningCertificate();
    }
    
    @Override
    public String toString() {
        return "Wrapped{" + this.wrappedContainer.toString() + "}";
    }
    
    @Override
    public Map<String, String> getCustomModProperties() {
        return this.wrappedContainer.getCustomModProperties();
    }
    
    @Override
    public Class<?> getCustomResourcePackClass() {
        return this.wrappedContainer.getCustomResourcePackClass();
    }
    
    @Override
    public Map<String, String> getSharedModDescriptor() {
        return this.wrappedContainer.getSharedModDescriptor();
    }
}
