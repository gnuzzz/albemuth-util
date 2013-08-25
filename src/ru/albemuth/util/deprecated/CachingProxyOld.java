package ru.albemuth.util.deprecated;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 31.03.2006
 * Time: 14:49:27
 */
public abstract class CachingProxyOld<T> {

    private static final Object KEY                                 = new Object();

    private CacheOld<T> cacheOld = new CacheOld<T>("cached data", 1, getTtl(), CacheOld.REPLACE_WHEN_EXHAUSTED);
    
    protected abstract long getTtl();
    protected abstract T obtainData();

    public T getData() {
        T data = cacheOld.get(KEY);
        if (data == null) {
            synchronized (this) {
                data = cacheOld.get(KEY);
                if (data == null) {
                    data = obtainData();
                    cacheOld.put(KEY, data);
                }
            }
        }
        return data;
    }

}
