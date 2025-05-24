// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound.codecs;

import java.nio.ShortBuffer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import ibxm.ProTracker;
import ibxm.ScreamTracker3;
import java.io.DataInput;
import ibxm.FastTracker2;
import java.io.DataInputStream;
import paulscode.sound.SoundBuffer;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;
import ibxm.Module;
import ibxm.IBXM;
import javax.sound.sampled.AudioFormat;
import paulscode.sound.ICodec;

public class CodecIBXM implements ICodec
{
    private static final boolean GET = false;
    private static final boolean SET = true;
    private static final boolean XXX = false;
    private boolean endOfStream;
    private boolean initialized;
    private AudioFormat myAudioFormat;
    private boolean reverseBytes;
    private IBXM ibxm;
    private Module module;
    private int songDuration;
    private int playPosition;
    private SoundSystemLogger logger;
    
    public CodecIBXM() {
        this.endOfStream = false;
        this.initialized = false;
        this.myAudioFormat = null;
        this.reverseBytes = false;
        this.logger = SoundSystemConfig.getLogger();
    }
    
    public void reverseByteOrder(final boolean b) {
        this.reverseBytes = b;
    }
    
    public boolean initialize(final URL url) {
        this.initialized(true, false);
        this.cleanup();
        if (url == null) {
            this.errorMessage("url null in method 'initialize'");
            this.cleanup();
            return false;
        }
        InputStream is = null;
        try {
            is = url.openStream();
        }
        catch (final IOException ioe) {
            this.errorMessage("Unable to open stream in method 'initialize'");
            this.printStackTrace(ioe);
            return false;
        }
        if (this.ibxm == null) {
            this.ibxm = new IBXM(48000);
        }
        if (this.myAudioFormat == null) {
            this.myAudioFormat = new AudioFormat(48000.0f, 16, 2, true, true);
        }
        try {
            this.setModule(loadModule(is));
        }
        catch (final IllegalArgumentException iae) {
            this.errorMessage("Illegal argument in method 'initialize'");
            this.printStackTrace(iae);
            if (is != null) {
                try {
                    is.close();
                }
                catch (final IOException ex) {}
            }
            return false;
        }
        catch (final IOException ioe) {
            this.errorMessage("Error loading module in method 'initialize'");
            this.printStackTrace(ioe);
            if (is != null) {
                try {
                    is.close();
                }
                catch (final IOException ex2) {}
            }
            return false;
        }
        if (is != null) {
            try {
                is.close();
            }
            catch (final IOException ex3) {}
        }
        this.endOfStream(true, false);
        this.initialized(true, true);
        return true;
    }
    
    public boolean initialized() {
        return this.initialized(false, false);
    }
    
    public SoundBuffer read() {
        if (this.endOfStream(false, false)) {
            return null;
        }
        if (this.module == null) {
            this.errorMessage("Module null in method 'read'");
            return null;
        }
        if (this.myAudioFormat == null) {
            this.errorMessage("Audio Format null in method 'read'");
            return null;
        }
        final int bufferFrameSize = SoundSystemConfig.getStreamingBufferSize() / 4;
        int frames = this.songDuration - this.playPosition;
        if (frames > bufferFrameSize) {
            frames = bufferFrameSize;
        }
        if (frames <= 0) {
            this.endOfStream(true, true);
            return null;
        }
        final byte[] outputBuffer = new byte[frames * 4];
        this.ibxm.get_audio(outputBuffer, frames);
        this.playPosition += frames;
        if (this.playPosition >= this.songDuration) {
            this.endOfStream(true, true);
        }
        if (this.reverseBytes) {
            reverseBytes(outputBuffer, 0, frames * 4);
        }
        final SoundBuffer buffer = new SoundBuffer(outputBuffer, this.myAudioFormat);
        return buffer;
    }
    
    public SoundBuffer readAll() {
        if (this.module == null) {
            this.errorMessage("Module null in method 'readAll'");
            return null;
        }
        if (this.myAudioFormat == null) {
            this.errorMessage("Audio Format null in method 'readAll'");
            return null;
        }
        final int bufferFrameSize = SoundSystemConfig.getFileChunkSize() / 4;
        final byte[] outputBuffer = new byte[bufferFrameSize * 4];
        byte[] fullBuffer = null;
        int totalBytes = 0;
        while (!this.endOfStream(false, false) && totalBytes < SoundSystemConfig.getMaxFileSize()) {
            int frames = this.songDuration - this.playPosition;
            if (frames > bufferFrameSize) {
                frames = bufferFrameSize;
            }
            this.ibxm.get_audio(outputBuffer, frames);
            totalBytes += frames * 4;
            fullBuffer = appendByteArrays(fullBuffer, outputBuffer, frames * 4);
            this.playPosition += frames;
            if (this.playPosition >= this.songDuration) {
                this.endOfStream(true, true);
            }
        }
        if (this.reverseBytes) {
            reverseBytes(fullBuffer, 0, totalBytes);
        }
        final SoundBuffer buffer = new SoundBuffer(fullBuffer, this.myAudioFormat);
        return buffer;
    }
    
    public boolean endOfStream() {
        return this.endOfStream(false, false);
    }
    
    public void cleanup() {
        this.playPosition = 0;
    }
    
    public AudioFormat getAudioFormat() {
        return this.myAudioFormat;
    }
    
    private static Module loadModule(final InputStream input) throws IllegalArgumentException, IOException {
        final DataInputStream data_input_stream = new DataInputStream(input);
        final byte[] xm_header = new byte[60];
        data_input_stream.readFully(xm_header);
        if (FastTracker2.is_xm(xm_header)) {
            return FastTracker2.load_xm(xm_header, data_input_stream);
        }
        final byte[] s3m_header = new byte[96];
        System.arraycopy(xm_header, 0, s3m_header, 0, 60);
        data_input_stream.readFully(s3m_header, 60, 36);
        if (ScreamTracker3.is_s3m(s3m_header)) {
            return ScreamTracker3.load_s3m(s3m_header, data_input_stream);
        }
        final byte[] mod_header = new byte[1084];
        System.arraycopy(s3m_header, 0, mod_header, 0, 96);
        data_input_stream.readFully(mod_header, 96, 988);
        return ProTracker.load_mod(mod_header, data_input_stream);
    }
    
    private void setModule(final Module m) {
        if (m != null) {
            this.module = m;
        }
        this.ibxm.set_module(this.module);
        this.songDuration = this.ibxm.calculate_song_duration();
    }
    
    private synchronized boolean initialized(final boolean action, final boolean value) {
        if (action) {
            this.initialized = value;
        }
        return this.initialized;
    }
    
    private synchronized boolean endOfStream(final boolean action, final boolean value) {
        if (action) {
            this.endOfStream = value;
        }
        return this.endOfStream;
    }
    
    private static byte[] trimArray(final byte[] array, final int maxLength) {
        byte[] trimmedArray = null;
        if (array != null && array.length > maxLength) {
            trimmedArray = new byte[maxLength];
            System.arraycopy(array, 0, trimmedArray, 0, maxLength);
        }
        return trimmedArray;
    }
    
    public static void reverseBytes(final byte[] buffer) {
        reverseBytes(buffer, 0, buffer.length);
    }
    
    public static void reverseBytes(final byte[] buffer, final int offset, final int size) {
        for (int i = offset; i < offset + size; i += 2) {
            final byte b = buffer[i];
            buffer[i] = buffer[i + 1];
            buffer[i + 1] = b;
        }
    }
    
    private static byte[] convertAudioBytes(final byte[] audio_bytes, final boolean two_bytes_data) {
        final ByteBuffer dest = ByteBuffer.allocateDirect(audio_bytes.length);
        dest.order(ByteOrder.nativeOrder());
        final ByteBuffer src = ByteBuffer.wrap(audio_bytes);
        src.order(ByteOrder.LITTLE_ENDIAN);
        if (two_bytes_data) {
            final ShortBuffer dest_short = dest.asShortBuffer();
            final ShortBuffer src_short = src.asShortBuffer();
            while (src_short.hasRemaining()) {
                dest_short.put(src_short.get());
            }
        }
        else {
            while (src.hasRemaining()) {
                dest.put(src.get());
            }
        }
        dest.rewind();
        if (!dest.hasArray()) {
            final byte[] arrayBackedBuffer = new byte[dest.capacity()];
            dest.get(arrayBackedBuffer);
            dest.clear();
            return arrayBackedBuffer;
        }
        return dest.array();
    }
    
    private static byte[] appendByteArrays(byte[] arrayOne, byte[] arrayTwo, final int length) {
        if (arrayOne == null && arrayTwo == null) {
            return null;
        }
        byte[] newArray;
        if (arrayOne == null) {
            newArray = new byte[length];
            System.arraycopy(arrayTwo, 0, newArray, 0, length);
            arrayTwo = null;
        }
        else if (arrayTwo == null) {
            newArray = new byte[arrayOne.length];
            System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
            arrayOne = null;
        }
        else {
            newArray = new byte[arrayOne.length + length];
            System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
            System.arraycopy(arrayTwo, 0, newArray, arrayOne.length, length);
            arrayOne = null;
            arrayTwo = null;
        }
        return newArray;
    }
    
    private void errorMessage(final String message) {
        this.logger.errorMessage("CodecWav", message, 0);
    }
    
    private void printStackTrace(final Exception e) {
        this.logger.printStackTrace(e, 1);
    }
}
