package ru.albemuth.util;

import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import ru.albemuth.util.deprecated.CacheOld;
import ru.albemuth.util.deprecated.GenericInvocationHandler;

/**
 * Created by IntelliJ IDEA.
 * User: -
 * Date: 07.08.2007
 * Time: 1:47:20
 */
public class TestProxy extends TestCase {

    protected HashMap<Integer, Entity> storage;
    protected EntityCacheOld cache;

    public void testStress() {
        try {
            storage = new HashMap<Integer, Entity>();
            cache = new EntityCacheOld("entity cache", 4000, 24*60*60*1000, CacheOld.REPLACE_WHEN_EXHAUSTED);

            for (int i = 0; i < 10000; i++) {
                Entity entity = getEntity(i);
                if (entity == null) {
                    System.out.println("Entity is null!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void test() {
        try {
            storage = new HashMap<Integer, Entity>();
            cache = new EntityCacheOld("entity cache", 2, 1000, CacheOld.REPLACE_WHEN_EXHAUSTED);

            Entity entity1 = cache.getEntityProxy(2);
            Entity entity2 = cache.getEntityProxy(2);
            assertFalse(entity1 == entity2);
            assertTrue(entity1.equals(entity2));
            assertEquals("2", entity1.getValue());
            assertEquals("2", entity2.getValue());
            entity1.setValue("2-1");
            assertEquals("2-1", entity2.getValue());
            storage.get(2).setValue("2-2");
            Thread.sleep(2000);
            assertEquals("2-2", entity1.getValue());
            assertEquals("2-2", entity2.getValue());
            storage.get(2).setValue("2-3");
            assertEquals("2-2", entity1.getValue());
            assertEquals("2-2", entity2.getValue());
            List<Integer> ids = new ArrayList<Integer>();
            ids.add(3);
            ids.add(4);
            ids.add(5);
            List<Entity> loadedEntities = loadEntities(ids);
            List<Entity> entities = new ArrayList<Entity>(loadedEntities.size());
            for (Entity e: loadedEntities) {
                entities.add(cache.getEntityProxy(e));
            }
            assertEquals(3, entities.size());
            assertEquals(2, cache.getSize());
            assertEquals("2-3", entity1.getValue());
            assertEquals("2-3", entity2.getValue());
            entity1.setValue("2-4");
            assertEquals("2-4", entity2.getValue());

            long t1, t2;
            Entity e;
            int count = 10000000;
            e = storage.get(5);
            t1 = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                e.getValue();
            }
            t2 = System.currentTimeMillis();
            System.out.println((t2 - t1) + ", " + (t2 - t1)/(double)count);

            e = cache.getEntityProxy(loadEntity(5));
            t1 = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                e.getValue();
            }
            t2 = System.currentTimeMillis();
            System.out.println((t2 - t1) + ", " + (t2 - t1)/(double)count);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
    }

    public Entity getEntity(Integer id) {
System.out.println(cache.getSize());
        return cache.getEntityProxy(loadEntity(id));
    }

    protected synchronized final Entity loadEntity(Integer id) {
        Entity entity = storage.get(id);
        if (entity == null) {
            entity =  new EntityImpl();
            entity.setId(id);
            entity.setValue(id.toString());
            storage.put(id, entity);
        }
        return entity.clone();
    }

    protected List<Entity> loadEntities(List<Integer> ids) {
        List<Entity> entities = new ArrayList<Entity>(ids.size());
        for (Integer id: ids) {
            entities.add(loadEntity(id));
        }
        return entities;
    }

    class EntityCacheOld extends CacheOld<Entity> {

        public final ClassLoader ENTITY_CLASS_LOADER = Entity.class.getClassLoader();
        public final Class[] ENTITY_CLASS = {Entity.class};

        public EntityCacheOld(String name, int capacity, long ttl, int whenExhaustedAction) {
            super(name, capacity, ttl, whenExhaustedAction);
        }

        protected synchronized CacheItem<Entity> putEntity(Object key, Entity entity) {
            put(key, entity);
            return storage.get(key);
        }

        protected Entity getEntityProxy(Entity entity) {
            Entity ret = null;
            if (entity != null) {
                ret = getEntityProxy(new EntityHandler(entity));
            }
            return ret;
        }

        protected Entity getEntityProxy(Integer entityId) {
            return getEntityProxy(new EntityHandler(entityId));
        }

        protected Entity getEntityProxy(EntityHandler handler) {
            return (Entity)Proxy.newProxyInstance(ENTITY_CLASS_LOADER, ENTITY_CLASS, handler);
        }

        class EntityHandler extends GenericInvocationHandler<CacheItem<Entity>> {

            private Integer entityId;

            public EntityHandler(Entity entity) {
                super(EntityCacheOld.this.putEntity(entity.getId(), entity));
                this.entityId = entity.getId();
            }

            public EntityHandler(Integer entityId) {
                super(null);
                this.entityId = entityId;
            }

            public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
                Entity entity;
                if (object == null || (entity = object.getValue()) == null || object.isExpired()) {
                    synchronized(EntityCacheOld.this) {
                        entity = EntityCacheOld.this.get(entityId);
                        if (entity != null) {
                            object = EntityCacheOld.this.storage.get(entityId);
                        } else {
                            entity = loadEntity(entityId);
                            object = EntityCacheOld.this.putEntity(entityId, entity);
                        }
                    }
                }
                return method.invoke(entity, objects);
            }

        }
    }

}

interface Entity extends Cloneable {

    public Integer getId();

    public void setId(Integer id);

    public String getValue();

    public void setValue(String value);

    public Entity clone();

}

class EntityImpl implements Entity {

    protected Integer id;
    protected String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object object) {
        return object instanceof Entity && ((Entity)object).getId().equals(id);
    }

    public Entity clone() {
        try {
            return (Entity)super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}
