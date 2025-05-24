// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class ShapedOreRecipe implements aah
{
    private static final int MAX_CRAFT_GRID_WIDTH = 3;
    private static final int MAX_CRAFT_GRID_HEIGHT = 3;
    private ye output;
    private Object[] input;
    private int width;
    private int height;
    private boolean mirrored;
    
    public ShapedOreRecipe(final aqz result, final Object... recipe) {
        this(new ye(result), recipe);
    }
    
    public ShapedOreRecipe(final yc result, final Object... recipe) {
        this(new ye(result), recipe);
    }
    
    public ShapedOreRecipe(final ye result, Object... recipe) {
        this.output = null;
        this.input = null;
        this.width = 0;
        this.height = 0;
        this.mirrored = true;
        this.output = result.m();
        String shape = "";
        int idx = 0;
        if (recipe[idx] instanceof Boolean) {
            this.mirrored = (boolean)recipe[idx];
            if (recipe[idx + 1] instanceof Object[]) {
                recipe = (Object[])recipe[idx + 1];
            }
            else {
                idx = 1;
            }
        }
        if (recipe[idx] instanceof String[]) {
            final String[] arr$;
            final String[] parts = arr$ = (String[])recipe[idx++];
            for (final String s : arr$) {
                this.width = s.length();
                shape += s;
            }
            this.height = parts.length;
        }
        else {
            while (recipe[idx] instanceof String) {
                final String s2 = (String)recipe[idx++];
                shape += s2;
                this.width = s2.length();
                ++this.height;
            }
        }
        if (this.width * this.height != shape.length()) {
            String ret = "Invalid shaped ore recipe: ";
            for (final Object tmp : recipe) {
                ret = ret + tmp + ", ";
            }
            ret += this.output;
            throw new RuntimeException(ret);
        }
        final HashMap<Character, Object> itemMap = new HashMap<Character, Object>();
        while (idx < recipe.length) {
            final Character chr = (Character)recipe[idx];
            final Object in = recipe[idx + 1];
            if (in instanceof ye) {
                itemMap.put(chr, ((ye)in).m());
            }
            else if (in instanceof yc) {
                itemMap.put(chr, new ye((yc)in));
            }
            else if (in instanceof aqz) {
                itemMap.put(chr, new ye((aqz)in, 1, 32767));
            }
            else {
                if (!(in instanceof String)) {
                    String ret2 = "Invalid shaped ore recipe: ";
                    for (final Object tmp2 : recipe) {
                        ret2 = ret2 + tmp2 + ", ";
                    }
                    ret2 += this.output;
                    throw new RuntimeException(ret2);
                }
                itemMap.put(chr, OreDictionary.getOres((String)in));
            }
            idx += 2;
        }
        this.input = new Object[this.width * this.height];
        int x = 0;
        for (final char chr2 : shape.toCharArray()) {
            this.input[x++] = itemMap.get(chr2);
        }
    }
    
    ShapedOreRecipe(final aai recipe, final Map<ye, String> replacements) {
        this.output = null;
        this.input = null;
        this.width = 0;
        this.height = 0;
        this.mirrored = true;
        this.output = recipe.b();
        this.width = recipe.b;
        this.height = recipe.c;
        this.input = new Object[recipe.d.length];
        for (int i = 0; i < this.input.length; ++i) {
            final ye ingred = recipe.d[i];
            if (ingred != null) {
                this.input[i] = recipe.d[i];
                for (final Map.Entry<ye, String> replace : replacements.entrySet()) {
                    if (OreDictionary.itemMatches(replace.getKey(), ingred, true)) {
                        this.input[i] = OreDictionary.getOres(replace.getValue());
                        break;
                    }
                }
            }
        }
    }
    
    public ye a(final vk var1) {
        return this.output.m();
    }
    
    public int a() {
        return this.input.length;
    }
    
    public ye b() {
        return this.output;
    }
    
    public boolean a(final vk inv, final abw world) {
        for (int x = 0; x <= 3 - this.width; ++x) {
            for (int y = 0; y <= 3 - this.height; ++y) {
                if (this.checkMatch(inv, x, y, false)) {
                    return true;
                }
                if (this.mirrored && this.checkMatch(inv, x, y, true)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean checkMatch(final vk inv, final int startX, final int startY, final boolean mirror) {
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                final int subX = x - startX;
                final int subY = y - startY;
                Object target = null;
                if (subX >= 0 && subY >= 0 && subX < this.width && subY < this.height) {
                    if (mirror) {
                        target = this.input[this.width - subX - 1 + subY * this.width];
                    }
                    else {
                        target = this.input[subX + subY * this.width];
                    }
                }
                final ye slot = inv.b(x, y);
                if (target instanceof ye) {
                    if (!this.checkItemEquals((ye)target, slot)) {
                        return false;
                    }
                }
                else if (target instanceof ArrayList) {
                    boolean matched = false;
                    for (final ye item : (ArrayList)target) {
                        matched = (matched || this.checkItemEquals(item, slot));
                    }
                    if (!matched) {
                        return false;
                    }
                }
                else if (target == null && slot != null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean checkItemEquals(final ye target, final ye input) {
        return (input != null || target == null) && (input == null || target != null) && target.d == input.d && (target.k() == 32767 || target.k() == input.k());
    }
    
    public ShapedOreRecipe setMirrored(final boolean mirror) {
        this.mirrored = mirror;
        return this;
    }
    
    public Object[] getInput() {
        return this.input;
    }
}
