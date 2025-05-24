// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.List;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.classloading.FMLForgePlugin;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class EnumHelper
{
    private static Object reflectionFactory;
    private static Method newConstructorAccessor;
    private static Method newInstance;
    private static Method newFieldAccessor;
    private static Method fieldAccessorSet;
    private static boolean isSetup;
    private static Class[][] commonTypes;
    
    public static zj addAction(final String name) {
        return addEnum(zj.class, name, new Object[0]);
    }
    
    public static wj addArmorMaterial(final String name, final int durability, final int[] reductionAmounts, final int enchantability) {
        return addEnum(wj.class, name, durability, reductionAmounts, enchantability);
    }
    
    public static om addArt(final String name, final String tile, final int sizeX, final int sizeY, final int offsetX, final int offsetY) {
        return addEnum(om.class, name, tile, sizeX, sizeY, offsetX, offsetY);
    }
    
    public static oj addCreatureAttribute(final String name) {
        return addEnum(oj.class, name, new Object[0]);
    }
    
    public static oh addCreatureType(final String name, final Class typeClass, final int maxNumber, final akc material, final boolean peaceful) {
        return addEnum(oh.class, name, typeClass, maxNumber, material, peaceful);
    }
    
    public static aim addDoor(final String name) {
        return addEnum(aim.class, name, new Object[0]);
    }
    
    public static aav addEnchantmentType(final String name) {
        return addEnum(aav.class, name, new Object[0]);
    }
    
    public static nr addEntitySize(final String name) {
        return addEnum(nr.class, name, new Object[0]);
    }
    
    public static apx addMobType(final String name) {
        return addEnum(apx.class, name, new Object[0]);
    }
    
    public static atb addMovingObjectType(final String name) {
        if (!EnumHelper.isSetup) {
            setup();
        }
        return addEnum(atb.class, name, new Object[0]);
    }
    
    public static ach addSkyBlock(final String name, final int lightValue) {
        return addEnum(ach.class, name, lightValue);
    }
    
    public static ug addStatus(final String name) {
        return addEnum(ug.class, name, new Object[0]);
    }
    
    public static yd addToolMaterial(final String name, final int harvestLevel, final int maxUses, final float efficiency, final float damage, final int enchantability) {
        return addEnum(yd.class, name, harvestLevel, maxUses, efficiency, damage, enchantability);
    }
    
    private static void setup() {
        if (EnumHelper.isSetup) {
            return;
        }
        try {
            final Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory", (Class<?>[])new Class[0]);
            EnumHelper.reflectionFactory = getReflectionFactory.invoke(null, new Object[0]);
            EnumHelper.newConstructorAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newConstructorAccessor", Constructor.class);
            EnumHelper.newInstance = Class.forName("sun.reflect.ConstructorAccessor").getDeclaredMethod("newInstance", Object[].class);
            EnumHelper.newFieldAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newFieldAccessor", Field.class, Boolean.TYPE);
            EnumHelper.fieldAccessorSet = Class.forName("sun.reflect.FieldAccessor").getDeclaredMethod("set", Object.class, Object.class);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        EnumHelper.isSetup = true;
    }
    
    private static Object getConstructorAccessor(final Class<?> enumClass, final Class<?>[] additionalParameterTypes) throws Exception {
        final Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = Integer.TYPE;
        System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
        return EnumHelper.newConstructorAccessor.invoke(EnumHelper.reflectionFactory, enumClass.getDeclaredConstructor(parameterTypes));
    }
    
    private static <T extends Enum<?>> T makeEnum(final Class<T> enumClass, final String value, final int ordinal, final Class<?>[] additionalTypes, final Object[] additionalValues) throws Exception {
        final Object[] parms = new Object[additionalValues.length + 2];
        parms[0] = value;
        parms[1] = ordinal;
        System.arraycopy(additionalValues, 0, parms, 2, additionalValues.length);
        return enumClass.cast(EnumHelper.newInstance.invoke(getConstructorAccessor(enumClass, additionalTypes), parms));
    }
    
    public static void setFailsafeFieldValue(final Field field, final Object target, final Object value) throws Exception {
        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
        final Object fieldAccessor = EnumHelper.newFieldAccessor.invoke(EnumHelper.reflectionFactory, field, false);
        EnumHelper.fieldAccessorSet.invoke(fieldAccessor, target, value);
    }
    
    private static void blankField(final Class<?> enumClass, final String fieldName) throws Exception {
        for (final Field field : Class.class.getDeclaredFields()) {
            if (field.getName().contains(fieldName)) {
                field.setAccessible(true);
                setFailsafeFieldValue(field, enumClass, null);
                break;
            }
        }
    }
    
    private static void cleanEnumCache(final Class<?> enumClass) throws Exception {
        blankField(enumClass, "enumConstantDirectory");
        blankField(enumClass, "enumConstants");
    }
    
    public static <T extends Enum<?>> T addEnum(final Class<T> enumType, final String enumName, final Object... paramValues) {
        return addEnum(EnumHelper.commonTypes, enumType, enumName, paramValues);
    }
    
    public static <T extends Enum<?>> T addEnum(final Class[][] map, final Class<T> enumType, final String enumName, final Object... paramValues) {
        for (final Class[] lookup : map) {
            if (lookup[0] == enumType) {
                final Class<?>[] paramTypes = new Class[lookup.length - 1];
                if (paramTypes.length > 0) {
                    System.arraycopy(lookup, 1, paramTypes, 0, paramTypes.length);
                }
                return addEnum(enumType, enumName, paramTypes, paramValues);
            }
        }
        return null;
    }
    
    public static <T extends Enum<?>> T addEnum(final Class<T> enumType, final String enumName, final Class<?>[] paramTypes, final Object[] paramValues) {
        if (!EnumHelper.isSetup) {
            setup();
        }
        Field valuesField = null;
        final Field[] arr$;
        final Field[] fields = arr$ = enumType.getDeclaredFields();
        for (final Field field : arr$) {
            final String name = field.getName();
            if (name.equals("$VALUES") || name.equals("ENUM$VALUES")) {
                valuesField = field;
                break;
            }
        }
        final int flags = (FMLForgePlugin.RUNTIME_DEOBF ? 1 : 2) | 0x8 | 0x10 | 0x1000;
        if (valuesField == null) {
            final String valueType = String.format("[L%s;", enumType.getName().replace('.', '/'));
            for (final Field field2 : fields) {
                if ((field2.getModifiers() & flags) == flags && field2.getType().getName().replace('.', '/').equals(valueType)) {
                    valuesField = field2;
                    break;
                }
            }
        }
        if (valuesField == null) {
            FMLLog.severe("Could not find $VALUES field for enum: %s", enumType.getName());
            FMLLog.severe("Runtime Deobf: %s", FMLForgePlugin.RUNTIME_DEOBF);
            FMLLog.severe("Flags: %s", String.format("%16s", Integer.toBinaryString(flags)).replace(' ', '0'));
            FMLLog.severe("Fields:", new Object[0]);
            for (final Field field3 : fields) {
                final String mods = String.format("%16s", Integer.toBinaryString(field3.getModifiers())).replace(' ', '0');
                FMLLog.severe("       %s %s: %s", mods, field3.getName(), field3.getType().getName());
            }
            return null;
        }
        valuesField.setAccessible(true);
        try {
            final T[] previousValues = (T[])valuesField.get(enumType);
            final List<T> values = new ArrayList<T>((Collection<? extends T>)Arrays.asList(previousValues));
            final T newValue = makeEnum(enumType, enumName, values.size(), paramTypes, paramValues);
            values.add(newValue);
            setFailsafeFieldValue(valuesField, null, values.toArray((Enum[])Array.newInstance(enumType, 0)));
            cleanEnumCache(enumType);
            return newValue;
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    static {
        EnumHelper.reflectionFactory = null;
        EnumHelper.newConstructorAccessor = null;
        EnumHelper.newInstance = null;
        EnumHelper.newFieldAccessor = null;
        EnumHelper.fieldAccessorSet = null;
        EnumHelper.isSetup = false;
        EnumHelper.commonTypes = new Class[][] { { zj.class }, { wj.class, Integer.TYPE, int[].class, Integer.TYPE }, { om.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, { oj.class }, { oh.class, Class.class, Integer.TYPE, akc.class, Boolean.TYPE }, { aim.class }, { aav.class }, { nr.class }, { apx.class }, { atb.class }, { ach.class, Integer.TYPE }, { ug.class }, { yd.class, Integer.TYPE, Integer.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE } };
        if (!EnumHelper.isSetup) {
            setup();
        }
    }
}
