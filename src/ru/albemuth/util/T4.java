package ru.albemuth.util;

/**
 * @author VKornyshev
 */
public class T4<V1, V2, V3, V4> {

    protected V1 v1;
    protected V2 v2;
    protected V3 v3;
    protected V4 v4;

    public T4(V1 v1, V2 v2, V3 v3, V4 v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
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

    public V4 getV4() {
        return v4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        T4 t4 = (T4) o;

        return !(v1 != null ? !v1.equals(t4.v1) : t4.v1 != null) &&
                !(v2 != null ? !v2.equals(t4.v2) : t4.v2 != null) &&
                !(v3 != null ? !v3.equals(t4.v3) : t4.v3 != null) &&
                !(v4 != null ? !v4.equals(t4.v4) : t4.v4 != null);

    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        result = 31 * result + (v3 != null ? v3.hashCode() : 0);
        result = 31 * result + (v4 != null ? v4.hashCode() : 0);
        return result;
    }
}
