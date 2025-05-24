// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

public class ForgeVersion
{
    public static final int majorVersion = 9;
    public static final int minorVersion = 11;
    public static final int revisionVersion = 1;
    public static final int buildVersion = 965;
    
    public static int getMajorVersion() {
        return 9;
    }
    
    public static int getMinorVersion() {
        return 11;
    }
    
    public static int getRevisionVersion() {
        return 1;
    }
    
    public static int getBuildVersion() {
        return 965;
    }
    
    public static String getVersion() {
        return String.format("%d.%d.%d.%d", 9, 11, 1, 965);
    }
}
