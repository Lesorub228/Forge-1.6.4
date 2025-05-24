// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.util.logging.Logger;
import java.util.logging.Level;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class FMLLog
{
    private static FMLRelaunchLog coreLog;
    
    public static void log(final String logChannel, final Level level, final String format, final Object... data) {
        final FMLRelaunchLog coreLog = FMLLog.coreLog;
        FMLRelaunchLog.log(logChannel, level, format, data);
    }
    
    public static void log(final Level level, final String format, final Object... data) {
        final FMLRelaunchLog coreLog = FMLLog.coreLog;
        FMLRelaunchLog.log(level, format, data);
    }
    
    public static void log(final String logChannel, final Level level, final Throwable ex, final String format, final Object... data) {
        final FMLRelaunchLog coreLog = FMLLog.coreLog;
        FMLRelaunchLog.log(logChannel, level, ex, format, data);
    }
    
    public static void log(final Level level, final Throwable ex, final String format, final Object... data) {
        final FMLRelaunchLog coreLog = FMLLog.coreLog;
        FMLRelaunchLog.log(level, ex, format, data);
    }
    
    public static void severe(final String format, final Object... data) {
        log(Level.SEVERE, format, data);
    }
    
    public static void warning(final String format, final Object... data) {
        log(Level.WARNING, format, data);
    }
    
    public static void info(final String format, final Object... data) {
        log(Level.INFO, format, data);
    }
    
    public static void fine(final String format, final Object... data) {
        log(Level.FINE, format, data);
    }
    
    public static void finer(final String format, final Object... data) {
        log(Level.FINER, format, data);
    }
    
    public static void finest(final String format, final Object... data) {
        log(Level.FINEST, format, data);
    }
    
    public static Logger getLogger() {
        return FMLLog.coreLog.getLogger();
    }
    
    public static void makeLog(final String logChannel) {
        final FMLRelaunchLog coreLog = FMLLog.coreLog;
        FMLRelaunchLog.makeLog(logChannel);
    }
    
    static {
        FMLLog.coreLog = FMLRelaunchLog.log;
    }
}
