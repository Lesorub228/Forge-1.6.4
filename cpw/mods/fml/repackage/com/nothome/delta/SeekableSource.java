// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.repackage.com.nothome.delta;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.Closeable;

public interface SeekableSource extends Closeable
{
    void seek(final long p0) throws IOException;
    
    int read(final ByteBuffer p0) throws IOException;
}
