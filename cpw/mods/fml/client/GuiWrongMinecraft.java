// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.WrongMinecraftVersionException;

public class GuiWrongMinecraft extends avh
{
    private WrongMinecraftVersionException wrongMC;
    
    public GuiWrongMinecraft(final WrongMinecraftVersionException wrongMC) {
        super((String)null, (String)null);
        this.wrongMC = wrongMC;
    }
    
    public void A_() {
        super.A_();
    }
    
    public void a(final int par1, final int par2, final float par3) {
        this.e();
        int offset = 75;
        this.a(this.o, "Forge Mod Loader has found a problem with your minecraft installation", this.g / 2, offset, 16777215);
        offset += 10;
        this.a(this.o, String.format("The mod listed below does not want to run in Minecraft version %s", Loader.instance().getMinecraftModContainer().getVersion()), this.g / 2, offset, 16777215);
        offset += 5;
        offset += 10;
        this.a(this.o, String.format("%s (%s) wants Minecraft %s", this.wrongMC.mod.getName(), this.wrongMC.mod.getModId(), this.wrongMC.mod.acceptableMinecraftVersionRange()), this.g / 2, offset, 15658734);
        offset += 20;
        this.a(this.o, "The file 'ForgeModLoader-client-0.log' contains more information", this.g / 2, offset, 16777215);
    }
}
