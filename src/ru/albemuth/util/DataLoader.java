package ru.albemuth.util;

public abstract class DataLoader<T> implements Configured, Closed {

    public abstract T next() throws LoadException;

}
