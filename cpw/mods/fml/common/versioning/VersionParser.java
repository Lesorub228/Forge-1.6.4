// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.versioning;

import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.base.Strings;
import com.google.common.base.Splitter;

public class VersionParser
{
    private static final Splitter SEPARATOR;
    
    public static ArtifactVersion parseVersionReference(final String labelledRef) {
        if (Strings.isNullOrEmpty(labelledRef)) {
            throw new RuntimeException(String.format("Empty reference %s", labelledRef));
        }
        final List<String> parts = Lists.newArrayList(VersionParser.SEPARATOR.split((CharSequence)labelledRef));
        if (parts.size() > 2) {
            throw new RuntimeException(String.format("Invalid versioned reference %s", labelledRef));
        }
        if (parts.size() == 1) {
            return new DefaultArtifactVersion(parts.get(0), true);
        }
        return new DefaultArtifactVersion(parts.get(0), parseRange(parts.get(1)));
    }
    
    public static boolean satisfies(final ArtifactVersion target, final ArtifactVersion source) {
        return target.containsVersion(source);
    }
    
    public static VersionRange parseRange(final String range) {
        try {
            return VersionRange.createFromVersionSpec(range);
        }
        catch (final InvalidVersionSpecificationException e) {
            FMLLog.log(Level.SEVERE, e, "Unable to parse a version range specification successfully %s", range);
            throw new LoaderException(e);
        }
    }
    
    static {
        SEPARATOR = Splitter.on('@').omitEmptyStrings().trimResults();
    }
}
