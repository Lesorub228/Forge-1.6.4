// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.relauncher;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.io.OutputStream;
import com.google.common.base.Throwables;
import net.minecraft.launchwrapper.LogWrapper;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.io.PrintStream;
import java.io.File;

public class FMLRelaunchLog
{
    public static FMLRelaunchLog log;
    static File minecraftHome;
    private static boolean configured;
    private static Thread consoleLogThread;
    private static PrintStream errCache;
    private Logger myLog;
    private static FileHandler fileHandler;
    private static FMLLogFormatter formatter;
    static String logFileNamePattern;
    
    private FMLRelaunchLog() {
    }
    
    private static void configureLogging() {
        LogManager.getLogManager().reset();
        final Logger globalLogger = Logger.getLogger("global");
        globalLogger.setLevel(Level.OFF);
        LogWrapper.retarget(FMLRelaunchLog.log.myLog = Logger.getLogger("ForgeModLoader"));
        final Logger stdOut = Logger.getLogger("STDOUT");
        stdOut.setParent(FMLRelaunchLog.log.myLog);
        final Logger stdErr = Logger.getLogger("STDERR");
        stdErr.setParent(FMLRelaunchLog.log.myLog);
        FMLRelaunchLog.log.myLog.setLevel(Level.ALL);
        FMLRelaunchLog.log.myLog.setUseParentHandlers(false);
        (FMLRelaunchLog.consoleLogThread = new Thread(new ConsoleLogThread())).setDaemon(true);
        FMLRelaunchLog.consoleLogThread.start();
        FMLRelaunchLog.formatter = new FMLLogFormatter();
        try {
            final File logPath = new File(FMLRelaunchLog.minecraftHome, FMLRelaunchLog.logFileNamePattern);
            FMLRelaunchLog.fileHandler = new FileHandler(logPath.getPath(), 0, 3) {
                @Override
                public synchronized void close() throws SecurityException {
                }
            };
        }
        catch (final Throwable t) {
            throw Throwables.propagate(t);
        }
        resetLoggingHandlers();
        FMLRelaunchLog.errCache = System.err;
        System.setOut(new PrintStream(new LoggingOutStream(stdOut), true));
        System.setErr(new PrintStream(new LoggingOutStream(stdErr), true));
        FMLRelaunchLog.configured = true;
    }
    
    private static void resetLoggingHandlers() {
        ConsoleLogThread.wrappedHandler.setLevel(Level.parse(System.getProperty("fml.log.level", "INFO")));
        FMLRelaunchLog.log.myLog.addHandler(new ConsoleLogWrapper());
        ConsoleLogThread.wrappedHandler.setFormatter(FMLRelaunchLog.formatter);
        FMLRelaunchLog.fileHandler.setLevel(Level.ALL);
        FMLRelaunchLog.fileHandler.setFormatter(FMLRelaunchLog.formatter);
        FMLRelaunchLog.log.myLog.addHandler(FMLRelaunchLog.fileHandler);
    }
    
    public static void loadLogConfiguration(final File logConfigFile) {
        if (logConfigFile != null && logConfigFile.exists() && logConfigFile.canRead()) {
            try {
                LogManager.getLogManager().readConfiguration(new FileInputStream(logConfigFile));
                resetLoggingHandlers();
            }
            catch (final Exception e) {
                log(Level.SEVERE, e, "Error reading logging configuration file %s", logConfigFile.getName());
            }
        }
    }
    
    public static void log(final String logChannel, final Level level, final String format, final Object... data) {
        makeLog(logChannel);
        Logger.getLogger(logChannel).log(level, String.format(format, data));
    }
    
    public static void log(final Level level, final String format, final Object... data) {
        if (!FMLRelaunchLog.configured) {
            configureLogging();
        }
        FMLRelaunchLog.log.myLog.log(level, String.format(format, data));
    }
    
    public static void log(final String logChannel, final Level level, final Throwable ex, final String format, final Object... data) {
        makeLog(logChannel);
        Logger.getLogger(logChannel).log(level, String.format(format, data), ex);
    }
    
    public static void log(final Level level, final Throwable ex, final String format, final Object... data) {
        if (!FMLRelaunchLog.configured) {
            configureLogging();
        }
        FMLRelaunchLog.log.myLog.log(level, String.format(format, data), ex);
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
    
    public Logger getLogger() {
        return this.myLog;
    }
    
    public static void makeLog(final String logChannel) {
        final Logger l = Logger.getLogger(logChannel);
        l.setParent(FMLRelaunchLog.log.myLog);
    }
    
    static {
        FMLRelaunchLog.log = new FMLRelaunchLog();
    }
    
    private static class ConsoleLogWrapper extends Handler
    {
        @Override
        public void publish(final LogRecord record) {
            final boolean currInt = Thread.interrupted();
            try {
                ConsoleLogThread.recordQueue.put(record);
            }
            catch (final InterruptedException e) {
                e.printStackTrace(FMLRelaunchLog.errCache);
            }
            if (currInt) {
                Thread.currentThread().interrupt();
            }
        }
        
        @Override
        public void flush() {
        }
        
        @Override
        public void close() throws SecurityException {
        }
    }
    
    private static class ConsoleLogThread implements Runnable
    {
        static ConsoleHandler wrappedHandler;
        static LinkedBlockingQueue<LogRecord> recordQueue;
        
        @Override
        public void run() {
            while (true) {
                try {
                    final LogRecord lr = ConsoleLogThread.recordQueue.take();
                    ConsoleLogThread.wrappedHandler.publish(lr);
                }
                catch (final InterruptedException e) {
                    e.printStackTrace(FMLRelaunchLog.errCache);
                    Thread.interrupted();
                }
            }
        }
        
        static {
            ConsoleLogThread.wrappedHandler = new ConsoleHandler();
            ConsoleLogThread.recordQueue = new LinkedBlockingQueue<LogRecord>();
        }
    }
    
    private static class LoggingOutStream extends ByteArrayOutputStream
    {
        private Logger log;
        private StringBuilder currentMessage;
        
        public LoggingOutStream(final Logger log) {
            this.log = log;
            this.currentMessage = new StringBuilder();
        }
        
        @Override
        public void flush() throws IOException {
            synchronized (FMLRelaunchLog.class) {
                super.flush();
                final String record = this.toString();
                super.reset();
                this.currentMessage.append(record.replace(FMLLogFormatter.LINE_SEPARATOR, "\n"));
                int lastIdx = -1;
                for (int idx = this.currentMessage.indexOf("\n", lastIdx + 1); idx >= 0; idx = this.currentMessage.indexOf("\n", lastIdx + 1)) {
                    this.log.log(Level.INFO, this.currentMessage.substring(lastIdx + 1, idx));
                    lastIdx = idx;
                }
                if (lastIdx >= 0) {
                    final String rem = this.currentMessage.substring(lastIdx + 1);
                    this.currentMessage.setLength(0);
                    this.currentMessage.append(rem);
                }
            }
        }
    }
}
