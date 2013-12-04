package ru.albemuth.util;

/**
 * @author VKornyshev
 */
public class T3<V1, V2, V3> {

    protected V1 v1;
    protected V2 v2;
    protected V3 v3;

    public T3(V1 v1, V2 v2, V3 v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public V1 getV1() {
        return v1;
    }

    public V2 getV2() {
        return v2;
    }

    public V3 getV3() {
        return v3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        T3 t3 = (T3) o;

        return !(v1 != null ? !v1.equals(t3.v1) : t3.v1 != null) &&
                !(v2 != null ? !v2.equals(t3.v2) : t3.v2 != null) &&
                !(v3 != null ? !v3.equals(t3.v3) : t3.v3 != null);

    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        result = 31 * result + (v3 != null ? v3.hashCode() : 0);
        return result;
    }
}
