import net.minecraft.server.MinecraftServer;
import java.util.Random;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Map;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.modloader.BaseModProxy;

// 
// Decompiled by Procyon v0.6.0
// 

@Deprecated
public abstract class BaseMod implements BaseModProxy
{
    @Deprecated
    @Override
    public final boolean doTickInGame(final TickType tick, final boolean tickEnd, final Object... data) {
        final atv mc = FMLClientHandler.instance().getClient();
        final boolean hasWorld = mc.f != null;
        return !tickEnd || (tick != TickType.RENDER && tick != TickType.CLIENT) || !hasWorld || this.onTickInGame((float)data[0], mc);
    }
    
    @Deprecated
    @Override
    public final boolean doTickInGUI(final TickType tick, final boolean tickEnd, final Object... data) {
        final atv mc = FMLClientHandler.instance().getClient();
        final boolean hasWorld = mc.f != null;
        return !tickEnd || (tick != TickType.RENDER && (tick != TickType.CLIENT || !hasWorld)) || this.onTickInGUI((float)data[0], mc, mc.n);
    }
    
    @Deprecated
    @Override
    public int addFuel(final int id, final int metadata) {
        return 0;
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public void addRenderer(final Map<Class<? extends nn>, bgm> renderers) {
    }
    
    @Deprecated
    @Override
    public void generateNether(final abw world, final Random random, final int chunkX, final int chunkZ) {
    }
    
    @Deprecated
    @Override
    public void generateSurface(final abw world, final Random random, final int chunkX, final int chunkZ) {
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public awy getContainerGUI(final bdi player, final int containerID, final int x, final int y, final int z) {
        return null;
    }
    
    @Deprecated
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
    
    @Deprecated
    @Override
    public String getPriorities() {
        return "";
    }
    
    @Deprecated
    @Override
    public abstract String getVersion();
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public void keyboardEvent(final ats event) {
    }
    
    @Deprecated
    @Override
    public abstract void load();
    
    @Deprecated
    @Override
    public void modsLoaded() {
    }
    
    @Deprecated
    @Override
    public void onItemPickup(final uf player, final ye item) {
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean onTickInGame(final float time, final atv minecraftInstance) {
        return false;
    }
    
    @Deprecated
    public boolean onTickInGame(final MinecraftServer minecraftServer) {
        return false;
    }
    
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean onTickInGUI(final float tick, final atv game, final awe gui) {
        return false;
    }
    
    @Deprecated
    @Override
    public void clientChat(final String text) {
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public void clientConnect(final bcw handler) {
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public void clientDisconnect(final bcw handler) {
    }
    
    @Deprecated
    @Override
    public void receiveCustomPacket(final ea packet) {
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public void registerAnimation(final atv game) {
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public void renderInvBlock(final bfr renderer, final aqz block, final int metadata, final int modelID) {
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public boolean renderWorldBlock(final bfr renderer, final acf world, final int x, final int y, final int z, final aqz block, final int modelID) {
        return false;
    }
    
    @Deprecated
    @Override
    public void serverConnect(final ez handler) {
    }
    
    @Deprecated
    @Override
    public void serverCustomPayload(final ka handler, final ea packet) {
    }
    
    @Deprecated
    @Override
    public void serverDisconnect() {
    }
    
    @Deprecated
    @Override
    public void takenFromCrafting(final uf player, final ye item, final mo matrix) {
    }
    
    @Deprecated
    @Override
    public void takenFromFurnace(final uf player, final ye item) {
    }
    
    @Override
    public String toString() {
        return this.getName() + " " + this.getVersion();
    }
    
    @Deprecated
    @Override
    public void serverChat(final ka source, final String message) {
    }
    
    @Deprecated
    @Override
    public void onClientLogin(final uf player) {
    }
    
    @Deprecated
    @Override
    public void onClientLogout(final cm mgr) {
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public nn spawnEntity(final int entityId, final abw world, final double scaledX, final double scaledY, final double scaledZ) {
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public void clientCustomPayload(final bcw handler, final ea packet) {
    }
}
