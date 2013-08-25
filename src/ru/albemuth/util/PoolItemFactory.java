package ru.albemuth.util;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 09.08.2004
 * Time: 13:46:47
 * To change this template use Options | File Templates.
 */
public interface PoolItemFactory<T> {

    public T getNewPoolItem() throws PoolException;

    public Class<T> getPoolItemClass() throws PoolException;

}
