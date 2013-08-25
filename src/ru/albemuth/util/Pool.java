package ru.albemuth.util;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 09.08.2004
 * Time: 13:45:23
 * To change this template use Options | File Templates.
 */
public class Pool<T> {

    public static final int EXCEPTION_WHEN_EXHAUSTED                = 0;
    public static final int WAIT_WHEN_EXHAUSTED                     = 1;
    public static final int GROW_WHEN_EXHAUSTED                     = 2;

    protected String name;

    protected T[] itemStore;
    protected int poolPointer;
    protected Class<T> itemClass;
    protected PoolItemFactory<T> itemFactory;

    protected boolean exceptionWhenExhausted;
    protected boolean waitWhenExhausted;
    protected boolean growWhenExhausted;

    protected long maxWait;

    protected int initialSize;
    protected int maxSize;

    protected Pool(String aName, int aInitialSize, int aMaxSize, int aAction, long aMaxWait) throws PoolException {
        name = aName;
        initialSize = aInitialSize;
        maxSize = aMaxSize;
        maxWait = aMaxWait;
        if (aAction == EXCEPTION_WHEN_EXHAUSTED) {
            exceptionWhenExhausted = true;
        } else if (aAction == WAIT_WHEN_EXHAUSTED) {
            waitWhenExhausted = true;
        } else if (aAction == GROW_WHEN_EXHAUSTED) {
            growWhenExhausted = true;
        } else {
            throw new PoolException("Unknown whenExhaustedAction: " + aAction);
        }
    }

    public Pool(String aName, int aInitialSize, int aMaxSize, int aAction, long aMaxWait, Class<T> aItemClass) throws PoolException {
        this(aName,aInitialSize, aMaxSize, aAction, aMaxWait);
        itemClass = aItemClass;
        createItemStore(initialSize, initialSize);
    }

    public Pool(String aName, int aInitialSize, int aMaxSize, int aAction, long aMaxWait, PoolItemFactory<T> aItemFactory) throws PoolException {
        this(aName,aInitialSize, aMaxSize, aAction, aMaxWait);
        itemFactory = aItemFactory;
        itemClass = itemFactory.getPoolItemClass();
        createItemStore(initialSize, initialSize);
    }

    public int size() {
        return itemStore.length;
    }

    public synchronized T getObject() throws PoolException {
        if (poolPointer == 0) {
            doWhenExhaustedAction();
        }
        poolPointer--;
        T ret = itemStore[poolPointer];
        itemStore[poolPointer] = null;
        return ret;
    }

    public synchronized void returnObject(T aObject) throws PoolException {
        if (aObject == null) {
            throw new PoolException("Try to return null object");
        } else {
            //TODO: there are no check for double returned objects!
            itemStore[poolPointer] = aObject;
            poolPointer++;
            if (waitWhenExhausted) {
                notify();
            }
        }
    }

    private void createItemStore(int aStoreSize, int aItemNumber) throws PoolException {
        itemStore = (T[])new Object[aStoreSize];
        if (aItemNumber > aStoreSize) {
            throw new PoolException("Initial item number - " + aItemNumber + " more, than store size: " + aStoreSize);
        }
        for (int i = 0; i < aItemNumber; i++) {
            itemStore[i] = getNewItem();
        }
        poolPointer = aItemNumber;
    }

    private T getNewItem() throws PoolException {
        T ret;
        if (itemFactory != null) {
            ret = itemFactory.getNewPoolItem();
        } else {
            try {
                ret = itemClass.newInstance();
            } catch (InstantiationException e) {
                throw new PoolException("InstantiationException while creating new pool item: " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new PoolException("IllegalAccessException while creating new pool item: " + e.getMessage(), e);
            }
        }
        return ret;
    }

    private void doWhenExhaustedAction() throws PoolException {
        if (exceptionWhenExhausted) {
            exceptionWhenExhausted();
        } else if (waitWhenExhausted) {
            waitWhenExhausted();
        } else if (growWhenExhausted) {
            growWhenExhausted();
        }

    }

    protected void exceptionWhenExhausted() throws PoolException {
        throw new PoolException("Pool " + name + " exhausted");
    }

    protected void waitWhenExhausted() throws PoolException {
        try {
            synchronized(this) {
                wait(maxWait);
            }
            if (poolPointer == 0) {
                throw new PoolException("Pool " + name + " exhausted: wait for new item too long");
            }
        } catch (InterruptedException e) {
            throw new PoolException("InterruptedException while waiting pool item: " + e.getMessage(), e);
        }
    }

    protected void growWhenExhausted() throws PoolException {
        if (itemStore.length == maxSize) {
            //throw new PoolException("Pool " + name + " exhausted: maximum item store size (" + maxSize + ") achieved");
            try {
                waitWhenExhausted();
            } catch (PoolException e) {
                throw new PoolException("Pool " + name + " exhausted: maximum item store size (" + maxSize + ") achieved", e);
            }
        } else {
            int newItemStoreSize = 2 * itemStore.length;
            if (newItemStoreSize > maxSize) {
                newItemStoreSize = maxSize;
            }
            createItemStore(newItemStoreSize, newItemStoreSize - itemStore.length);
        }
    }

}
