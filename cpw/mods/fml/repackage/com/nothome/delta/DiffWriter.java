// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.repackage.com.nothome.delta;

import java.io.IOException;
import java.io.Closeable;

public interface DiffWriter extends Closeable
{
    void addCopy(final long p0, final int p1) throws IOException;
    
    void addData(final byte p0) throws IOException;
    
    void flush() throws IOException;
    
    void close() throws IOException;
}
