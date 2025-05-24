// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.repackage.com.nothome.delta;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class DebugDiffWriter implements DiffWriter
{
    private ByteArrayOutputStream os;
    
    public DebugDiffWriter() {
        this.os = new ByteArrayOutputStream();
    }
    
    @Override
    public void addCopy(final long offset, final int length) throws IOException {
        if (this.os.size() > 0) {
            this.writeBuf();
        }
        System.err.println("COPY off: " + offset + ", len: " + length);
    }
    
    @Override
    public void addData(final byte b) throws IOException {
        this.os.write(b);
        this.writeBuf();
    }
    
    private void writeBuf() {
        System.err.print("DATA: ");
        final byte[] ba = this.os.toByteArray();
        for (int ix = 0; ix < ba.length; ++ix) {
            if (ba[ix] == 10) {
                System.err.print("\\n");
            }
            else {
                System.err.print(String.valueOf((char)ba[ix]));
            }
        }
        System.err.println("");
        this.os.reset();
    }
    
    @Override
    public void flush() throws IOException {
        System.err.println("FLUSH");
    }
    
    @Override
    public void close() throws IOException {
        System.err.println("CLOSE");
    }
}
