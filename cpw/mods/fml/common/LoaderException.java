// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

public class LoaderException extends RuntimeException
{
    private static final long serialVersionUID = -5675297950958861378L;
    
    public LoaderException(final Throwable wrapped) {
        super(wrapped);
    }
    
    public LoaderException() {
    }
}
