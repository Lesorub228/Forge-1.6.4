// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

public class WrongMinecraftVersionException extends RuntimeException
{
    public ModContainer mod;
    
    public WrongMinecraftVersionException(final ModContainer mod) {
        this.mod = mod;
    }
}
