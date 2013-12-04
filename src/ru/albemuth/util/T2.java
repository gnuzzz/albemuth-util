package ru.albemuth.util;

/**
 * @author VKornyshev
 */
public class T2<V1, V2> {

    protected V1 v1;
    protected V2 v2;

    public T2(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public V1 getV1() {
        return v1;
    }

    public V2 getV2() {
        return v2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        T2 t2 = (T2) o;

        return !(v1 != null ? !v1.equals(t2.v1) : t2.v1 != null) &&
                !(v2 != null ? !v2.equals(t2.v2) : t2.v2 != null);

    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        return result;
    }

}
