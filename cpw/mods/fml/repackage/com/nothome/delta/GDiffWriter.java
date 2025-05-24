// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.repackage.com.nothome.delta;

import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;

public class GDiffWriter implements DiffWriter
{
    public static final int CHUNK_SIZE = 32767;
    public static final byte EOF = 0;
    public static final int DATA_MAX = 246;
    public static final int DATA_USHORT = 247;
    public static final int DATA_INT = 248;
    public static final int COPY_USHORT_UBYTE = 249;
    public static final int COPY_USHORT_USHORT = 250;
    public static final int COPY_USHORT_INT = 251;
    public static final int COPY_INT_UBYTE = 252;
    public static final int COPY_INT_USHORT = 253;
    public static final int COPY_INT_INT = 254;
    public static final int COPY_LONG_INT = 255;
    private ByteArrayOutputStream buf;
    private boolean debug;
    private DataOutputStream output;
    
    public GDiffWriter(final DataOutputStream os) throws IOException {
        this.buf = new ByteArrayOutputStream();
        this.debug = false;
        this.output = null;
        (this.output = os).writeByte(209);
        this.output.writeByte(255);
        this.output.writeByte(209);
        this.output.writeByte(255);
        this.output.writeByte(4);
    }
    
    public GDiffWriter(final OutputStream output) throws IOException {
        this(new DataOutputStream(output));
    }
    
    @Override
    public void addCopy(final long offset, final int length) throws IOException {
        this.writeBuf();
        if (this.debug) {
            System.err.println("COPY off: " + offset + ", len: " + length);
        }
        if (offset > 2147483647L) {
            this.output.writeByte(255);
            this.output.writeLong(offset);
            this.output.writeInt(length);
        }
        else if (offset < 65536L) {
            if (length < 256) {
                this.output.writeByte(249);
                this.output.writeShort((int)offset);
                this.output.writeByte(length);
            }
            else if (length > 65535) {
                this.output.writeByte(251);
                this.output.writeShort((int)offset);
                this.output.writeInt(length);
            }
            else {
                this.output.writeByte(250);
                this.output.writeShort((int)offset);
                this.output.writeShort(length);
            }
        }
        else if (length < 256) {
            this.output.writeByte(252);
            this.output.writeInt((int)offset);
            this.output.writeByte(length);
        }
        else if (length > 65535) {
            this.output.writeByte(254);
            this.output.writeInt((int)offset);
            this.output.writeInt(length);
        }
        else {
            this.output.writeByte(253);
            this.output.writeInt((int)offset);
            this.output.writeShort(length);
        }
    }
    
    @Override
    public void addData(final byte b) throws IOException {
        this.buf.write(b);
        if (this.buf.size() >= 32767) {
            this.writeBuf();
        }
    }
    
    private void writeBuf() throws IOException {
        if (this.buf.size() > 0) {
            if (this.buf.size() <= 246) {
                this.output.writeByte(this.buf.size());
            }
            else if (this.buf.size() <= 65535) {
                this.output.writeByte(247);
                this.output.writeShort(this.buf.size());
            }
            else {
                this.output.writeByte(248);
                this.output.writeInt(this.buf.size());
            }
            this.buf.writeTo(this.output);
            this.buf.reset();
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.writeBuf();
        this.output.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        this.output.write(0);
        this.output.close();
    }
}
