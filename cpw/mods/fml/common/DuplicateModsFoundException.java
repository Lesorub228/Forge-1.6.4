// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.io.File;
import com.google.common.collect.SetMultimap;

public class DuplicateModsFoundException extends LoaderException
{
    public SetMultimap<ModContainer, File> dupes;
    
    public DuplicateModsFoundException(final SetMultimap<ModContainer, File> dupes) {
        this.dupes = dupes;
    }
}
