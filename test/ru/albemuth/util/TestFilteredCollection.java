package ru.albemuth.util;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TestFilteredCollection {

    @Test
    public void testSize() {
        try {
            Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };
            assertEquals(2, filteredCollection.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testIsEmpty() {
        try {
            Collection<Integer> collection, filteredCollection;
            collection = Arrays.asList(1, 2, 3, 4);
            filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };
            assertFalse(filteredCollection.isEmpty());

            collection = Collections.emptyList();
            filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };
            assertTrue(filteredCollection.isEmpty());

            collection = Arrays.asList(1, 3, 5, 7);
            filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };
            assertTrue(filteredCollection.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testContains() {
        try {
            Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };
            assertTrue(filteredCollection.contains(2));
            assertFalse(filteredCollection.contains(1));
            assertFalse(filteredCollection.contains(5));
            assertFalse(filteredCollection.contains(6));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testIterator() {
        try {
            Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };
            Iterator<Integer> it;
            it = filteredCollection.iterator();
            assertTrue(it instanceof FilterIterator);
            for (int i = 0; it.hasNext(); i++) {
                Integer value = it.next();
                assertEquals((i+1)*2, value.intValue());
            }
            assertEquals(2, filteredCollection.size());
            it = filteredCollection.iterator();
            assertFalse(it instanceof FilterIterator);
            for (int i = 0; it.hasNext(); i++) {
                Integer value = it.next();
                assertEquals((i+1)*2, value.intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testToArray() {
        try {
            Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };

            Object[] values = filteredCollection.toArray();
            assertEquals(2, values.length);
            assertTrue(Arrays.equals(new Object[]{2, 4}, values));

            Integer[] ints = {};
            ints = filteredCollection.toArray(ints);
            assertEquals(2, ints.length);
            assertTrue(Arrays.equals(new Integer[]{2, 4}, values));

            ints = new Integer[2];
            ints = filteredCollection.toArray(ints);
            assertEquals(2, ints.length);
            assertTrue(Arrays.equals(new Integer[]{2, 4}, values));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAdd() {
        try {
            Collection<Integer> collection = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };
            filteredCollection.add(5);
            assertFalse(collection.contains(5));
            filteredCollection.add(6);
            assertTrue(collection.contains(6));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testRemove() {
        try {
            Collection<Integer> collection = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };
            assertTrue(filteredCollection.remove(2));
            assertFalse(collection.contains(2));
            assertFalse(filteredCollection.remove(5));
            assertFalse(filteredCollection.remove(3));
            assertTrue(collection.contains(3));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testContainsAll() {
        try {
            Collection<Integer> collection = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };

            assertTrue(filteredCollection.containsAll(Arrays.asList(2, 4)));
            assertFalse(filteredCollection.containsAll(Arrays.asList(2, 5)));
            assertFalse(filteredCollection.containsAll(Arrays.asList(2, 3)));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddAll() {
        try {
            Collection<Integer> collection = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };

            filteredCollection.addAll(Arrays.asList(6, 8));
            assertTrue(filteredCollection.contains(6));
            assertTrue(filteredCollection.contains(8));
            assertTrue(collection.contains(6));
            assertTrue(collection.contains(8));
            filteredCollection.addAll(Arrays.asList(5, 7));
            assertFalse(filteredCollection.contains(5));
            assertFalse(filteredCollection.contains(7));
            assertFalse(collection.contains(5));
            assertFalse(collection.contains(7));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testRemoveAll() {
        try {
            Collection<Integer> collection = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };

            filteredCollection.removeAll(Arrays.asList(1, 2, 3));
            assertFalse(collection.contains(2));
            assertTrue(collection.contains(1));
            assertFalse(collection.contains(2));
            assertTrue(collection.contains(3));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testRetainAll() {
        try {
            Collection<Integer> collection = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };

            filteredCollection.retainAll(Arrays.asList(2, 3));
            assertEquals(1, filteredCollection.size());
            assertEquals(3, collection.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testClear() {
        try {
            Collection<Integer> collection = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
            Collection<Integer> filteredCollection = new FilteredCollection<Integer>(collection) {
                @Override
                public boolean accept(Integer integer) {
                    return integer % 2 == 0;
                }
            };
            filteredCollection.clear();
            assertEquals(0, filteredCollection.size());
            assertEquals(2, collection.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
