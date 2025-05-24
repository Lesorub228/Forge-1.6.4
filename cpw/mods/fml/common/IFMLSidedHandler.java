// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import cpw.mods.fml.common.registry.ItemData;
import com.google.common.collect.MapDifference;
import cpw.mods.fml.common.network.ModMissingPacket;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import java.util.List;

public interface IFMLSidedHandler
{
    List<String> getAdditionalBrandingInformation();
    
    Side getSide();
    
    void haltGame(final String p0, final Throwable p1);
    
    void showGuiScreen(final Object p0);
    
    nn spawnEntityIntoClientWorld(final EntityRegistry.EntityRegistration p0, final EntitySpawnPacket p1);
    
    void adjustEntityLocationOnClient(final EntitySpawnAdjustmentPacket p0);
    
    void beginServerLoading(final MinecraftServer p0);
    
    void finishServerLoading();
    
    MinecraftServer getServer();
    
    void sendPacket(final ey p0);
    
    void displayMissingMods(final ModMissingPacket p0);
    
    void handleTinyPacket(final ez p0, final dr p1);
    
    void setClientCompatibilityLevel(final byte p0);
    
    byte getClientCompatibilityLevel();
    
    boolean shouldServerShouldBeKilledQuietly();
    
    void disconnectIDMismatch(final MapDifference<Integer, ItemData> p0, final ez p1, final cm p2);
    
    void addModAsResource(final ModContainer p0);
    
    void updateResourcePackList();
    
    String getCurrentLanguage();
    
    void serverStopped();
}
