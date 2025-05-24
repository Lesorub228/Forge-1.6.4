// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.util.concurrent.Callable;

public interface ICrashCallable extends Callable<String>
{
    String getLabel();
}
