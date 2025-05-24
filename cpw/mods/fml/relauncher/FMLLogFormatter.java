// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.relauncher;

import java.util.logging.Level;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;

final class FMLLogFormatter extends Formatter
{
    static final String LINE_SEPARATOR;
    private SimpleDateFormat dateFormat;
    
    FMLLogFormatter() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    @Override
    public String format(final LogRecord record) {
        final StringBuilder msg = new StringBuilder();
        msg.append(this.dateFormat.format(record.getMillis()));
        final Level lvl = record.getLevel();
        String name = lvl.getLocalizedName();
        if (name == null) {
            name = lvl.getName();
        }
        if (name != null && name.length() > 0) {
            msg.append(" [" + name + "] ");
        }
        else {
            msg.append(" ");
        }
        if (record.getLoggerName() != null) {
            msg.append("[" + record.getLoggerName() + "] ");
        }
        else {
            msg.append("[] ");
        }
        msg.append(this.formatMessage(record));
        msg.append(FMLLogFormatter.LINE_SEPARATOR);
        final Throwable thr = record.getThrown();
        if (thr != null) {
            final StringWriter thrDump = new StringWriter();
            thr.printStackTrace(new PrintWriter(thrDump));
            msg.append(thrDump.toString());
        }
        return msg.toString();
    }
    
    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
}
