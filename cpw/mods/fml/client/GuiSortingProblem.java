// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import java.util.Iterator;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.toposort.ModSortingException;

public class GuiSortingProblem extends awe
{
    private ModSortingException modSorting;
    private ModSortingException.SortingExceptionData<ModContainer> failedList;
    
    public GuiSortingProblem(final ModSortingException modSorting) {
        this.modSorting = modSorting;
        this.failedList = modSorting.getExceptionData();
    }
    
    public void A_() {
        super.A_();
    }
    
    public void a(final int par1, final int par2, final float par3) {
        this.e();
        int offset = Math.max(85 - (this.failedList.getVisitedNodes().size() + 3) * 10, 10);
        this.a(this.o, "Forge Mod Loader has found a problem with your minecraft installation", this.g / 2, offset, 16777215);
        offset += 10;
        this.a(this.o, "A mod sorting cycle was detected and loading cannot continue", this.g / 2, offset, 16777215);
        offset += 10;
        this.a(this.o, String.format("The first mod in the cycle is %s", this.failedList.getFirstBadNode()), this.g / 2, offset, 16777215);
        offset += 10;
        this.a(this.o, "The remainder of the cycle involves these mods", this.g / 2, offset, 16777215);
        offset += 5;
        for (final ModContainer mc : this.failedList.getVisitedNodes()) {
            offset += 10;
            this.a(this.o, String.format("%s : before: %s, after: %s", mc.toString(), mc.getDependants(), mc.getDependencies()), this.g / 2, offset, 15658734);
        }
        offset += 20;
        this.a(this.o, "The file 'ForgeModLoader-client-0.log' contains more information", this.g / 2, offset, 16777215);
    }
}
