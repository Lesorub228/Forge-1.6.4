// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.server;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.Iterator;
import cpw.mods.fml.common.FMLLog;
import java.util.zip.ZipEntry;
import java.util.Collections;
import java.util.zip.ZipFile;
import java.io.File;
import java.io.IOException;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.ItemData;
import com.google.common.collect.MapDifference;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import com.google.common.collect.ImmutableList;
import java.util.List;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.FMLCommonHandler;
import java.util.regex.Pattern;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.IFMLSidedHandler;

public class FMLServerHandler implements IFMLSidedHandler
{
    private static final FMLServerHandler INSTANCE;
    private MinecraftServer server;
    private static final Pattern assetENUSLang;
    
    private FMLServerHandler() {
        FMLCommonHandler.instance().beginLoading(this);
    }
    
    @Override
    public void beginServerLoading(final MinecraftServer minecraftServer) {
        this.server = minecraftServer;
        Loader.instance().loadMods();
    }
    
    @Override
    public void finishServerLoading() {
        Loader.instance().initializeMods();
        LanguageRegistry.reloadLanguageTable();
        GameData.initializeServerGate(1);
    }
    
    @Override
    public void haltGame(final String message, final Throwable exception) {
        throw new RuntimeException(message, exception);
    }
    
    @Override
    public MinecraftServer getServer() {
        return this.server;
    }
    
    public static FMLServerHandler instance() {
        return FMLServerHandler.INSTANCE;
    }
    
    @Override
    public List<String> getAdditionalBrandingInformation() {
        return (List<String>)ImmutableList.of();
    }
    
    @Override
    public Side getSide() {
        return Side.SERVER;
    }
    
    @Override
    public void showGuiScreen(final Object clientGuiElement) {
    }
    
    @Override
    public nn spawnEntityIntoClientWorld(final EntityRegistry.EntityRegistration er, final EntitySpawnPacket packet) {
        return null;
    }
    
    @Override
    public void adjustEntityLocationOnClient(final EntitySpawnAdjustmentPacket entitySpawnAdjustmentPacket) {
    }
    
    @Override
    public void sendPacket(final ey packet) {
        throw new RuntimeException("You cannot send a bare packet without a target on the server!");
    }
    
    @Override
    public void displayMissingMods(final ModMissingPacket modMissingPacket) {
    }
    
    @Override
    public void handleTinyPacket(final ez handler, final dr mapData) {
    }
    
    @Override
    public void setClientCompatibilityLevel(final byte compatibilityLevel) {
    }
    
    @Override
    public byte getClientCompatibilityLevel() {
        return 0;
    }
    
    @Override
    public boolean shouldServerShouldBeKilledQuietly() {
        return false;
    }
    
    @Override
    public void disconnectIDMismatch(final MapDifference<Integer, ItemData> s, final ez handler, final cm mgr) {
    }
    
    @Override
    public void addModAsResource(final ModContainer container) {
        final File source = container.getSource();
        try {
            if (source.isDirectory()) {
                this.searchDirForENUSLanguage(source, "");
            }
            else {
                this.searchZipForENUSLanguage(source);
            }
        }
        catch (final IOException ex) {}
    }
    
    private void searchZipForENUSLanguage(final File source) throws IOException {
        final ZipFile zf = new ZipFile(source);
        for (final ZipEntry ze : Collections.list(zf.entries())) {
            final Matcher matcher = FMLServerHandler.assetENUSLang.matcher(ze.getName());
            if (matcher.matches()) {
                FMLLog.fine("Injecting found translation data in zip file %s at %s into language system", source.getName(), ze.getName());
                bv.inject(zf.getInputStream(ze));
            }
        }
        zf.close();
    }
    
    private void searchDirForENUSLanguage(final File source, final String path) throws IOException {
        for (final File file : source.listFiles()) {
            final String currPath = path + file.getName();
            if (file.isDirectory()) {
                this.searchDirForENUSLanguage(file, currPath + '/');
            }
            final Matcher matcher = FMLServerHandler.assetENUSLang.matcher(currPath);
            if (matcher.matches()) {
                FMLLog.fine("Injecting found translation data at %s into language system", currPath);
                bv.inject((InputStream)new FileInputStream(file));
            }
        }
    }
    
    @Override
    public void updateResourcePackList() {
    }
    
    @Override
    public String getCurrentLanguage() {
        return "en_US";
    }
    
    @Override
    public void serverStopped() {
    }
    
    static {
        INSTANCE = new FMLServerHandler();
        assetENUSLang = Pattern.compile("assets/(.*)/lang/en_US.lang");
    }
}
