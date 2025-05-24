// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.event;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.util.Set;

public class FMLFingerprintViolationEvent extends FMLEvent
{
    public final boolean isDirectory;
    public final Set<String> fingerprints;
    public final File source;
    public final String expectedFingerprint;
    
    public FMLFingerprintViolationEvent(final boolean isDirectory, final File source, final ImmutableSet<String> fingerprints, final String expectedFingerprint) {
        this.isDirectory = isDirectory;
        this.source = source;
        this.fingerprints = (Set<String>)fingerprints;
        this.expectedFingerprint = expectedFingerprint;
    }
}
