// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm;

import org.objectweb.asm.FieldVisitor;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.common.patcher.ClassPatchManager;
import java.util.Map;
import java.util.jar.JarEntry;
import java.security.cert.Certificate;
import java.security.CodeSource;
import java.awt.Component;
import javax.swing.JOptionPane;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import java.io.IOException;
import java.util.logging.Level;
import com.google.common.io.ByteStreams;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import java.net.URLDecoder;
import com.google.common.base.Charsets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.common.CertificateHelper;
import java.io.File;
import net.minecraft.launchwrapper.LaunchClassLoader;
import cpw.mods.fml.relauncher.IFMLCallHook;

public class FMLSanityChecker implements IFMLCallHook
{
    private static final String FMLFINGERPRINT;
    private static final String FORGEFINGERPRINT;
    private static final String MCFINGERPRINT;
    private LaunchClassLoader cl;
    public static File fmlLocation;
    
    @Override
    public Void call() throws Exception {
        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
        boolean goodFML = false;
        boolean fmlIsJar = false;
        if (codeSource.getLocation().getProtocol().equals("jar")) {
            fmlIsJar = true;
            final Certificate[] certificates = codeSource.getCertificates();
            if (certificates != null) {
                for (final Certificate cert : certificates) {
                    final String fingerprint = CertificateHelper.getFingerprint(cert);
                    if (fingerprint.equals(FMLSanityChecker.FMLFINGERPRINT)) {
                        FMLRelaunchLog.info("Found valid fingerprint for FML. Certificate fingerprint %s", fingerprint);
                        goodFML = true;
                    }
                    else if (fingerprint.equals(FMLSanityChecker.FORGEFINGERPRINT)) {
                        FMLRelaunchLog.info("Found valid fingerprint for Minecraft Forge. Certificate fingerprint %s", fingerprint);
                        goodFML = true;
                    }
                    else {
                        FMLRelaunchLog.severe("Found invalid fingerprint for FML: %s", fingerprint);
                    }
                }
            }
        }
        else {
            goodFML = true;
        }
        boolean goodMC = FMLLaunchHandler.side() == Side.SERVER;
        int certCount = 0;
        try {
            final Class cbr = Class.forName("net.minecraft.client.ClientBrandRetriever", false, (ClassLoader)this.cl);
            codeSource = cbr.getProtectionDomain().getCodeSource();
        }
        catch (final Exception e) {
            goodMC = true;
        }
        JarFile mcJarFile = null;
        if (fmlIsJar && !goodMC && codeSource.getLocation().getProtocol().equals("jar")) {
            try {
                String mcPath = codeSource.getLocation().getPath().substring(5);
                mcPath = mcPath.substring(0, mcPath.lastIndexOf(33));
                mcPath = URLDecoder.decode(mcPath, Charsets.UTF_8.name());
                mcJarFile = new JarFile(mcPath, true);
                mcJarFile.getManifest();
                final JarEntry cbrEntry = mcJarFile.getJarEntry("net/minecraft/client/ClientBrandRetriever.class");
                ByteStreams.toByteArray(mcJarFile.getInputStream(cbrEntry));
                final Certificate[] certificates2 = cbrEntry.getCertificates();
                certCount = ((certificates2 != null) ? certificates2.length : 0);
                if (certificates2 != null) {
                    for (final Certificate cert2 : certificates2) {
                        final String fingerprint2 = CertificateHelper.getFingerprint(cert2);
                        if (fingerprint2.equals(FMLSanityChecker.MCFINGERPRINT)) {
                            FMLRelaunchLog.info("Found valid fingerprint for Minecraft. Certificate fingerprint %s", fingerprint2);
                            goodMC = true;
                        }
                    }
                }
            }
            catch (final Throwable e2) {
                FMLRelaunchLog.log(Level.SEVERE, e2, "A critical error occurred trying to read the minecraft jar file", new Object[0]);
            }
            finally {
                if (mcJarFile != null) {
                    try {
                        mcJarFile.close();
                    }
                    catch (final IOException ex) {}
                }
            }
        }
        else {
            goodMC = true;
        }
        if (!goodMC) {
            FMLRelaunchLog.severe("The minecraft jar %s appears to be corrupt! There has been CRITICAL TAMPERING WITH MINECRAFT, it is highly unlikely minecraft will work! STOP NOW, get a clean copy and try again!", codeSource.getLocation().getFile());
            if (!Boolean.parseBoolean(System.getProperty("fml.ignoreInvalidMinecraftCertificates", "false"))) {
                FMLRelaunchLog.severe("For your safety, FML will not launch minecraft. You will need to fetch a clean version of the minecraft jar file", new Object[0]);
                FMLRelaunchLog.severe("Technical information: The class net.minecraft.client.ClientBrandRetriever should have been associated with the minecraft jar file, and should have returned us a valid, intact minecraft jar location. This did not work. Either you have modified the minecraft jar file (if so run the forge installer again), or you are using a base editing jar that is changing this class (and likely others too). If you REALLY want to run minecraft in this configuration, add the flag -Dfml.ignoreInvalidMinecraftCertificates=true to the 'JVM settings' in your launcher profile.", new Object[0]);
                System.exit(1);
            }
            else {
                FMLRelaunchLog.severe("FML has been ordered to ignore the invalid or missing minecraft certificate. This is very likely to cause a problem!", new Object[0]);
                FMLRelaunchLog.severe("Technical information: ClientBrandRetriever was at %s, there were %d certificates for it", codeSource.getLocation(), certCount);
            }
        }
        if (!goodFML) {
            FMLRelaunchLog.severe("FML appears to be missing any signature data. This is not a good thing", new Object[0]);
        }
        final byte[] mlClass = this.cl.getClassBytes("ModLoader");
        if (mlClass == null) {
            return null;
        }
        final MLDetectorClassVisitor mlTester = new MLDetectorClassVisitor();
        final ClassReader cr = new ClassReader(mlClass);
        cr.accept((ClassVisitor)mlTester, 1);
        if (!mlTester.foundMarker) {
            JOptionPane.showMessageDialog(null, "<html>CRITICAL ERROR<br/>ModLoader was detected in this environment<br/>ForgeModLoader cannot be installed alongside ModLoader<br/>All mods should work without ModLoader being installed<br/>Because ForgeModLoader is 100% compatible with ModLoader<br/>Re-install Minecraft Forge or Forge ModLoader into a clean<br/>jar and try again.", "ForgeModLoader critical error", 0);
            throw new RuntimeException("Invalid ModLoader class detected");
        }
        return null;
    }
    
    @Override
    public void injectData(final Map<String, Object> data) {
        this.cl = data.get("classLoader");
        final File mcDir = data.get("mcLocation");
        FMLSanityChecker.fmlLocation = data.get("coremodLocation");
        ClassPatchManager.INSTANCE.setup(FMLLaunchHandler.side());
        FMLDeobfuscatingRemapper.INSTANCE.setup(mcDir, this.cl, data.get("deobfuscationFileName"));
    }
    
    static {
        FMLFINGERPRINT = "51:0A:FB:4C:AF:A4:A0:F2:F5:CF:C5:0E:B4:CC:3C:30:24:4A:E3:8E".toLowerCase().replace(":", "");
        FORGEFINGERPRINT = "E3:C3:D5:0C:7C:98:6D:F7:4C:64:5C:0A:C5:46:39:74:1C:90:A5:57".toLowerCase().replace(":", "");
        MCFINGERPRINT = "CD:99:95:96:56:F7:53:DC:28:D8:63:B4:67:69:F7:F8:FB:AE:FC:FC".toLowerCase().replace(":", "");
    }
    
    static class MLDetectorClassVisitor extends ClassVisitor
    {
        private boolean foundMarker;
        
        private MLDetectorClassVisitor() {
            super(262144);
            this.foundMarker = false;
        }
        
        public FieldVisitor visitField(final int arg0, final String arg1, final String arg2, final String arg3, final Object arg4) {
            if ("fmlMarker".equals(arg1)) {
                this.foundMarker = true;
            }
            return null;
        }
    }
}
