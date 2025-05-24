// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.client.FMLFolderResourcePack;
import cpw.mods.fml.common.asm.FMLSanityChecker;
import java.io.File;
import java.security.cert.Certificate;
import cpw.mods.fml.common.registry.ItemData;
import java.util.Set;
import java.util.logging.Level;
import java.util.Map;
import java.util.Iterator;
import cpw.mods.fml.common.registry.GameData;
import com.google.common.eventbus.EventBus;
import java.util.Arrays;

public class FMLDummyContainer extends DummyModContainer implements WorldAccessContainer
{
    public FMLDummyContainer() {
        super(new ModMetadata());
        final ModMetadata meta = this.getMetadata();
        meta.modId = "FML";
        meta.name = "Forge Mod Loader";
        meta.version = Loader.instance().getFMLVersionString();
        meta.credits = "Made possible with help from many people";
        meta.authorList = Arrays.asList("cpw, LexManos");
        meta.description = "The Forge Mod Loader provides the ability for systems to load mods from the file system. It also provides key capabilities for mods to be able to cooperate and provide a good modding environment. The mod loading system is compatible with ModLoader, all your ModLoader mods should work.";
        meta.url = "https://github.com/MinecraftForge/FML/wiki";
        meta.updateUrl = "https://github.com/MinecraftForge/FML/wiki";
        meta.screenshots = new String[0];
        meta.logoFile = "";
    }
    
    @Override
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        return true;
    }
    
    @Override
    public by getDataForWriting(final alq handler, final als info) {
        final by fmlData = new by();
        final cg list = new cg();
        for (final ModContainer mc : Loader.instance().getActiveModList()) {
            final by mod = new by();
            mod.a("ModId", mc.getModId());
            mod.a("ModVersion", mc.getVersion());
            list.a((cl)mod);
        }
        fmlData.a("ModList", (cl)list);
        final cg itemList = new cg();
        GameData.writeItemData(itemList);
        fmlData.a("ModItemData", (cl)itemList);
        return fmlData;
    }
    
    @Override
    public void readData(final alq handler, final als info, final Map<String, cl> propertyMap, final by tag) {
        if (tag.b("ModList")) {
            final cg modList = tag.m("ModList");
            for (int i = 0; i < modList.c(); ++i) {
                final by mod = (by)modList.b(i);
                final String modId = mod.i("ModId");
                final String modVersion = mod.i("ModVersion");
                final ModContainer container = Loader.instance().getIndexedModList().get(modId);
                if (container == null) {
                    FMLLog.log("fml.ModTracker", Level.SEVERE, "This world was saved with mod %s which appears to be missing, things may not work well", modId);
                }
                else if (!modVersion.equals(container.getVersion())) {
                    FMLLog.log("fml.ModTracker", Level.INFO, "This world was saved with mod %s version %s and it is now at version %s, things may not work well", modId, modVersion, container.getVersion());
                }
            }
        }
        if (tag.b("ModItemData")) {
            final cg modList = tag.m("ModItemData");
            final Set<ItemData> worldSaveItems = GameData.buildWorldItemData(modList);
            GameData.validateWorldSave(worldSaveItems);
        }
        else {
            GameData.validateWorldSave(null);
        }
    }
    
    @Override
    public Certificate getSigningCertificate() {
        final Certificate[] certificates = this.getClass().getProtectionDomain().getCodeSource().getCertificates();
        return (certificates != null) ? certificates[0] : null;
    }
    
    @Override
    public File getSource() {
        return FMLSanityChecker.fmlLocation;
    }
    
    @Override
    public Class<?> getCustomResourcePackClass() {
        return (Class<?>)(this.getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class);
    }
}
