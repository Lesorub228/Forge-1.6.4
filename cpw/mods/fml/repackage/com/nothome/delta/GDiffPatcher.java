// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.repackage.com.nothome.delta;

import java.io.EOFException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.io.File;
import java.nio.ByteBuffer;

public class GDiffPatcher
{
    private ByteBuffer buf;
    private byte[] buf2;
    
    public GDiffPatcher() {
        this.buf = ByteBuffer.allocate(1024);
        this.buf2 = this.buf.array();
    }
    
    public void patch(final File sourceFile, final File patchFile, final File outputFile) throws IOException {
        final RandomAccessFileSeekableSource source = new RandomAccessFileSeekableSource(new RandomAccessFile(sourceFile, "r"));
        final InputStream patch = new FileInputStream(patchFile);
        final OutputStream output = new FileOutputStream(outputFile);
        try {
            this.patch(source, patch, output);
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            source.close();
            patch.close();
            output.close();
        }
    }
    
    public void patch(final byte[] source, final InputStream patch, final OutputStream output) throws IOException {
        this.patch(new ByteBufferSeekableSource(source), patch, output);
    }
    
    public byte[] patch(final byte[] source, final byte[] patch) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.patch(source, new ByteArrayInputStream(patch), os);
        return os.toByteArray();
    }
    
    public void patch(final SeekableSource source, final InputStream patch, final OutputStream out) throws IOException {
        final DataOutputStream outOS = new DataOutputStream(out);
        final DataInputStream patchIS = new DataInputStream(patch);
        if (patchIS.readUnsignedByte() != 209 || patchIS.readUnsignedByte() != 255 || patchIS.readUnsignedByte() != 209 || patchIS.readUnsignedByte() != 255 || patchIS.readUnsignedByte() != 4) {
            throw new PatchException("magic string not found, aborting!");
        }
        while (true) {
            final int command = patchIS.readUnsignedByte();
            if (command == 0) {
                outOS.flush();
                return;
            }
            if (command <= 246) {
                this.append(command, patchIS, outOS);
            }
            else {
                switch (command) {
                    case 247: {
                        final int length = patchIS.readUnsignedShort();
                        this.append(length, patchIS, outOS);
                        continue;
                    }
                    case 248: {
                        final int length = patchIS.readInt();
                        this.append(length, patchIS, outOS);
                        continue;
                    }
                    case 249: {
                        final int offset = patchIS.readUnsignedShort();
                        final int length = patchIS.readUnsignedByte();
                        this.copy(offset, length, source, outOS);
                        continue;
                    }
                    case 250: {
                        final int offset = patchIS.readUnsignedShort();
                        final int length = patchIS.readUnsignedShort();
                        this.copy(offset, length, source, outOS);
                        continue;
                    }
                    case 251: {
                        final int offset = patchIS.readUnsignedShort();
                        final int length = patchIS.readInt();
                        this.copy(offset, length, source, outOS);
                        continue;
                    }
                    case 252: {
                        final int offset = patchIS.readInt();
                        final int length = patchIS.readUnsignedByte();
                        this.copy(offset, length, source, outOS);
                        continue;
                    }
                    case 253: {
                        final int offset = patchIS.readInt();
                        final int length = patchIS.readUnsignedShort();
                        this.copy(offset, length, source, outOS);
                        continue;
                    }
                    case 254: {
                        final int offset = patchIS.readInt();
                        final int length = patchIS.readInt();
                        this.copy(offset, length, source, outOS);
                        continue;
                    }
                    case 255: {
                        final long loffset = patchIS.readLong();
                        final int length = patchIS.readInt();
                        this.copy(loffset, length, source, outOS);
                        continue;
                    }
                    default: {
                        throw new IllegalStateException("command " + command);
                    }
                }
            }
        }
    }
    
    private void copy(final long offset, int length, final SeekableSource source, final OutputStream output) throws IOException {
        source.seek(offset);
        while (length > 0) {
            final int len = Math.min(this.buf.capacity(), length);
            this.buf.clear().limit(len);
            final int res = source.read(this.buf);
            if (res == -1) {
                throw new EOFException("in copy " + offset + " " + length);
            }
            output.write(this.buf.array(), 0, res);
            length -= res;
        }
    }
    
    private void append(int length, final InputStream patch, final OutputStream output) throws IOException {
        while (length > 0) {
            final int len = Math.min(this.buf2.length, length);
            final int res = patch.read(this.buf2, 0, len);
            if (res == -1) {
                throw new EOFException("cannot read " + length);
            }
            output.write(this.buf2, 0, res);
            length -= res;
        }
    }
    
    public static void main(final String[] argv) {
        if (argv.length != 3) {
            System.err.println("usage GDiffPatch source patch output");
            System.err.println("aborting..");
            return;
        }
        try {
            final File sourceFile = new File(argv[0]);
            final File patchFile = new File(argv[1]);
            final File outputFile = new File(argv[2]);
            if (sourceFile.length() > 2147483647L || patchFile.length() > 2147483647L) {
                System.err.println("source or patch is too large, max length is 2147483647");
                System.err.println("aborting..");
                return;
            }
            final GDiffPatcher patcher = new GDiffPatcher();
            patcher.patch(sourceFile, patchFile, outputFile);
            System.out.println("finished patching file");
        }
        catch (final Exception ioe) {
            System.err.println("error while patching: " + ioe);
        }
    }
}
