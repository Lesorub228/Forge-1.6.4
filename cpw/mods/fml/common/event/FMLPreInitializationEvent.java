// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.event;

import java.security.CodeSource;
import java.security.cert.Certificate;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Logger;
import cpw.mods.fml.common.FMLModContainer;
import java.util.Properties;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import java.io.File;
import cpw.mods.fml.common.ModMetadata;

public class FMLPreInitializationEvent extends FMLStateEvent
{
    private ModMetadata modMetadata;
    private File sourceFile;
    private File configurationDir;
    private File suggestedConfigFile;
    private ASMDataTable asmData;
    private ModContainer modContainer;
    
    public FMLPreInitializationEvent(final Object... data) {
        super(data);
        this.asmData = (ASMDataTable)data[0];
        this.configurationDir = (File)data[1];
    }
    
    @Override
    public LoaderState.ModState getModState() {
        return LoaderState.ModState.PREINITIALIZED;
    }
    
    @Override
    public void applyModContainer(final ModContainer activeContainer) {
        this.modContainer = activeContainer;
        this.modMetadata = activeContainer.getMetadata();
        this.sourceFile = activeContainer.getSource();
        this.suggestedConfigFile = new File(this.configurationDir, activeContainer.getModId() + ".cfg");
    }
    
    public File getSourceFile() {
        return this.sourceFile;
    }
    
    public ModMetadata getModMetadata() {
        return this.modMetadata;
    }
    
    public File getModConfigurationDirectory() {
        return this.configurationDir;
    }
    
    public File getSuggestedConfigurationFile() {
        return this.suggestedConfigFile;
    }
    
    public ASMDataTable getAsmData() {
        return this.asmData;
    }
    
    public Properties getVersionProperties() {
        if (this.modContainer instanceof FMLModContainer) {
            return ((FMLModContainer)this.modContainer).searchForVersionProperties();
        }
        return null;
    }
    
    public Logger getModLog() {
        final Logger log = Logger.getLogger(this.modContainer.getModId());
        log.setParent(FMLLog.getLogger());
        return log;
    }
    
    @Deprecated
    public Certificate[] getFMLSigningCertificates() {
        final CodeSource codeSource = this.getClass().getClassLoader().getParent().getClass().getProtectionDomain().getCodeSource();
        final Certificate[] certs = codeSource.getCertificates();
        if (certs == null) {
            return new Certificate[0];
        }
        return certs;
    }
}
