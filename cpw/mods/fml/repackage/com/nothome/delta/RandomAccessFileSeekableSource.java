// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.repackage.com.nothome.delta;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileSeekableSource implements SeekableSource
{
    private RandomAccessFile raf;
    
    public RandomAccessFileSeekableSource(final RandomAccessFile raf) {
        if (raf == null) {
            throw new NullPointerException("raf");
        }
        this.raf = raf;
    }
    
    @Override
    public void seek(final long pos) throws IOException {
        this.raf.seek(pos);
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.raf.read(b, off, len);
    }
    
    public long length() throws IOException {
        return this.raf.length();
    }
    
    @Override
    public void close() throws IOException {
        this.raf.close();
    }
    
    @Override
    public int read(final ByteBuffer bb) throws IOException {
        final int c = this.raf.read(bb.array(), bb.position(), bb.remaining());
        if (c == -1) {
            return -1;
        }
        bb.position(bb.position() + c);
        return c;
    }
}
