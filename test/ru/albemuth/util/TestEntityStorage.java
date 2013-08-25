package ru.albemuth.util;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;

public class TestEntityStorage extends TestCase {

    /*public void test() {
        try {
            EntityStorage<ValueImpl> es = new EntityStorage<ValueImpl>() {
                @Override
                public Class<? extends ValueImpl> getEntityClass() {
                    return ValueImpl.class;
                }

                @Override
                public ValueImpl loadEntity(Object key) {
                    System.out.println("Loading " + key);
                    return new ValueImpl(key.toString(), key.toString());
                }

                @Override
                protected boolean keepEntityInCache(ValueImpl entity) {
                    return entity.getValue().startsWith("1");
                }
            };
            Value v1 = es.get("1");
            Value v2 = es.get("2");
            Value v3 = es.get("3", EntityStorage.FLAG_LAZY);
            Value v12 = es.get("12", EntityStorage.FLAG_LAZY);
            System.out.println(v1.getValue() + ", " + v2.getValue() + ", " + v3.getValue() + ", " + v12.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }*/

    //get expired
    public void testGetExpired() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    ttl = 1000;
                    Value value = storage.get("aaa");
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                    Thread.sleep(1500);
                    assertEquals(1, loadCounter);
                    assertEquals("2", value.getValue());
                    assertEquals(2, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {}
            }
        );
    }

    //get lazy expired
    public void testGetLazyExpired() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    ttl = 1000;
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    Thread.sleep(1500);
                    assertEquals(0, loadCounter);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {}
            }
        );
    }

    //get loaded only
    public void testGetLoadedOnly() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    ttl = 1000;
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {}
            }
        );
    }

    //put expired
    public void testPutExpired() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("bbb", new ValueImpl("bbb", "p"), 1000);
                    Value value = storage.get("bbb");
                    assertEquals(0, loadCounter);
                    assertEquals("p", value.getValue());
                    Thread.sleep(1500);
                    assertEquals(0, loadCounter);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                    Thread.sleep(1500);
                    assertEquals(1, loadCounter);
                    assertEquals("2", value.getValue());
                    assertEquals(2, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {}
            }
        );
    }

    //get, get
    public void testGetGet() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa");
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.get("aaa");
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                }
            }
        );
    }

    //get, put
    public void testGetPut() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa");
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                    Thread.sleep(2000);
                    assertEquals(1, loadCounter);
                    assertEquals("p", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals("1", value.getValue());
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                }
            }
        );
    }

    //put, get
    public void testPutGet() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    assertEquals(0, loadCounter);
                    Value value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                }
            }
        );
    }

    //put, put
    public void testPutPut() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p1"));
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p1", value.getValue());
                    Thread.sleep(2000);
                    assertEquals("p2", value.getValue());
                    assertEquals(0, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p2"));
                    assertEquals("p1", value.getValue());
                    value = storage.get("aaa");
                    assertEquals("p2", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //get lazy, get
    public void testGetLazyGet() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    Thread.sleep(2000);
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.get("aaa");
                    assertEquals("1", value.getValue());
                }
            }
        );
    }

    //get, get lazy
    public void testGetGetLazy() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa");
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            }
        );
    }

    //get lazy, put
    public void testGetLazyPut() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    Thread.sleep(2000);
                    assertEquals(0, loadCounter);
                    assertEquals("p", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //put, get lazy
    public void testPutGetLazy() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //get lazy, get lazy
    public void testGetLazyGetLazy() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            }
        );
    }

    //get loaded only, get
    public void testGetLoadedOnlyGet() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(2000);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.get("aaa");
                    assertEquals("1", value.getValue());
                }
            }
        );
    }

    //get loaded only, get lazy
    public void testGetLoadedOnlyGetLazy() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(2000);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    storage.get("aaa", EntityStorage.FLAG_LAZY);
                }
            }
        );
    }

    //get, get loaded only
    public void testGetGetLoadedOnly() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa");
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            }
        );
    }

    //get lazy, get loaded only
    public void testGetLazyGetLoadedOnly() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    Thread.sleep(2000);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(2000);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            }
        );
    }

    //get loaded only, put
    public void testGetLoadedOnlyPut() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(2000);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNotNull(value);
                    assertEquals("p", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //put, get loaded only
    public void testPutGetLoadedOnly() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertNotNull(value);
                    assertEquals(0, loadCounter);
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //get loaded only, get loaded only
    public void testGetLoadedOnlyGetLoadedOnly() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                }
            }
        );
    }

    //get & get
    public void testGetAndGet() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa");
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.get("aaa");
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            }
        );
    }

    //get & put
    public void testGetAndPut() {
        doTest(
            1000,
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa");
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                    Thread.sleep(500);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                    Thread.sleep(1000);
                    assertEquals("p", value.getValue());
                    assertEquals(1, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals("1", value.getValue());
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                }
            }
        );
    }

    //put & get
    public void testPutAndGet() {
        doTest(
            1000,
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertNull(value);
                    assertEquals(0, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    assertEquals(0, loadCounter);
                    Value value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                }
            }
        );
    }

    //put & put
    public void testPutAndPut() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p1"));
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p1", value.getValue());
                    Thread.sleep(2000);
                    assertEquals("p2", value.getValue());
                    assertEquals(0, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p2"));
                    assertEquals("p1", value.getValue());
                    value = storage.get("aaa");
                    assertEquals("p2", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //get lazy & get
    public void testGetLazyAndGet() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.get("aaa");
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            }
        );
    }

    //get & get lazy
    public void testGetAndGetLazy() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa");
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            }
        );
    }

    //get lazy & put
    public void testGetLazyAndPut() {
        doTest(
            1000,
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    assertEquals(0, loadCounter);
                    assertEquals("p", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //put & get lazy
    public void testPutAndGetLazy() {
        doTest(
            1000,
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    Thread.sleep(200);
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //get lazy & get lazy
    public void testGetLazyAndGetLazy() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                }
            }
        );
    }

    //get loaded only & get
    public void testGetLoadedOnlyAndGet() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(1000);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.get("aaa");
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            }
        );
    }

    //get & get loaded only
    public void testGetAndGetLoadedOnly() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa");
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(200);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(500);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(1, loadCounter);
                    assertNotNull(value);
                    assertEquals("1", value.getValue());
                }
            }
        );
    }

    //get loaded only & put
    public void testGetLoadedOnlyAndPut() {
        doTest(
            1000,
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(1000);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(1000);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNotNull(value);
                    assertEquals("p", value.getValue());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //put & get loaded only
    public void testPutAndGetLoadedOnly() {
        doTest(
            1000,
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(200);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(500);
                    value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNotNull(value);
                    assertEquals("p", value.getValue());
                }
            }
        );
    }

    //get, remove
    public void testGetRemove() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa");
                    assertEquals(1, loadCounter);
                    assertEquals("1", value.getValue());
                    Thread.sleep(1000);
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        value = storage.get("aaa");
                        assertEquals(2, loadCounter);
                        assertEquals("2", value.getValue());
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.remove("aaa");
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            }
        );
    }

    //get lazy, remove
    public void testGetLazyRemove() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        value = storage.get("aaa");
                        assertEquals(1, loadCounter);
                        assertEquals("1", value.getValue());
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.remove("aaa");
                    assertNull(value);
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //get loaded only, remove
    public void testGetLoadedOnlyRemove() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(1000);
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        value = storage.get("aaa");
                        assertEquals(1, loadCounter);
                        assertEquals("1", value.getValue());
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.remove("aaa");
                    assertNull(value);
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //put, remove
    public void testPutRemove() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    value = storage.get("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        value = storage.get("aaa");
                        assertEquals(1, loadCounter);
                        assertEquals("1", value.getValue());
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.remove("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //remove, remove
    public void testRemoveRemove() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertNull(value);
                    assertEquals(0, loadCounter);
                    value = storage.remove("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.remove("aaa");
                    assertNull(value);
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //get & remove
    public void testGetAndRemove() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa");
                    assertEquals(1, loadCounter);
                    Thread.sleep(1000);
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        value = storage.get("aaa");
                        assertEquals(2, loadCounter);
                        assertEquals("2", value.getValue());
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.remove("aaa");
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                }
            }
        );
    }

    //get lazy & remove
    public void testGetLazyAndRemove() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LAZY);
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        value = storage.get("aaa");
                        assertEquals(1, loadCounter);
                        assertEquals("1", value.getValue());
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.remove("aaa");
                    assertNull(value);
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //get loaded only & remove
    public void testGetLoadedOnlyAndRemove() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("aaa", EntityStorage.FLAG_LOADED_ONLY);
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(1000);
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        value = storage.get("aaa");
                        assertEquals(1, loadCounter);
                        assertEquals("1", value.getValue());
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.remove("aaa");
                    assertNull(value);
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //put & remove
    public void testPutAndRemove() {
        doTest(
            1000,
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertEquals(0, loadCounter);
                    assertNull(value);
                    Thread.sleep(1000);
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        value = storage.get("aaa");
                        assertEquals(1, loadCounter);
                        assertEquals("1", value.getValue());
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.remove("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //remove & remove
    public void testRemoveAndRemove() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.put("aaa", new ValueImpl("aaa", "p"));
                    assertNull(value);
                    assertEquals(0, loadCounter);
                    value = storage.remove("aaa");
                    assertEquals("p", value.getValue());
                    assertEquals(0, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Value value = storage.remove("aaa");
                    assertNull(value);
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //refresh
    public void testRefresh() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb"), 1000);
                    assertEquals(0, loadCounter);
                    Value value1 = storage.get("aaa");
                    Value value2 = storage.get("bbb");
                    assertEquals(0, loadCounter);
                    Thread.sleep(2000);
                    try {
                        assertEquals("pb", value2.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        Value value3 = storage.get("aaa");
                        Value value4 = storage.get("bbb");
                        assertEquals(2, loadCounter);
                        assertTrue(value1 == value3);
                        assertFalse(value2 == value4);
                    }

                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1100);
                    storage.refresh();
                }
            }
        );
    }

    //get & refresh
    public void testGetAndRefresh() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb"), 1000);
                    storage.put("ccc", new ValueImpl("ccc", "pc"), 1000);
                    Thread.sleep(1300);
                    Value value = storage.get("bbb");
                    Thread.sleep(500);
                    assertTrue(value == storage.get("bbb"));
                    assertEquals("1", value.getValue());
                    assertEquals(1, loadCounter);
                    assertEquals(2, storage.size());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1500);
                    storage.refresh();
                }
            }
        );
    }

    //refresh & get
    private long pt;
    public void testRefreshAndGet() {
        pt = 0;
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb") {
                        public String getValue() {
                            if (pt > 0) {
                                try {
                                    Thread.sleep(pt);
                                } catch (InterruptedException e) {
                                    System.err.println("InterruptedException while waiting for put timeout");
                                }
                            }
                            return super.getValue();
                        }
                    }, 1000);
                    storage.put("ccc", new ValueImpl("ccc", "pc"), 1000);
                    pt = 2000;
                    Thread.sleep(1300);
                    storage.refresh();
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(3500);
                    Value value = storage.get("bbb");
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        assertEquals(0, loadCounter);
                        assertEquals(1, storage.size());
                    }
                }
            }
        );
    }

    //get lazy & refresh
    public void testGetLazyAndRefresh() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb"), 1000);
                    storage.put("ccc", new ValueImpl("ccc", "pc"), 1000);
                    Thread.sleep(1300);
                    Value value = storage.get("bbb", EntityStorage.FLAG_LAZY);
                    Thread.sleep(500);
                    assertFalse(value == storage.get("bbb"));
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        assertEquals(1, loadCounter);
                        assertEquals(2, storage.size());
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1500);
                    storage.refresh();
                }
            }
        );
    }

    //get loaded only & refresh
    public void testGetLoadedOnlyAndRefresh() {
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb"), 1000);
                    storage.put("ccc", new ValueImpl("ccc", "pc"), 1000);
                    Thread.sleep(1300);
                    Value value = storage.get("bbb", EntityStorage.FLAG_LOADED_ONLY);
                    Thread.sleep(500);
                    assertFalse(value == storage.get("bbb"));
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        assertEquals(1, loadCounter);
                        assertEquals(2, storage.size());
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1500);
                    storage.refresh();
                }
            }
        );
    }

    //refresh & get lazy
    public void testRefreshAndGetLazy() {
        pt = 0;
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb") {
                        public String getValue() {
                            if (pt > 0) {
                                try {
                                    Thread.sleep(pt);
                                } catch (InterruptedException e) {
                                    System.err.println("InterruptedException while waiting for put timeout");
                                }
                            }
                            return super.getValue();
                        }
                    }, 1000);
                    storage.put("ccc", new ValueImpl("ccc", "pc"), 1000);
                    pt = 2000;
                    Thread.sleep(1300);
                    storage.refresh();
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(3500);
                    Value value = storage.get("bbb", EntityStorage.FLAG_LAZY);
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        assertEquals(0, loadCounter);
                        assertEquals(1, storage.size());
                    }
                }
            }
        );
    }

    //refresh & get loaded only
    public void testRefreshAndGetLoadedOnly() {
        pt = 0;
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb") {
                        public String getValue() {
                            if (pt > 0) {
                                try {
                                    Thread.sleep(pt);
                                } catch (InterruptedException e) {
                                    System.err.println("InterruptedException while waiting for put timeout");
                                }
                            }
                            return super.getValue();
                        }
                    }, 1000);
                    storage.put("ccc", new ValueImpl("ccc", "pc"), 1000);
                    pt = 2000;
                    Thread.sleep(1300);
                    storage.refresh();
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(3500);
                    Value value = storage.get("bbb", EntityStorage.FLAG_LOADED_ONLY);
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        assertEquals(0, loadCounter);
                        assertEquals(1, storage.size());
                    }
                }
            }
        );
    }

    //put & refresh
    public void testPutAndRefresh() {
        pt = 0;
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb") {
                        public String getValue() {
                            if (pt > 0) {
                                try {
                                    Thread.sleep(pt);
                                } catch (InterruptedException e) {
                                    System.err.println("InterruptedException while waiting for put timeout");
                                }
                            }
                            return super.getValue();
                        }
                    }, 1000);
                    storage.put("ccc", new ValueImpl("ccc", "pc"), 1000);
                    Thread.sleep(1300);
                    pt = 2000;
                    storage.put("bbb", new ValueImpl("bbb", "pbb") {
                        public String getValue() {
                            if (pt > 0) {
                                try {
                                    Thread.sleep(pt);
                                } catch (InterruptedException e) {
                                    System.err.println("InterruptedException while waiting for put timeout");
                                }
                            }
                            return super.getValue();
                        }
                    }, 1000);
                    Thread.sleep(500);
                    assertEquals("pbb", storage.get("bbb").getValue());
                    assertEquals(0, loadCounter);
                    assertEquals(2, storage.size());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(3500);
                    storage.refresh();
                }
            }
        );
    }

    // refresh & put
    public void testRefreshAndPut() {
        pt = 0;
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb") {
                        public String getValue() {
                            if (pt > 0) {
                                try {
                                    Thread.sleep(pt);
                                } catch (InterruptedException e) {
                                    System.err.println("InterruptedException while waiting for put timeout");
                                }
                            }
                            return super.getValue();
                        }
                    }, 1000);
                    storage.put("ccc", new ValueImpl("ccc", "pc"), 1000);
                    pt = 2000;
                    Thread.sleep(1300);
                    storage.refresh();
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("bbb", EntityStorage.FLAG_LAZY);
                    Thread.sleep(3500);
                    storage.put("bbb", new ValueImpl("bbb", "pbb") {
                        public String getValue() {
                            if (pt > 0) {
                                try {
                                    Thread.sleep(pt);
                                } catch (InterruptedException e) {
                                    System.err.println("InterruptedException while waiting for put timeout");
                                }
                            }
                            return super.getValue();
                        }
                    }, 3000);//3000 - чтобы новое содержимое не устарело из-за pt=2000
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        assertEquals(0, loadCounter);
                        assertEquals(2, storage.size());
                        assertEquals("pbb", storage.get("bbb").getValue());
                    }
                }
            }
        );
    }

    //remove & refresh
    public void testRemoveAndRefresh() {
        pt = 0;
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb") {
                        public String getValue() {
                            if (pt > 0) {
                                try {
                                    Thread.sleep(pt);
                                } catch (InterruptedException e) {
                                    System.err.println("InterruptedException while waiting for put timeout");
                                }
                            }
                            return super.getValue();
                        }
                    }, 5000);
                    storage.put("ccc", new ValueImpl("ccc", "cc"), 1000);
                    pt = 2000;
                    storage.remove("bbb");
                    Thread.sleep(2500);
                    assertEquals(0, loadCounter);
                    assertEquals(2, storage.size());
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    storage.refresh();
                }
            }
        );
    }

    //refresh & remove
    public void testRefreshAndRemove() {
        pt = 0;
        doTest(
            1000,
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 1000);
                    storage.put("bbb", new ValueImpl("bbb", "pb") {
                        public String getValue() {
                            if (pt > 0) {
                                try {
                                    Thread.sleep(pt);
                                } catch (InterruptedException e) {
                                    System.err.println("InterruptedException while waiting for put timeout");
                                }
                            }
                            return super.getValue();
                        }
                    }, 1000);
                    storage.put("ccc", new ValueImpl("ccc", "cc"), 1000);
                    pt = 2000;
                    Thread.sleep(1300);
                    storage.refresh();
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Value value = storage.get("bbb", EntityStorage.FLAG_LAZY);
                    Thread.sleep(3500);
                    Value v = storage.remove("bbb");
                    try {
                        assertEquals("1", value.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        assertEquals(0, loadCounter);
                        assertEquals(1, storage.size());
                        assertEquals("pb", v.getValue());
                    }
                }
            }
        );
    }

    //clear
    public void testClear() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 2000);
                    storage.put("bbb", new ValueImpl("bbb", "pb"), 2000);
                    assertEquals(0, loadCounter);
                    Value value1 = storage.get("aaa");
                    Value value2 = storage.get("bbb");
                    assertEquals(0, loadCounter);
                    Thread.sleep(1100);
                    try {
                        assertEquals("pb", value2.getValue());
                        fail();
                    } catch (NullPointerException e) {
                        Value value3 = storage.get("aaa");
                        Value value4 = storage.get("bbb");
                        assertEquals(2, loadCounter);
                        assertFalse(value1 == value3);
                        assertFalse(value2 == value4);
                    }

                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(1000);
                    storage.clear();
                }
            }
        );
    }

    //values put
    public void testValuesPut() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Collection<Value> values = storage.values();
                    assertEquals(2, values.size());
                    for (Value value: values) {
                        if ("aaa".equals(value.getKey())) {
                            assertEquals("p", value.getValue());
                        } else if ("bbb".equals(value.getKey())) {
                            assertEquals("pb", value.getValue());
                        }
                    }
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    for (Value value: values) {
                        if ("aaa".equals(value.getKey())) {
                            assertEquals("p", value.getValue());
                        } else if ("bbb".equals(value.getKey())) {
                            assertEquals("pbb", value.getValue());
                        }
                    }
                    assertEquals(0, loadCounter);
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 2000);
                    storage.put("bbb", new ValueImpl("bbb", "pb"), 2000);
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    storage.put("bbb", new ValueImpl("bbb", "pbb"), 2000);
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //values remove
    public void testValuesRemove() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Collection<Value> values = storage.values();
                    assertEquals(2, values.size());
                    for (Value value: values) {
                        if ("aaa".equals(value.getKey())) {
                            assertEquals("p", value.getValue());
                        } else if ("bbb".equals(value.getKey())) {
                            assertEquals("pb", value.getValue());
                        }
                    }
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    try {
                        for (Value value: values) {
                            if ("aaa".equals(value.getKey())) {
                                assertEquals("p", value.getValue());
                            } else if ("bbb".equals(value.getKey())) {
                                assertEquals("pbb", value.getValue());
                            }
                        }
                        fail();
                    } catch (NullPointerException e) {
                        assertEquals(1, storage.size());
                        Value value = storage.get("aaa");
                        assertEquals("p", value.getValue());
                        assertEquals(0, loadCounter);
                    }
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 2000);
                    storage.put("bbb", new ValueImpl("bbb", "pb"), 2000);
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    storage.remove("bbb");
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //keySet put
    public void testKeySetPut() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Set<Object> keys = storage.keySet();
                    assertEquals(0, loadCounter);
                    assertEquals(2, keys.size());
                    assertTrue(keys.contains("aaa"));
                    assertTrue(keys.contains("bbb"));
                    Thread.sleep(1000);
                    keys = storage.keySet();
                    assertEquals(0, loadCounter);
                    assertEquals(2, keys.size());
                    assertTrue(keys.contains("aaa"));
                    assertTrue(keys.contains("bbb"));
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 2000);
                    storage.put("bbb", new ValueImpl("bbb", "pb"), 2000);
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    storage.put("bbb", new ValueImpl("bbb", "pbb"), 2000);
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    //keySet remove
    public void testKeySetRemove() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    Thread.sleep(500);
                    Set<Object> keys = storage.keySet();
                    assertEquals(0, loadCounter);
                    assertEquals(2, keys.size());
                    assertTrue(keys.contains("aaa"));
                    assertTrue(keys.contains("bbb"));
                    Thread.sleep(1000);
                    keys = storage.keySet();
                    assertEquals(0, loadCounter);
                    assertEquals(1, keys.size());
                    assertTrue(keys.contains("aaa"));
                    assertFalse(keys.contains("bbb"));
                }
            },
            new Worker<Value>() {
                public void work() throws Exception {
                    storage.put("aaa", new ValueImpl("aaa", "p"), 2000);
                    storage.put("bbb", new ValueImpl("bbb", "pb"), 2000);
                    assertEquals(0, loadCounter);
                    Thread.sleep(1000);
                    storage.remove("bbb");
                    assertEquals(0, loadCounter);
                }
            }
        );
    }

    private volatile int loadCounter;
    private boolean failed;
    private long ttl;

    public synchronized void setFailed(boolean failed) {
        this.failed = failed;
    }

    protected void doTest(final long loadTimeout, final long putTimeout, Worker<Value> worker1, Worker<Value> worker2) {
        try {
            loadCounter = 0;
            ttl = 60*60000;
            EntityStorage<Value> storage = new EntityStorage<Value>() {

                public Class<? extends Value> getEntityClass() {
                    return ValueImpl.class;
                }

                public synchronized Value loadEntity(Object key) {
                    if (loadTimeout > 0) {
                        try {
                            Thread.sleep(loadTimeout);
                        } catch (InterruptedException e) {
                            System.err.println("InterruptedException while waiting for load timeout");
                        }
                    }
                    return new ValueImpl(key.toString(), Integer.valueOf(++loadCounter).toString());
                }

                protected final boolean keepEntityInCache(Value entity) {
                    if (putTimeout > 0 && entity.getValue().charAt(0) == 'p') {
                        try {
                            Thread.sleep(putTimeout);
                        } catch (InterruptedException e) {
                            System.err.println("InterruptedException while waiting for put timeout");
                        }
                    }
                    return entity.getValue().length() == 1;
                }

                public long ttl() {
                    return ttl;
                }

            };
            storage.configure(new Configuration(new Properties()));
            worker1.setStorage(storage);
            worker2.setStorage(storage);
            Thread thread1 = new Thread(worker1);
            thread1.start();
            Thread thread2 = new Thread(worker2);
            thread2.start();
            thread1.join();
            thread2.join();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        if (failed) {
            fail();
        }
    }

    //stress
    public void testStress() {
        doTest(
            new Worker<Value>() {
                public void work() throws Exception {
                    ttl = 1000000;
                    Value value = storage.get("aaa");
                    int result = value.getValue().length();
                    int curcles = 10000000;
                    long t1 = System.currentTimeMillis();
                    for (int i = 0; i < curcles; i++) {
                        result += value.getValue().length();
                    }
                    double av1 = (System.currentTimeMillis() - t1)/(double)curcles;

                    value = storage.remove("aaa");
                    t1 = System.currentTimeMillis();
                    for (int i = 0; i < curcles; i++) {
                        result += value.getValue().length();
                    }
                    double av2 = (System.currentTimeMillis() - t1)/(double)curcles;

                    assertTrue(result > 0 && av1 > 0 && av2 > 0 && av2 * 50 > av1);

                }
            },
            new Worker<Value>() {
                public void work() throws Exception {}
            }
        );
    }

    protected void doTest(Worker<Value> worker1, Worker<Value> worker2) {
        doTest(0, 0, worker1, worker2);
    }

    protected void doTest(final long loadTimeout, Worker<Value> worker1, Worker<Value> worker2) {
        doTest(loadTimeout, 0, worker1, worker2);
    }

    abstract class Worker<T> implements Runnable {

        protected EntityStorage<T> storage;

        public void setStorage(EntityStorage<T> storage) {
            this.storage = storage;
        }

        public void run() {
            try {
                work();
            } catch (Throwable th) {
                th.printStackTrace();
                setFailed(true);
            }
        }

        protected abstract void work() throws Exception;

    }

}