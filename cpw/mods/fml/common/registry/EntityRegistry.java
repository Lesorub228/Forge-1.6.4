// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.registry;

import cpw.mods.fml.common.network.EntitySpawnPacket;
import com.google.common.base.Function;
import java.util.List;
import com.google.common.primitives.UnsignedBytes;
import cpw.mods.fml.common.Loader;
import java.util.logging.Level;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.FMLCommonHandler;
import java.util.Iterator;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import java.util.Map;
import cpw.mods.fml.common.ModContainer;
import com.google.common.collect.ListMultimap;
import java.util.BitSet;

public class EntityRegistry
{
    private static final EntityRegistry INSTANCE;
    private BitSet availableIndicies;
    private ListMultimap<ModContainer, EntityRegistration> entityRegistrations;
    private Map<String, ModContainer> entityNames;
    private BiMap<Class<? extends nn>, EntityRegistration> entityClassRegistrations;
    
    public static EntityRegistry instance() {
        return EntityRegistry.INSTANCE;
    }
    
    private EntityRegistry() {
        this.entityRegistrations = (ListMultimap<ModContainer, EntityRegistration>)ArrayListMultimap.create();
        this.entityNames = Maps.newHashMap();
        this.entityClassRegistrations = (BiMap<Class<? extends nn>, EntityRegistration>)HashBiMap.create();
        (this.availableIndicies = new BitSet(256)).set(1, 255);
        for (final Object id : nt.d.keySet()) {
            this.availableIndicies.clear((int)id);
        }
    }
    
    public static void registerModEntity(final Class<? extends nn> entityClass, final String entityName, final int id, final Object mod, final int trackingRange, final int updateFrequency, final boolean sendsVelocityUpdates) {
        instance().doModEntityRegistration(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
    }
    
    private void doModEntityRegistration(final Class<? extends nn> entityClass, final String entityName, final int id, final Object mod, final int trackingRange, final int updateFrequency, final boolean sendsVelocityUpdates) {
        final ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        final EntityRegistration er = new EntityRegistration(mc, entityClass, entityName, id, trackingRange, updateFrequency, sendsVelocityUpdates);
        try {
            this.entityClassRegistrations.put((Object)entityClass, (Object)er);
            this.entityNames.put(entityName, mc);
            if (!nt.c.containsKey(entityClass)) {
                final String entityModName = String.format("%s.%s", mc.getModId(), entityName);
                nt.c.put(entityClass, entityModName);
                nt.b.put(entityModName, entityClass);
                FMLLog.finest("Automatically registered mod %s entity %s as %s", mc.getModId(), entityName, entityModName);
            }
            else {
                FMLLog.fine("Skipping automatic mod %s entity registration for already registered class %s", mc.getModId(), entityClass.getName());
            }
        }
        catch (final IllegalArgumentException e) {
            FMLLog.log(Level.WARNING, e, "The mod %s tried to register the entity (name,class) (%s,%s) one or both of which are already registered", mc.getModId(), entityName, entityClass.getName());
            return;
        }
        this.entityRegistrations.put((Object)mc, (Object)er);
    }
    
    public static void registerGlobalEntityID(final Class<? extends nn> entityClass, final String entityName, int id) {
        if (nt.c.containsKey(entityClass)) {
            final ModContainer activeModContainer = Loader.instance().activeModContainer();
            String modId = "unknown";
            if (activeModContainer != null) {
                modId = activeModContainer.getModId();
            }
            else {
                FMLLog.severe("There is a rogue mod failing to register entities from outside the context of mod loading. This is incredibly dangerous and should be stopped.", new Object[0]);
            }
            FMLLog.warning("The mod %s tried to register the entity class %s which was already registered - if you wish to override default naming for FML mod entities, register it here first", modId, entityClass);
            return;
        }
        id = instance().validateAndClaimId(id);
        nt.a((Class)entityClass, entityName, id);
    }
    
    private int validateAndClaimId(final int id) {
        int realId = id;
        if (id < -128) {
            FMLLog.warning("Compensating for modloader out of range compensation by mod : entityId %d for mod %s is now %d", id, Loader.instance().activeModContainer().getModId(), realId);
            realId += 3000;
        }
        if (realId < 0) {
            realId += 127;
        }
        try {
            UnsignedBytes.checkedCast((long)realId);
        }
        catch (final IllegalArgumentException e) {
            FMLLog.log(Level.SEVERE, "The entity ID %d for mod %s is not an unsigned byte and may not work", id, Loader.instance().activeModContainer().getModId());
        }
        if (!this.availableIndicies.get(realId)) {
            FMLLog.severe("The mod %s has attempted to register an entity ID %d which is already reserved. This could cause severe problems", Loader.instance().activeModContainer().getModId(), id);
        }
        this.availableIndicies.clear(realId);
        return realId;
    }
    
    public static void registerGlobalEntityID(final Class<? extends nn> entityClass, final String entityName, final int id, final int backgroundEggColour, final int foregroundEggColour) {
        if (nt.c.containsKey(entityClass)) {
            final ModContainer activeModContainer = Loader.instance().activeModContainer();
            String modId = "unknown";
            if (activeModContainer != null) {
                modId = activeModContainer.getModId();
            }
            else {
                FMLLog.severe("There is a rogue mod failing to register entities from outside the context of mod loading. This is incredibly dangerous and should be stopped.", new Object[0]);
            }
            FMLLog.warning("The mod %s tried to register the entity class %s which was already registered - if you wish to override default naming for FML mod entities, register it here first", modId, entityClass);
            return;
        }
        instance().validateAndClaimId(id);
        nt.a((Class)entityClass, entityName, id, backgroundEggColour, foregroundEggColour);
    }
    
    public static void addSpawn(final Class<? extends og> entityClass, final int weightedProb, final int min, final int max, final oh typeOfCreature, final acq... biomes) {
        for (final acq biome : biomes) {
            final List<acr> spawns = biome.a(typeOfCreature);
            for (final acr entry : spawns) {
                if (entry.b == entityClass) {
                    entry.a = weightedProb;
                    entry.c = min;
                    entry.d = max;
                    break;
                }
            }
            spawns.add(new acr((Class)entityClass, weightedProb, min, max));
        }
    }
    
    public static void addSpawn(final String entityName, final int weightedProb, final int min, final int max, final oh spawnList, final acq... biomes) {
        final Class<? extends nn> entityClazz = nt.b.get(entityName);
        if (og.class.isAssignableFrom(entityClazz)) {
            addSpawn((Class<? extends og>)entityClazz, weightedProb, min, max, spawnList, biomes);
        }
    }
    
    public static void removeSpawn(final Class<? extends og> entityClass, final oh typeOfCreature, final acq... biomes) {
        for (final acq biome : biomes) {
            final Iterator<acr> spawns = biome.a(typeOfCreature).iterator();
            while (spawns.hasNext()) {
                final acr entry = spawns.next();
                if (entry.b == entityClass) {
                    spawns.remove();
                }
            }
        }
    }
    
    public static void removeSpawn(final String entityName, final oh spawnList, final acq... biomes) {
        final Class<? extends nn> entityClazz = nt.b.get(entityName);
        if (og.class.isAssignableFrom(entityClazz)) {
            removeSpawn((Class<? extends og>)entityClazz, spawnList, biomes);
        }
    }
    
    public static int findGlobalUniqueEntityId() {
        final int res = instance().availableIndicies.nextSetBit(0);
        if (res < 0) {
            throw new RuntimeException("No more entity indicies left");
        }
        return res;
    }
    
    public EntityRegistration lookupModSpawn(final Class<? extends nn> clazz, boolean keepLooking) {
        Class<?> localClazz = clazz;
        do {
            final EntityRegistration er = (EntityRegistration)this.entityClassRegistrations.get((Object)localClazz);
            if (er != null) {
                return er;
            }
            localClazz = localClazz.getSuperclass();
            keepLooking = !Object.class.equals(localClazz);
        } while (keepLooking);
        return null;
    }
    
    public EntityRegistration lookupModSpawn(final ModContainer mc, final int modEntityId) {
        for (final EntityRegistration er : this.entityRegistrations.get((Object)mc)) {
            if (er.getModEntityId() == modEntityId) {
                return er;
            }
        }
        return null;
    }
    
    public boolean tryTrackingEntity(final jm entityTracker, final nn entity) {
        final EntityRegistration er = this.lookupModSpawn(entity.getClass(), true);
        if (er != null) {
            entityTracker.a(entity, er.getTrackingRange(), er.getUpdateFrequency(), er.sendsVelocityUpdates());
            return true;
        }
        return false;
    }
    
    @Deprecated
    public static EntityRegistration registerModLoaderEntity(final Object mod, final Class<? extends nn> entityClass, final int entityTypeId, final int updateRange, final int updateInterval, final boolean sendVelocityInfo) {
        final String entityName = nt.c.get(entityClass);
        if (entityName == null) {
            throw new IllegalArgumentException(String.format("The ModLoader mod %s has tried to register an entity tracker for a non-existent entity type %s", Loader.instance().activeModContainer().getModId(), entityClass.getCanonicalName()));
        }
        instance().doModEntityRegistration(entityClass, entityName, entityTypeId, mod, updateRange, updateInterval, sendVelocityInfo);
        return (EntityRegistration)instance().entityClassRegistrations.get((Object)entityClass);
    }
    
    static {
        INSTANCE = new EntityRegistry();
    }
    
    public class EntityRegistration
    {
        private Class<? extends nn> entityClass;
        private ModContainer container;
        private String entityName;
        private int modId;
        private int trackingRange;
        private int updateFrequency;
        private boolean sendsVelocityUpdates;
        private Function<EntitySpawnPacket, nn> customSpawnCallback;
        private boolean usesVanillaSpawning;
        
        public EntityRegistration(final ModContainer mc, final Class<? extends nn> entityClass, final String entityName, final int id, final int trackingRange, final int updateFrequency, final boolean sendsVelocityUpdates) {
            this.container = mc;
            this.entityClass = entityClass;
            this.entityName = entityName;
            this.modId = id;
            this.trackingRange = trackingRange;
            this.updateFrequency = updateFrequency;
            this.sendsVelocityUpdates = sendsVelocityUpdates;
        }
        
        public Class<? extends nn> getEntityClass() {
            return this.entityClass;
        }
        
        public ModContainer getContainer() {
            return this.container;
        }
        
        public String getEntityName() {
            return this.entityName;
        }
        
        public int getModEntityId() {
            return this.modId;
        }
        
        public int getTrackingRange() {
            return this.trackingRange;
        }
        
        public int getUpdateFrequency() {
            return this.updateFrequency;
        }
        
        public boolean sendsVelocityUpdates() {
            return this.sendsVelocityUpdates;
        }
        
        public boolean usesVanillaSpawning() {
            return this.usesVanillaSpawning;
        }
        
        public boolean hasCustomSpawning() {
            return this.customSpawnCallback != null;
        }
        
        public nn doCustomSpawning(final EntitySpawnPacket packet) throws Exception {
            return (nn)this.customSpawnCallback.apply((Object)packet);
        }
        
        public void setCustomSpawning(final Function<EntitySpawnPacket, nn> callable, final boolean usesVanillaSpawning) {
            this.customSpawnCallback = callable;
            this.usesVanillaSpawning = usesVanillaSpawning;
        }
    }
}
