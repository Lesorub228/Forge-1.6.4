// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.versioning;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.List;
import java.math.BigInteger;
import java.util.Stack;
import java.util.Locale;

public class ComparableVersion implements Comparable<ComparableVersion>
{
    private String value;
    private String canonical;
    private ListItem items;
    
    public ComparableVersion(final String version) {
        this.parseVersion(version);
    }
    
    public final void parseVersion(String version) {
        this.value = version;
        this.items = new ListItem();
        version = version.toLowerCase(Locale.ENGLISH);
        ListItem list = this.items;
        final Stack<Item> stack = new Stack<Item>();
        stack.push(list);
        boolean isDigit = false;
        int startIndex = 0;
        for (int i = 0; i < version.length(); ++i) {
            final char c = version.charAt(i);
            if (c == '.') {
                if (i == startIndex) {
                    ((ArrayList<IntegerItem>)list).add(IntegerItem.ZERO);
                }
                else {
                    list.add(parseItem(isDigit, version.substring(startIndex, i)));
                }
                startIndex = i + 1;
            }
            else if (c == '-') {
                if (i == startIndex) {
                    ((ArrayList<IntegerItem>)list).add(IntegerItem.ZERO);
                }
                else {
                    list.add(parseItem(isDigit, version.substring(startIndex, i)));
                }
                startIndex = i + 1;
                if (isDigit) {
                    list.normalize();
                    if (i + 1 < version.length() && Character.isDigit(version.charAt(i + 1))) {
                        ((ArrayList<ListItem>)list).add(list = new ListItem());
                        stack.push(list);
                    }
                }
            }
            else if (Character.isDigit(c)) {
                if (!isDigit && i > startIndex) {
                    ((ArrayList<StringItem>)list).add(new StringItem(version.substring(startIndex, i), true));
                    startIndex = i;
                }
                isDigit = true;
            }
            else {
                if (isDigit && i > startIndex) {
                    list.add(parseItem(true, version.substring(startIndex, i)));
                    startIndex = i;
                }
                isDigit = false;
            }
        }
        if (version.length() > startIndex) {
            list.add(parseItem(isDigit, version.substring(startIndex)));
        }
        while (!stack.isEmpty()) {
            list = stack.pop();
            list.normalize();
        }
        this.canonical = this.items.toString();
    }
    
    private static Item parseItem(final boolean isDigit, final String buf) {
        return isDigit ? new IntegerItem(buf) : new StringItem(buf, false);
    }
    
    @Override
    public int compareTo(final ComparableVersion o) {
        return this.items.compareTo(o.items);
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ComparableVersion && this.canonical.equals(((ComparableVersion)o).canonical);
    }
    
    @Override
    public int hashCode() {
        return this.canonical.hashCode();
    }
    
    private static class IntegerItem implements Item
    {
        private static final BigInteger BigInteger_ZERO;
        private final BigInteger value;
        public static final IntegerItem ZERO;
        
        private IntegerItem() {
            this.value = IntegerItem.BigInteger_ZERO;
        }
        
        public IntegerItem(final String str) {
            this.value = new BigInteger(str);
        }
        
        @Override
        public int getType() {
            return 0;
        }
        
        @Override
        public boolean isNull() {
            return IntegerItem.BigInteger_ZERO.equals(this.value);
        }
        
        @Override
        public int compareTo(final Item item) {
            if (item == null) {
                return IntegerItem.BigInteger_ZERO.equals(this.value) ? 0 : 1;
            }
            switch (item.getType()) {
                case 0: {
                    return this.value.compareTo(((IntegerItem)item).value);
                }
                case 1: {
                    return 1;
                }
                case 2: {
                    return 1;
                }
                default: {
                    throw new RuntimeException("invalid item: " + item.getClass());
                }
            }
        }
        
        @Override
        public String toString() {
            return this.value.toString();
        }
        
        static {
            BigInteger_ZERO = new BigInteger("0");
            ZERO = new IntegerItem();
        }
    }
    
    private static class StringItem implements Item
    {
        private static final String[] QUALIFIERS;
        private static final List<String> _QUALIFIERS;
        private static final Properties ALIASES;
        private static final String RELEASE_VERSION_INDEX;
        private String value;
        
        public StringItem(String value, final boolean followedByDigit) {
            if (followedByDigit && value.length() == 1) {
                switch (value.charAt(0)) {
                    case 'a': {
                        value = "alpha";
                        break;
                    }
                    case 'b': {
                        value = "beta";
                        break;
                    }
                    case 'm': {
                        value = "milestone";
                        break;
                    }
                }
            }
            this.value = StringItem.ALIASES.getProperty(value, value);
        }
        
        @Override
        public int getType() {
            return 1;
        }
        
        @Override
        public boolean isNull() {
            return comparableQualifier(this.value).compareTo(StringItem.RELEASE_VERSION_INDEX) == 0;
        }
        
        public static String comparableQualifier(final String qualifier) {
            final int i = StringItem._QUALIFIERS.indexOf(qualifier);
            return (i == -1) ? (StringItem._QUALIFIERS.size() + "-" + qualifier) : String.valueOf(i);
        }
        
        @Override
        public int compareTo(final Item item) {
            if (item == null) {
                return comparableQualifier(this.value).compareTo(StringItem.RELEASE_VERSION_INDEX);
            }
            switch (item.getType()) {
                case 0: {
                    return -1;
                }
                case 1: {
                    return comparableQualifier(this.value).compareTo(comparableQualifier(((StringItem)item).value));
                }
                case 2: {
                    return -1;
                }
                default: {
                    throw new RuntimeException("invalid item: " + item.getClass());
                }
            }
        }
        
        @Override
        public String toString() {
            return this.value;
        }
        
        static {
            QUALIFIERS = new String[] { "alpha", "beta", "milestone", "rc", "snapshot", "", "sp" };
            _QUALIFIERS = Arrays.asList(StringItem.QUALIFIERS);
            ((Hashtable<String, String>)(ALIASES = new Properties())).put("ga", "");
            ((Hashtable<String, String>)StringItem.ALIASES).put("final", "");
            ((Hashtable<String, String>)StringItem.ALIASES).put("cr", "rc");
            RELEASE_VERSION_INDEX = String.valueOf(StringItem._QUALIFIERS.indexOf(""));
        }
    }
    
    private static class ListItem extends ArrayList<Item> implements Item
    {
        @Override
        public int getType() {
            return 2;
        }
        
        @Override
        public boolean isNull() {
            return this.size() == 0;
        }
        
        void normalize() {
            final ListIterator<Item> iterator = this.listIterator(this.size());
            while (iterator.hasPrevious()) {
                final Item item = iterator.previous();
                if (!item.isNull()) {
                    break;
                }
                iterator.remove();
            }
        }
        
        @Override
        public int compareTo(final Item item) {
            if (item == null) {
                if (this.size() == 0) {
                    return 0;
                }
                final Item first = this.get(0);
                return first.compareTo(null);
            }
            else {
                switch (item.getType()) {
                    case 0: {
                        return -1;
                    }
                    case 1: {
                        return 1;
                    }
                    case 2: {
                        final Iterator<Item> left = this.iterator();
                        final Iterator<Item> right = ((ListItem)item).iterator();
                        while (left.hasNext() || right.hasNext()) {
                            final Item l = left.hasNext() ? left.next() : null;
                            final Item r = right.hasNext() ? right.next() : null;
                            final int result = (l == null) ? (-1 * r.compareTo(l)) : l.compareTo(r);
                            if (result != 0) {
                                return result;
                            }
                        }
                        return 0;
                    }
                    default: {
                        throw new RuntimeException("invalid item: " + item.getClass());
                    }
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder("(");
            final Iterator<Item> iter = this.iterator();
            while (iter.hasNext()) {
                buffer.append(iter.next());
                if (iter.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append(')');
            return buffer.toString();
        }
    }
    
    private interface Item
    {
        public static final int INTEGER_ITEM = 0;
        public static final int STRING_ITEM = 1;
        public static final int LIST_ITEM = 2;
        
        int compareTo(final Item p0);
        
        int getType();
        
        boolean isNull();
    }
}
