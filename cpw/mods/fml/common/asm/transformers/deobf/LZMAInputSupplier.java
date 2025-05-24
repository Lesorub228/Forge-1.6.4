// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm.transformers.deobf;

import java.io.IOException;
import LZMA.LzmaInputStream;
import java.io.InputStream;
import com.google.common.io.InputSupplier;

public class LZMAInputSupplier implements InputSupplier<InputStream>
{
    private InputStream compressedData;
    
    public LZMAInputSupplier(final InputStream compressedData) {
        this.compressedData = compressedData;
    }
    
    public InputStream getInput() throws IOException {
        return (InputStream)new LzmaInputStream(this.compressedData);
    }
}
