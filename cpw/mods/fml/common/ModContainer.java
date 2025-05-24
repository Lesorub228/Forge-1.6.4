// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import com.google.common.collect.ImmutableMap;
import java.security.cert.Certificate;
import cpw.mods.fml.common.versioning.VersionRange;
import com.google.common.eventbus.EventBus;
import java.util.List;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import java.util.Set;
import java.io.File;
import java.util.Map;

public interface ModContainer
{
    public static final Map<String, String> EMPTY_PROPERTIES = ImmutableMap.of();
    
    String getModId();
    
    String getName();
    
    String getVersion();
    
    File getSource();
    
    ModMetadata getMetadata();
    
    void bindMetadata(final MetadataCollection p0);
    
    void setEnabledState(final boolean p0);
    
    Set<ArtifactVersion> getRequirements();
    
    List<ArtifactVersion> getDependencies();
    
    List<ArtifactVersion> getDependants();
    
    String getSortingRules();
    
    boolean registerBus(final EventBus p0, final LoadController p1);
    
    boolean matches(final Object p0);
    
    Object getMod();
    
    ArtifactVersion getProcessedVersion();
    
    boolean isImmutable();
    
    boolean isNetworkMod();
    
    String getDisplayVersion();
    
    VersionRange acceptableMinecraftVersionRange();
    
    Certificate getSigningCertificate();
    
    Map<String, String> getCustomModProperties();
    
    Class<?> getCustomResourcePackClass();
    
    Map<String, String> getSharedModDescriptor();
}
