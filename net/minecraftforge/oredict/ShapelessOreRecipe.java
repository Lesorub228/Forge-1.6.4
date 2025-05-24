// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.oredict;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

public class ShapelessOreRecipe implements aah
{
    private ye output;
    private ArrayList input;
    
    public ShapelessOreRecipe(final aqz result, final Object... recipe) {
        this(new ye(result), recipe);
    }
    
    public ShapelessOreRecipe(final yc result, final Object... recipe) {
        this(new ye(result), recipe);
    }
    
    public ShapelessOreRecipe(final ye result, final Object... recipe) {
        this.output = null;
        this.input = new ArrayList();
        this.output = result.m();
        for (final Object in : recipe) {
            if (in instanceof ye) {
                this.input.add(((ye)in).m());
            }
            else if (in instanceof yc) {
                this.input.add(new ye((yc)in));
            }
            else if (in instanceof aqz) {
                this.input.add(new ye((aqz)in));
            }
            else {
                if (!(in instanceof String)) {
                    String ret = "Invalid shapeless ore recipe: ";
                    for (final Object tmp : recipe) {
                        ret = ret + tmp + ", ";
                    }
                    ret += this.output;
                    throw new RuntimeException(ret);
                }
                this.input.add(OreDictionary.getOres((String)in));
            }
        }
    }
    
    ShapelessOreRecipe(final aaj recipe, final Map<ye, String> replacements) {
        this.output = null;
        this.input = new ArrayList();
        this.output = recipe.b();
        for (Object finalObj : recipe.b) {
            final ye ingred = (ye)finalObj;
            for (final Map.Entry<ye, String> replace : replacements.entrySet()) {
                if (OreDictionary.itemMatches(replace.getKey(), ingred, false)) {
                    finalObj = OreDictionary.getOres(replace.getValue());
                    break;
                }
            }
            this.input.add(finalObj);
        }
    }
    
    public int a() {
        return this.input.size();
    }
    
    public ye b() {
        return this.output;
    }
    
    public ye a(final vk var1) {
        return this.output.m();
    }
    
    public boolean a(final vk var1, final abw world) {
        final ArrayList required = new ArrayList(this.input);
        for (int x = 0; x < var1.j_(); ++x) {
            final ye slot = var1.a(x);
            if (slot != null) {
                boolean inRecipe = false;
                final Iterator req = required.iterator();
                while (req.hasNext()) {
                    boolean match = false;
                    final Object next = req.next();
                    if (next instanceof ye) {
                        match = this.checkItemEquals((ye)next, slot);
                    }
                    else if (next instanceof ArrayList) {
                        for (final ye item : (ArrayList)next) {
                            match = (match || this.checkItemEquals(item, slot));
                        }
                    }
                    if (match) {
                        inRecipe = true;
                        required.remove(next);
                        break;
                    }
                }
                if (!inRecipe) {
                    return false;
                }
            }
        }
        return required.isEmpty();
    }
    
    private boolean checkItemEquals(final ye target, final ye input) {
        return target.d == input.d && (target.k() == 32767 || target.k() == input.k());
    }
    
    public ArrayList getInput() {
        return this.input;
    }
}
