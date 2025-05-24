// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.io.PushbackInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import cpw.mods.fml.common.Loader;
import java.util.Iterator;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.io.IOException;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Locale;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.FMLInjectionData;
import java.util.TreeMap;
import java.util.Map;
import java.io.File;
import com.google.common.base.CharMatcher;
import java.util.regex.Pattern;

public class Configuration
{
    private static boolean[] configMarkers;
    private static final int ITEM_SHIFT = 256;
    private static final int MAX_BLOCKS = 4096;
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_BLOCK = "block";
    public static final String CATEGORY_ITEM = "item";
    public static final String ALLOWED_CHARS = "._-";
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String CATEGORY_SPLITTER = ".";
    public static final String NEW_LINE;
    private static final Pattern CONFIG_START;
    private static final Pattern CONFIG_END;
    public static final CharMatcher allowedProperties;
    private static Configuration PARENT;
    File file;
    private Map<String, ConfigCategory> categories;
    private Map<String, Configuration> children;
    private boolean caseSensitiveCustomCategories;
    public String defaultEncoding;
    private String fileName;
    public boolean isChild;
    private boolean changed;
    
    public Configuration() {
        this.categories = new TreeMap<String, ConfigCategory>();
        this.children = new TreeMap<String, Configuration>();
        this.defaultEncoding = "UTF-8";
        this.fileName = null;
        this.isChild = false;
        this.changed = false;
    }
    
    public Configuration(final File file) {
        this.categories = new TreeMap<String, ConfigCategory>();
        this.children = new TreeMap<String, Configuration>();
        this.defaultEncoding = "UTF-8";
        this.fileName = null;
        this.isChild = false;
        this.changed = false;
        this.file = file;
        final String basePath = ((File)FMLInjectionData.data()[6]).getAbsolutePath().replace(File.separatorChar, '/').replace("/.", "");
        final String path = file.getAbsolutePath().replace(File.separatorChar, '/').replace("/./", "/").replace(basePath, "");
        if (Configuration.PARENT != null) {
            Configuration.PARENT.setChild(path, this);
            this.isChild = true;
        }
        else {
            this.fileName = path;
            this.load();
        }
    }
    
    public Configuration(final File file, final boolean caseSensitiveCustomCategories) {
        this(file);
        this.caseSensitiveCustomCategories = caseSensitiveCustomCategories;
    }
    
    public Property getBlock(final String key, final int defaultID) {
        return this.getBlock("block", key, defaultID, null);
    }
    
    public Property getBlock(final String key, final int defaultID, final String comment) {
        return this.getBlock("block", key, defaultID, comment);
    }
    
    public Property getBlock(final String category, final String key, final int defaultID) {
        return this.getBlockInternal(category, key, defaultID, null, 256, aqz.s.length);
    }
    
    public Property getBlock(final String category, final String key, final int defaultID, final String comment) {
        return this.getBlockInternal(category, key, defaultID, comment, 256, aqz.s.length);
    }
    
    public Property getTerrainBlock(final String category, final String key, final int defaultID, final String comment) {
        return this.getBlockInternal(category, key, defaultID, comment, 0, 256);
    }
    
    private Property getBlockInternal(final String category, final String key, int defaultID, final String comment, final int lower, final int upper) {
        final Property prop = this.get(category, key, -1, comment);
        if (prop.getInt() != -1) {
            Configuration.configMarkers[prop.getInt()] = true;
            return prop;
        }
        if (defaultID < lower) {
            FMLLog.warning("Mod attempted to get a block ID with a default in the Terrain Generation section, mod authors should make sure there defaults are above 256 unless explicitly needed for terrain generation. Most ores do not need to be below 256.", new Object[0]);
            FMLLog.warning("Config \"%s\" Category: \"%s\" Key: \"%s\" Default: %d", this.fileName, category, key, defaultID);
            defaultID = upper - 1;
        }
        if (aqz.s[defaultID] == null && !Configuration.configMarkers[defaultID]) {
            prop.set(defaultID);
            Configuration.configMarkers[defaultID] = true;
            return prop;
        }
        for (int j = upper - 1; j > 0; --j) {
            if (aqz.s[j] == null && !Configuration.configMarkers[j]) {
                prop.set(j);
                Configuration.configMarkers[j] = true;
                return prop;
            }
        }
        throw new RuntimeException("No more block ids available for " + key);
    }
    
    public Property getItem(final String key, final int defaultID) {
        return this.getItem("item", key, defaultID, null);
    }
    
    public Property getItem(final String key, final int defaultID, final String comment) {
        return this.getItem("item", key, defaultID, comment);
    }
    
    public Property getItem(final String category, final String key, final int defaultID) {
        return this.getItem(category, key, defaultID, null);
    }
    
    public Property getItem(final String category, final String key, final int defaultID, final String comment) {
        final Property prop = this.get(category, key, -1, comment);
        final int defaultShift = defaultID + 256;
        if (prop.getInt() != -1) {
            Configuration.configMarkers[prop.getInt() + 256] = true;
            return prop;
        }
        if (defaultID < 3840) {
            FMLLog.warning("Mod attempted to get a item ID with a default value in the block ID section, mod authors should make sure there defaults are above %d unless explicitly needed so that all block ids are free to store blocks.", 3840);
            FMLLog.warning("Config \"%s\" Category: \"%s\" Key: \"%s\" Default: %d", this.fileName, category, key, defaultID);
        }
        if (yc.g[defaultShift] == null && !Configuration.configMarkers[defaultShift] && defaultShift >= aqz.s.length) {
            prop.set(defaultID);
            Configuration.configMarkers[defaultShift] = true;
            return prop;
        }
        for (int x = yc.g.length - 1; x >= 256; --x) {
            if (yc.g[x] == null && !Configuration.configMarkers[x]) {
                prop.set(x - 256);
                Configuration.configMarkers[x] = true;
                return prop;
            }
        }
        throw new RuntimeException("No more item ids available for " + key);
    }
    
    public Property get(final String category, final String key, final int defaultValue) {
        return this.get(category, key, defaultValue, null);
    }
    
    public Property get(final String category, final String key, final int defaultValue, final String comment) {
        final Property prop = this.get(category, key, Integer.toString(defaultValue), comment, Property.Type.INTEGER);
        if (!prop.isIntValue()) {
            prop.set(defaultValue);
        }
        return prop;
    }
    
    public Property get(final String category, final String key, final boolean defaultValue) {
        return this.get(category, key, defaultValue, null);
    }
    
    public Property get(final String category, final String key, final boolean defaultValue, final String comment) {
        final Property prop = this.get(category, key, Boolean.toString(defaultValue), comment, Property.Type.BOOLEAN);
        if (!prop.isBooleanValue()) {
            prop.set(defaultValue);
        }
        return prop;
    }
    
    public Property get(final String category, final String key, final double defaultValue) {
        return this.get(category, key, defaultValue, null);
    }
    
    public Property get(final String category, final String key, final double defaultValue, final String comment) {
        final Property prop = this.get(category, key, Double.toString(defaultValue), comment, Property.Type.DOUBLE);
        if (!prop.isDoubleValue()) {
            prop.set(defaultValue);
        }
        return prop;
    }
    
    public Property get(final String category, final String key, final String defaultValue) {
        return this.get(category, key, defaultValue, null);
    }
    
    public Property get(final String category, final String key, final String defaultValue, final String comment) {
        return this.get(category, key, defaultValue, comment, Property.Type.STRING);
    }
    
    public Property get(final String category, final String key, final String[] defaultValue) {
        return this.get(category, key, defaultValue, null);
    }
    
    public Property get(final String category, final String key, final String[] defaultValue, final String comment) {
        return this.get(category, key, defaultValue, comment, Property.Type.STRING);
    }
    
    public Property get(final String category, final String key, final int[] defaultValue) {
        return this.get(category, key, defaultValue, null);
    }
    
    public Property get(final String category, final String key, final int[] defaultValue, final String comment) {
        final String[] values = new String[defaultValue.length];
        for (int i = 0; i < defaultValue.length; ++i) {
            values[i] = Integer.toString(defaultValue[i]);
        }
        final Property prop = this.get(category, key, values, comment, Property.Type.INTEGER);
        if (!prop.isIntList()) {
            prop.set(values);
        }
        return prop;
    }
    
    public Property get(final String category, final String key, final double[] defaultValue) {
        return this.get(category, key, defaultValue, null);
    }
    
    public Property get(final String category, final String key, final double[] defaultValue, final String comment) {
        final String[] values = new String[defaultValue.length];
        for (int i = 0; i < defaultValue.length; ++i) {
            values[i] = Double.toString(defaultValue[i]);
        }
        final Property prop = this.get(category, key, values, comment, Property.Type.DOUBLE);
        if (!prop.isDoubleList()) {
            prop.set(values);
        }
        return prop;
    }
    
    public Property get(final String category, final String key, final boolean[] defaultValue) {
        return this.get(category, key, defaultValue, null);
    }
    
    public Property get(final String category, final String key, final boolean[] defaultValue, final String comment) {
        final String[] values = new String[defaultValue.length];
        for (int i = 0; i < defaultValue.length; ++i) {
            values[i] = Boolean.toString(defaultValue[i]);
        }
        final Property prop = this.get(category, key, values, comment, Property.Type.BOOLEAN);
        if (!prop.isBooleanList()) {
            prop.set(values);
        }
        return prop;
    }
    
    public Property get(String category, final String key, final String defaultValue, final String comment, final Property.Type type) {
        if (!this.caseSensitiveCustomCategories) {
            category = category.toLowerCase(Locale.ENGLISH);
        }
        final ConfigCategory cat = this.getCategory(category);
        if (cat.containsKey(key)) {
            Property prop = cat.get(key);
            if (prop.getType() == null) {
                prop = new Property(prop.getName(), prop.getString(), type);
                cat.put(key, prop);
            }
            prop.comment = comment;
            return prop;
        }
        if (defaultValue != null) {
            final Property prop = new Property(key, defaultValue, type);
            prop.set(defaultValue);
            cat.put(key, prop);
            prop.comment = comment;
            return prop;
        }
        return null;
    }
    
    public Property get(String category, final String key, final String[] defaultValue, final String comment, final Property.Type type) {
        if (!this.caseSensitiveCustomCategories) {
            category = category.toLowerCase(Locale.ENGLISH);
        }
        final ConfigCategory cat = this.getCategory(category);
        if (cat.containsKey(key)) {
            Property prop = cat.get(key);
            if (prop.getType() == null) {
                prop = new Property(prop.getName(), prop.getString(), type);
                cat.put(key, prop);
            }
            prop.comment = comment;
            return prop;
        }
        if (defaultValue != null) {
            final Property prop = new Property(key, defaultValue, type);
            prop.comment = comment;
            cat.put(key, prop);
            return prop;
        }
        return null;
    }
    
    public boolean hasCategory(final String category) {
        return this.categories.get(category) != null;
    }
    
    public boolean hasKey(final String category, final String key) {
        final ConfigCategory cat = this.categories.get(category);
        return cat != null && cat.containsKey(key);
    }
    
    public void load() {
        if (Configuration.PARENT != null && Configuration.PARENT != this) {
            return;
        }
        BufferedReader buffer = null;
        UnicodeInputStreamReader input = null;
        try {
            if (this.file.getParentFile() != null) {
                this.file.getParentFile().mkdirs();
            }
            if (!this.file.exists() && !this.file.createNewFile()) {
                return;
            }
            if (this.file.canRead()) {
                input = new UnicodeInputStreamReader(new FileInputStream(this.file), this.defaultEncoding);
                this.defaultEncoding = input.getEncoding();
                buffer = new BufferedReader(input);
                ConfigCategory currentCat = null;
                Property.Type type = null;
                ArrayList<String> tmpList = null;
                int lineNum = 0;
                String name = null;
                while (true) {
                    ++lineNum;
                    final String line = buffer.readLine();
                    if (line == null) {
                        break;
                    }
                    final Matcher start = Configuration.CONFIG_START.matcher(line);
                    final Matcher end = Configuration.CONFIG_END.matcher(line);
                    if (start.matches()) {
                        this.fileName = start.group(1);
                        this.categories = new TreeMap<String, ConfigCategory>();
                    }
                    else if (end.matches()) {
                        this.fileName = end.group(1);
                        final Configuration child = new Configuration();
                        child.categories = this.categories;
                        this.children.put(this.fileName, child);
                    }
                    else {
                        int nameStart = -1;
                        int nameEnd = -1;
                        boolean skip = false;
                        boolean quoted = false;
                        for (int i = 0; i < line.length() && !skip; ++i) {
                            if (Character.isLetterOrDigit(line.charAt(i)) || "._-".indexOf(line.charAt(i)) != -1 || (quoted && line.charAt(i) != '\"')) {
                                if (nameStart == -1) {
                                    nameStart = i;
                                }
                                nameEnd = i;
                            }
                            else if (!Character.isWhitespace(line.charAt(i))) {
                                switch (line.charAt(i)) {
                                    case '#': {
                                        skip = true;
                                        break;
                                    }
                                    case '\"': {
                                        if (quoted) {
                                            quoted = false;
                                        }
                                        if (!quoted && nameStart == -1) {
                                            quoted = true;
                                            break;
                                        }
                                        break;
                                    }
                                    case '{': {
                                        name = line.substring(nameStart, nameEnd + 1);
                                        final String qualifiedName = ConfigCategory.getQualifiedName(name, currentCat);
                                        final ConfigCategory cat = this.categories.get(qualifiedName);
                                        if (cat == null) {
                                            currentCat = new ConfigCategory(name, currentCat);
                                            this.categories.put(qualifiedName, currentCat);
                                        }
                                        else {
                                            currentCat = cat;
                                        }
                                        name = null;
                                        break;
                                    }
                                    case '}': {
                                        if (currentCat == null) {
                                            throw new RuntimeException(String.format("Config file corrupt, attepted to close to many categories '%s:%d'", this.fileName, lineNum));
                                        }
                                        currentCat = currentCat.parent;
                                        break;
                                    }
                                    case '=': {
                                        name = line.substring(nameStart, nameEnd + 1);
                                        if (currentCat == null) {
                                            throw new RuntimeException(String.format("'%s' has no scope in '%s:%d'", name, this.fileName, lineNum));
                                        }
                                        final Property prop = new Property(name, line.substring(i + 1), type, true);
                                        i = line.length();
                                        currentCat.put(name, prop);
                                        break;
                                    }
                                    case ':': {
                                        type = Property.Type.tryParse(line.substring(nameStart, nameEnd + 1).charAt(0));
                                        nameEnd = (nameStart = -1);
                                        break;
                                    }
                                    case '<': {
                                        if (tmpList != null) {
                                            throw new RuntimeException(String.format("Malformed list property \"%s:%d\"", this.fileName, lineNum));
                                        }
                                        name = line.substring(nameStart, nameEnd + 1);
                                        if (currentCat == null) {
                                            throw new RuntimeException(String.format("'%s' has no scope in '%s:%d'", name, this.fileName, lineNum));
                                        }
                                        tmpList = new ArrayList<String>();
                                        skip = true;
                                        break;
                                    }
                                    case '>': {
                                        if (tmpList == null) {
                                            throw new RuntimeException(String.format("Malformed list property \"%s:%d\"", this.fileName, lineNum));
                                        }
                                        currentCat.put(name, new Property(name, tmpList.toArray(new String[tmpList.size()]), type));
                                        name = null;
                                        tmpList = null;
                                        type = null;
                                        break;
                                    }
                                    default: {
                                        throw new RuntimeException(String.format("Unknown character '%s' in '%s:%d'", line.charAt(i), this.fileName, lineNum));
                                    }
                                }
                            }
                        }
                        if (quoted) {
                            throw new RuntimeException(String.format("Unmatched quote in '%s:%d'", this.fileName, lineNum));
                        }
                        if (tmpList == null || skip) {
                            continue;
                        }
                        tmpList.add(line.trim());
                    }
                }
            }
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        finally {
            if (buffer != null) {
                try {
                    buffer.close();
                }
                catch (final IOException ex) {}
            }
            if (input != null) {
                try {
                    input.close();
                }
                catch (final IOException ex2) {}
            }
        }
        this.resetChangedState();
    }
    
    public void save() {
        if (Configuration.PARENT != null && Configuration.PARENT != this) {
            Configuration.PARENT.save();
            return;
        }
        try {
            if (this.file.getParentFile() != null) {
                this.file.getParentFile().mkdirs();
            }
            if (!this.file.exists() && !this.file.createNewFile()) {
                return;
            }
            if (this.file.canWrite()) {
                final FileOutputStream fos = new FileOutputStream(this.file);
                final BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, this.defaultEncoding));
                buffer.write("# Configuration file" + Configuration.NEW_LINE + Configuration.NEW_LINE);
                if (this.children.isEmpty()) {
                    this.save(buffer);
                }
                else {
                    for (final Map.Entry<String, Configuration> entry : this.children.entrySet()) {
                        buffer.write("START: \"" + entry.getKey() + "\"" + Configuration.NEW_LINE);
                        entry.getValue().save(buffer);
                        buffer.write("END: \"" + entry.getKey() + "\"" + Configuration.NEW_LINE + Configuration.NEW_LINE);
                    }
                }
                buffer.close();
                fos.close();
            }
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    private void save(final BufferedWriter out) throws IOException {
        for (final ConfigCategory cat : this.categories.values()) {
            if (!cat.isChild()) {
                cat.write(out, 0);
                out.newLine();
            }
        }
    }
    
    public ConfigCategory getCategory(final String category) {
        ConfigCategory ret = this.categories.get(category);
        if (ret == null) {
            if (category.contains(".")) {
                final String[] hierarchy = category.split("\\.");
                ConfigCategory parent = this.categories.get(hierarchy[0]);
                if (parent == null) {
                    parent = new ConfigCategory(hierarchy[0]);
                    this.categories.put(parent.getQualifiedName(), parent);
                    this.changed = true;
                }
                for (int i = 1; i < hierarchy.length; ++i) {
                    final String name = ConfigCategory.getQualifiedName(hierarchy[i], parent);
                    ConfigCategory child = this.categories.get(name);
                    if (child == null) {
                        child = new ConfigCategory(hierarchy[i], parent);
                        this.categories.put(name, child);
                        this.changed = true;
                    }
                    ret = child;
                    parent = child;
                }
            }
            else {
                ret = new ConfigCategory(category);
                this.categories.put(category, ret);
                this.changed = true;
            }
        }
        return ret;
    }
    
    public void removeCategory(final ConfigCategory category) {
        for (final ConfigCategory child : category.getChildren()) {
            this.removeCategory(child);
        }
        if (this.categories.containsKey(category.getQualifiedName())) {
            this.categories.remove(category.getQualifiedName());
            if (category.parent != null) {
                category.parent.removeChild(category);
            }
            this.changed = true;
        }
    }
    
    public void addCustomCategoryComment(String category, final String comment) {
        if (!this.caseSensitiveCustomCategories) {
            category = category.toLowerCase(Locale.ENGLISH);
        }
        this.getCategory(category).setComment(comment);
    }
    
    private void setChild(final String name, final Configuration child) {
        if (!this.children.containsKey(name)) {
            this.children.put(name, child);
            this.changed = true;
        }
        else {
            final Configuration old = this.children.get(name);
            child.categories = old.categories;
            child.fileName = old.fileName;
            old.changed = true;
        }
    }
    
    public static void enableGlobalConfig() {
        (Configuration.PARENT = new Configuration(new File(Loader.instance().getConfigDir(), "global.cfg"))).load();
    }
    
    public boolean hasChanged() {
        if (this.changed) {
            return true;
        }
        for (final ConfigCategory cat : this.categories.values()) {
            if (cat.hasChanged()) {
                return true;
            }
        }
        for (final Configuration child : this.children.values()) {
            if (child.hasChanged()) {
                return true;
            }
        }
        return false;
    }
    
    private void resetChangedState() {
        this.changed = false;
        for (final ConfigCategory cat : this.categories.values()) {
            cat.resetChangedState();
        }
        for (final Configuration child : this.children.values()) {
            child.resetChangedState();
        }
    }
    
    public Set<String> getCategoryNames() {
        return (Set<String>)ImmutableSet.copyOf((Collection)this.categories.keySet());
    }
    
    static {
        Configuration.configMarkers = new boolean[yc.g.length];
        CONFIG_START = Pattern.compile("START: \"([^\\\"]+)\"");
        CONFIG_END = Pattern.compile("END: \"([^\\\"]+)\"");
        allowedProperties = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.anyOf((CharSequence)"._-"));
        Configuration.PARENT = null;
        Arrays.fill(Configuration.configMarkers, false);
        NEW_LINE = System.getProperty("line.separator");
    }
    
    public static class UnicodeInputStreamReader extends Reader
    {
        private final InputStreamReader input;
        private final String defaultEnc;
        
        public UnicodeInputStreamReader(final InputStream source, final String encoding) throws IOException {
            this.defaultEnc = encoding;
            String enc = encoding;
            final byte[] data = new byte[4];
            final PushbackInputStream pbStream = new PushbackInputStream(source, data.length);
            final int read = pbStream.read(data, 0, data.length);
            int size = 0;
            final int bom16 = (data[0] & 0xFF) << 8 | (data[1] & 0xFF);
            final int bom17 = bom16 << 8 | (data[2] & 0xFF);
            final int bom18 = bom17 << 8 | (data[3] & 0xFF);
            if (bom17 == 15711167) {
                enc = "UTF-8";
                size = 3;
            }
            else if (bom16 == 65279) {
                enc = "UTF-16BE";
                size = 2;
            }
            else if (bom16 == 65534) {
                enc = "UTF-16LE";
                size = 2;
            }
            else if (bom18 == 65279) {
                enc = "UTF-32BE";
                size = 4;
            }
            else if (bom18 == -131072) {
                enc = "UTF-32LE";
                size = 4;
            }
            if (size < read) {
                pbStream.unread(data, size, read - size);
            }
            this.input = new InputStreamReader(pbStream, enc);
        }
        
        public String getEncoding() {
            return this.input.getEncoding();
        }
        
        @Override
        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            return this.input.read(cbuf, off, len);
        }
        
        @Override
        public void close() throws IOException {
            this.input.close();
        }
    }
}
