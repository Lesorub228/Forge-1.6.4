// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.repackage.com.nothome.delta;

import java.nio.channels.Channels;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;

public class Delta
{
    static final boolean debug = false;
    public static final int DEFAULT_CHUNK_SIZE = 16;
    private int S;
    private SourceState source;
    private TargetState target;
    private DiffWriter output;
    
    public Delta() {
        this.setChunkSize(16);
    }
    
    public void setChunkSize(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Invalid size");
        }
        this.S = size;
    }
    
    public void compute(final byte[] source, final byte[] target, final OutputStream output) throws IOException {
        this.compute(new ByteBufferSeekableSource(source), new ByteArrayInputStream(target), new GDiffWriter(output));
    }
    
    public byte[] compute(final byte[] source, final byte[] target) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.compute(source, target, os);
        return os.toByteArray();
    }
    
    public void compute(final byte[] sourceBytes, final InputStream inputStream, final DiffWriter diffWriter) throws IOException {
        this.compute(new ByteBufferSeekableSource(sourceBytes), inputStream, diffWriter);
    }
    
    public void compute(final File sourceFile, final File targetFile, final DiffWriter output) throws IOException {
        final RandomAccessFileSeekableSource source = new RandomAccessFileSeekableSource(new RandomAccessFile(sourceFile, "r"));
        final InputStream is = new BufferedInputStream(new FileInputStream(targetFile));
        try {
            this.compute(source, is, output);
        }
        finally {
            source.close();
            is.close();
        }
    }
    
    public void compute(final SeekableSource seekSource, final InputStream targetIS, final DiffWriter output) throws IOException {
        this.source = new SourceState(seekSource);
        this.target = new TargetState(targetIS);
        this.output = output;
        while (!this.target.eof()) {
            this.debug("!target.eof()");
            final int index = this.target.find(this.source);
            if (index != -1) {
                final long offset = index * (long)this.S;
                this.source.seek(offset);
                final int match = this.target.longestMatch(this.source);
                if (match >= this.S) {
                    output.addCopy(offset, match);
                }
                else {
                    this.target.tbuf.position(this.target.tbuf.position() - match);
                    this.addData();
                }
            }
            else {
                this.addData();
            }
        }
        output.close();
    }
    
    private void addData() throws IOException {
        final int i = this.target.read();
        if (i == -1) {
            return;
        }
        this.output.addData((byte)i);
    }
    
    public static void main(final String[] argv) throws Exception {
        if (argv.length != 3) {
            System.err.println("usage Delta [-d] source target [output]");
            System.err.println("either -d or an output filename must be specified.");
            System.err.println("aborting..");
            return;
        }
        DiffWriter output = null;
        File sourceFile = null;
        File targetFile = null;
        if (argv[0].equals("-d")) {
            sourceFile = new File(argv[1]);
            targetFile = new File(argv[2]);
            output = new DebugDiffWriter();
        }
        else {
            sourceFile = new File(argv[0]);
            targetFile = new File(argv[1]);
            output = new GDiffWriter(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(argv[2])))));
        }
        if (sourceFile.length() > 2147483647L || targetFile.length() > 2147483647L) {
            System.err.println("source or target is too large, max length is 2147483647");
            System.err.println("aborting..");
            output.close();
            return;
        }
        final Delta d = new Delta();
        d.compute(sourceFile, targetFile, output);
        output.flush();
        output.close();
    }
    
    private void debug(final String s) {
    }
    
    class SourceState
    {
        private Checksum checksum;
        private SeekableSource source;
        
        public SourceState(final SeekableSource source) throws IOException {
            this.checksum = new Checksum(source, Delta.this.S);
            (this.source = source).seek(0L);
        }
        
        public void seek(final long index) throws IOException {
            this.source.seek(index);
        }
        
        @Override
        public String toString() {
            return "Source checksum=" + this.checksum + " source=" + this.source + "";
        }
    }
    
    class TargetState
    {
        private ReadableByteChannel c;
        private ByteBuffer tbuf;
        private ByteBuffer sbuf;
        private long hash;
        private boolean hashReset;
        private boolean eof;
        
        TargetState(final InputStream targetIS) throws IOException {
            this.tbuf = ByteBuffer.allocate(this.blocksize());
            this.sbuf = ByteBuffer.allocate(this.blocksize());
            this.hashReset = true;
            this.c = Channels.newChannel(targetIS);
            this.tbuf.limit(0);
        }
        
        private int blocksize() {
            return Math.min(16384, Delta.this.S * 4);
        }
        
        public int find(final SourceState source) throws IOException {
            if (this.eof) {
                return -1;
            }
            this.sbuf.clear();
            this.sbuf.limit(0);
            if (this.hashReset) {
                Delta.this.debug("hashReset");
                while (this.tbuf.remaining() < Delta.this.S) {
                    this.tbuf.compact();
                    final int read = this.c.read(this.tbuf);
                    this.tbuf.flip();
                    if (read == -1) {
                        Delta.this.debug("target ending");
                        return -1;
                    }
                }
                this.hash = Checksum.queryChecksum(this.tbuf, Delta.this.S);
                this.hashReset = false;
            }
            return source.checksum.findChecksumIndex(this.hash);
        }
        
        public boolean eof() {
            return this.eof;
        }
        
        public int read() throws IOException {
            if (this.tbuf.remaining() <= Delta.this.S) {
                this.readMore();
                if (!this.tbuf.hasRemaining()) {
                    this.eof = true;
                    return -1;
                }
            }
            final byte b = this.tbuf.get();
            if (this.tbuf.remaining() >= Delta.this.S) {
                final byte nchar = this.tbuf.get(this.tbuf.position() + Delta.this.S - 1);
                this.hash = Checksum.incrementChecksum(this.hash, b, nchar, Delta.this.S);
            }
            else {
                Delta.this.debug("out of char");
            }
            return b & 0xFF;
        }
        
        public int longestMatch(final SourceState source) throws IOException {
            Delta.this.debug("longestMatch");
            int match = 0;
            this.hashReset = true;
            while (true) {
                if (!this.sbuf.hasRemaining()) {
                    this.sbuf.clear();
                    final int read = source.source.read(this.sbuf);
                    this.sbuf.flip();
                    if (read == -1) {
                        return match;
                    }
                }
                if (!this.tbuf.hasRemaining()) {
                    this.readMore();
                    if (!this.tbuf.hasRemaining()) {
                        Delta.this.debug("target ending");
                        this.eof = true;
                        return match;
                    }
                }
                if (this.sbuf.get() != this.tbuf.get()) {
                    this.tbuf.position(this.tbuf.position() - 1);
                    return match;
                }
                ++match;
            }
        }
        
        private void readMore() throws IOException {
            this.tbuf.compact();
            this.c.read(this.tbuf);
            this.tbuf.flip();
        }
        
        void hash() {
            this.hash = Checksum.queryChecksum(this.tbuf, Delta.this.S);
        }
        
        @Override
        public String toString() {
            return "Target[ targetBuff=" + this.dump() + " sourceBuff=" + this.sbuf + " hashf=" + this.hash + " eof=" + this.eof + "]";
        }
        
        private String dump() {
            return this.dump(this.tbuf);
        }
        
        private String dump(final ByteBuffer bb) {
            return this.getTextDump(bb);
        }
        
        private void append(final StringBuffer sb, final int value) {
            final char b1 = (char)(value >> 4 & 0xF);
            final char b2 = (char)(value & 0xF);
            sb.append(Character.forDigit(b1, 16));
            sb.append(Character.forDigit(b2, 16));
        }
        
        public String getTextDump(final ByteBuffer bb) {
            final StringBuffer sb = new StringBuffer(bb.remaining() * 2);
            bb.mark();
            while (bb.hasRemaining()) {
                final int val = bb.get();
                if (val > 32 && val < 127) {
                    sb.append(" ").append((char)val);
                }
                else {
                    this.append(sb, val);
                }
            }
            bb.reset();
            return sb.toString();
        }
    }
}
