// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.registry;

import java.util.Hashtable;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import com.google.common.base.Charsets;
import cpw.mods.fml.common.ModContainer;
import java.net.URL;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.FMLCommonHandler;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;

public class LanguageRegistry
{
    private static final LanguageRegistry INSTANCE;
    private Map<String, Properties> modLanguageData;
    
    public LanguageRegistry() {
        this.modLanguageData = new HashMap<String, Properties>();
    }
    
    public static LanguageRegistry instance() {
        return LanguageRegistry.INSTANCE;
    }
    
    public String getStringLocalization(final String key) {
        return this.getStringLocalization(key, FMLCommonHandler.instance().getCurrentLanguage());
    }
    
    public String getStringLocalization(final String key, final String lang) {
        String localizedString = "";
        final Properties langPack = this.modLanguageData.get(lang);
        if (langPack != null && langPack.getProperty(key) != null) {
            localizedString = langPack.getProperty(key);
        }
        return localizedString;
    }
    
    public void addStringLocalization(final String key, final String value) {
        this.addStringLocalization(key, "en_US", value);
    }
    
    public void addStringLocalization(final String key, final String lang, final String value) {
        Properties langPack = this.modLanguageData.get(lang);
        if (langPack == null) {
            langPack = new Properties();
            this.modLanguageData.put(lang, langPack);
        }
        ((Hashtable<String, String>)langPack).put(key, value);
    }
    
    public void addStringLocalization(final Properties langPackAdditions) {
        this.addStringLocalization(langPackAdditions, "en_US");
    }
    
    public void addStringLocalization(final Properties langPackAdditions, final String lang) {
        Properties langPack = this.modLanguageData.get(lang);
        if (langPack == null) {
            langPack = new Properties();
            this.modLanguageData.put(lang, langPack);
        }
        if (langPackAdditions != null) {
            langPack.putAll(langPackAdditions);
        }
    }
    
    public static void reloadLanguageTable() {
    }
    
    public void addNameForObject(final Object objectToName, final String lang, final String name) {
        String objectName;
        if (objectToName instanceof yc) {
            objectName = ((yc)objectToName).a();
        }
        else if (objectToName instanceof aqz) {
            objectName = ((aqz)objectToName).a();
        }
        else {
            if (!(objectToName instanceof ye)) {
                throw new IllegalArgumentException(String.format("Illegal object for naming %s", objectToName));
            }
            objectName = ((ye)objectToName).b().d((ye)objectToName);
        }
        objectName += ".name";
        this.addStringLocalization(objectName, lang, name);
    }
    
    public static void addName(final Object objectToName, final String name) {
        instance().addNameForObject(objectToName, "en_US", name);
    }
    
    public void loadLanguageTable(final Map field_135032_a, final String lang) {
        final Properties usPack = this.modLanguageData.get("en_US");
        if (usPack != null) {
            field_135032_a.putAll(usPack);
        }
        final Properties langPack = this.modLanguageData.get(lang);
        if (langPack == null) {
            return;
        }
        field_135032_a.putAll(langPack);
    }
    
    public void loadLocalization(final String localizationFile, final String lang, final boolean isXML) {
        final URL urlResource = this.getClass().getResource(localizationFile);
        if (urlResource != null) {
            this.loadLocalization(urlResource, lang, isXML);
        }
        else {
            final ModContainer activeModContainer = Loader.instance().activeModContainer();
            if (activeModContainer != null) {
                FMLLog.log(activeModContainer.getModId(), Level.SEVERE, "The language resource %s cannot be located on the classpath. This is a programming error.", localizationFile);
            }
            else {
                FMLLog.log(Level.SEVERE, "The language resource %s cannot be located on the classpath. This is a programming error.", localizationFile);
            }
        }
    }
    
    public void loadLocalization(final URL localizationFile, final String lang, final boolean isXML) {
        InputStream langStream = null;
        final Properties langPack = new Properties();
        try {
            langStream = localizationFile.openStream();
            if (isXML) {
                langPack.loadFromXML(langStream);
            }
            else {
                langPack.load(new InputStreamReader(langStream, Charsets.UTF_8));
            }
            this.addStringLocalization(langPack, lang);
        }
        catch (final IOException e) {
            FMLLog.log(Level.SEVERE, e, "Unable to load localization from file %s", localizationFile);
        }
        finally {
            try {
                if (langStream != null) {
                    langStream.close();
                }
            }
            catch (final IOException ex) {}
        }
    }
    
    static {
        INSTANCE = new LanguageRegistry();
    }
}
