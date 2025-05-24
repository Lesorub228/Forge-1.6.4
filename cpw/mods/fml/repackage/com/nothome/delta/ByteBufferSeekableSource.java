// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.repackage.com.nothome.delta;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferSeekableSource implements SeekableSource
{
    private ByteBuffer bb;
    private ByteBuffer cur;
    
    public ByteBufferSeekableSource(final byte[] source) {
        this(ByteBuffer.wrap(source));
    }
    
    public ByteBufferSeekableSource(final ByteBuffer bb) {
        if (bb == null) {
            throw new NullPointerException("bb");
        }
        (this.bb = bb).rewind();
        try {
            this.seek(0L);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void seek(final long pos) throws IOException {
        this.cur = this.bb.slice();
        if (pos > this.cur.limit()) {
            throw new IOException("pos " + pos + " cannot seek " + this.cur.limit());
        }
        this.cur.position((int)pos);
    }
    
    @Override
    public int read(final ByteBuffer dest) throws IOException {
        if (!this.cur.hasRemaining()) {
            return -1;
        }
        int c = 0;
        while (this.cur.hasRemaining() && dest.hasRemaining()) {
            dest.put(this.cur.get());
            ++c;
        }
        return c;
    }
    
    @Override
    public void close() throws IOException {
        this.bb = null;
        this.cur = null;
    }
    
    @Override
    public String toString() {
        return "BBSeekable bb=" + this.bb.position() + "-" + this.bb.limit() + " cur=" + this.cur.position() + "-" + this.cur.limit() + "";
    }
}
