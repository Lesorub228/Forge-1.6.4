// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import com.google.common.base.Strings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.discovery.ASMDataTable;

public class ProxyInjector
{
    public static void inject(final ModContainer mod, final ASMDataTable data, final Side side, final ILanguageAdapter languageAdapter) {
        FMLLog.fine("Attempting to inject @SidedProxy classes into %s", mod.getModId());
        final Set<ASMDataTable.ASMData> targets = data.getAnnotationsFor(mod).get((Object)SidedProxy.class.getName());
        final ClassLoader mcl = Loader.instance().getModClassLoader();
        for (final ASMDataTable.ASMData targ : targets) {
            try {
                final Class<?> proxyTarget = Class.forName(targ.getClassName(), true, mcl);
                final Field target = proxyTarget.getDeclaredField(targ.getObjectName());
                if (target == null) {
                    FMLLog.severe("Attempted to load a proxy type into %s.%s but the field was not found", targ.getClassName(), targ.getObjectName());
                    throw new LoaderException();
                }
                final SidedProxy annotation = target.getAnnotation(SidedProxy.class);
                if (!Strings.isNullOrEmpty(annotation.modId()) && !annotation.modId().equals(mod.getModId())) {
                    FMLLog.fine("Skipping proxy injection for %s.%s since it is not for mod %s", targ.getClassName(), targ.getObjectName(), mod.getModId());
                }
                else {
                    final String targetType = side.isClient() ? annotation.clientSide() : annotation.serverSide();
                    final Object proxy = Class.forName(targetType, true, mcl).newInstance();
                    if (languageAdapter.supportsStatics() && (target.getModifiers() & 0x8) == 0x0) {
                        FMLLog.severe("Attempted to load a proxy type %s into %s.%s, but the field is not static", targetType, targ.getClassName(), targ.getObjectName());
                        throw new LoaderException();
                    }
                    if (!target.getType().isAssignableFrom(proxy.getClass())) {
                        FMLLog.severe("Attempted to load a proxy type %s into %s.%s, but the types don't match", targetType, targ.getClassName(), targ.getObjectName());
                        throw new LoaderException();
                    }
                    languageAdapter.setProxy(target, proxyTarget, proxy);
                }
            }
            catch (final Exception e) {
                FMLLog.log(Level.SEVERE, e, "An error occured trying to load a proxy into %s.%s", targ.getAnnotationInfo(), targ.getClassName(), targ.getObjectName());
                throw new LoaderException(e);
            }
        }
        languageAdapter.setInternalProxies(mod, side, mcl);
    }
}
