package ru.albemuth.util.deprecated;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 17.08.2007
 * Time: 19:26:32
 */
public class LevensteinMultitudeComparator {

    private static final ThreadLocal<int[]> TABLE = new ThreadLocal<int[]>();

    private static ThreadLocal<int[][][]> table = new ThreadLocal<int[][][]>();
    private static ThreadLocal<int[]> insert = new ThreadLocal<int[]>();
    private static ThreadLocal<int[]> delete = new ThreadLocal<int[]>();
    private static ThreadLocal<int[]> update = new ThreadLocal<int[]>();

    public static <T> double minDifference(Collection<? extends T> coll1, Collection<? extends T> coll2) {
        return Math.abs(coll1.size() - coll2.size());
    }

    public static <T> double maxDifference(Collection<? extends T> coll1, Collection<? extends T> coll2) {
        return Math.sqrt(coll1.size() * coll1.size() + coll2.size() * coll2.size());
    }

    public static <T> double difference(Set<? extends T> set1, Set<? extends T> set2) {
        Set<T> s1 = new HashSet<T>(set1);
        double s1size = s1.size();
        s1.retainAll(set2);
        return Math.sqrt((s1size - s1.size()) * (s1size - s1.size()) + (set2.size() - s1.size()) * (set2.size() - s1.size()));
    }

    public static <T> double relativeDifference(Set<? extends T> set1, Set<? extends T> set2) {
        return relativeDifference(set1, set2, difference(set1, set2));
    }

    public static <T> double relativeDifference(Set<? extends T> set1, Set<? extends T> set2, double difference) {
        return difference / maxDifference(set1, set2);
    }

    public static <T> double difference(List<? extends T> list1, List<? extends T> list2) {
        Set<T> itemsSet = new HashSet<T>(list1);
        itemsSet.addAll(list2);
        if (itemsSet.size() > 65536) {
            throw new IndexOutOfBoundsException("List parameters contains more than 65536 different objects");
        }
        HashMap<T, Character> itemsMap = new HashMap<T, Character>();
        Iterator<T> it = itemsSet.iterator();
        for (char c = 0; it.hasNext(); c++) {
            itemsMap.put(it.next(), c);
        }
        return difference(createStringFromList(list1, itemsMap), createStringFromList(list2, itemsMap));
    }

    protected static <T> String createStringFromList(List<? extends T> list, Map<T, Character> itemsMap) {
        StringBuffer sb = new StringBuffer();
        for (T t: list) {
            sb.append(itemsMap.get(t));
        }
        return sb.toString();
    }

    public static <T> double relativeDifference(List<? extends T> list1, List<? extends T> list2) {
        return relativeDifference(list1, list2, difference(list1, list2));
    }

    public static <T> double relativeDifference(List<? extends T> list1, List<? extends T> list2, double difference) {
        return difference / maxDifference(list1, list2);
    }

    public static double difference(String s1, String s2) {
        int[] levensteinVector = levensteinVector(s1, s2);
        return Math.sqrt(levensteinVector[0] * levensteinVector[0] + levensteinVector[1] * levensteinVector[1]);
    }

    public static double fastDifference(String s1, String s2) {
        return Math.sqrt(levenstein(s1, s2));
    }

    public static double minDifference(String s1, String s2) {
        return Math.abs(s1.length() - s2.length());
    }

    public static double maxDifference(String s1, String s2) {
        return Math.sqrt(s1.length() * s1.length() + s2.length() * s2.length());
    }

    public static double relativeDifference(String s1, String s2) {
        return relativeDifference(s1, s2, difference(s1, s2));
    }

    public static double relativeDifference(String s1, String s2, double difference) {
        return difference / maxDifference(s1, s2);
    }

    protected static void checkArrays(String s1, String s2) {
        int[][][] tab = table.get();
        if (tab == null) {
            table.set(new int[s1.length() + 1 < 255 ? 255 : s1.length() + 1][s2.length() + 1 < 255 ? 255 : s2.length() + 1][2]);
        } else if (tab.length < s1.length() + 1 || tab[0].length < s2.length() + 1) {
            table.set(new int[s1.length() + 1][s2.length() + 1][2]);
        }
        if (insert.get() == null) {
            insert.set(new int[2]);
        }
        if (delete.get() == null) {
            delete.set(new int[2]);
        }
        if (update.get() == null) {
            update.set(new int[2]);
        }
    }

    protected static void check(String s1, String s2) {
        int[] tab = TABLE.get();
        if (tab == null) {
            TABLE.set(new int[(s1.length() + 1 < 255 ? 255 : s1.length() + 1) * (s2.length() + 1 < 255 ? 255 : s2.length() + 1) * 2]);
        } else if (tab.length < (s1.length() + 1) * (s2.length() + 1) * 2) {
            TABLE.set(new int[(s1.length() + 1) * (s2.length() + 1) * 2]);
        }
    }

    protected static int[] levensteinVector(String s1, String s2) {
        checkArrays(s1, s2);
        int tab[][][] = table.get();
        for(int i = 0; i <= s1.length(); i++ ) {
            tab[i][0][0] = i;
        }
        for(int j = 0; j <= s2.length(); j++ ) {
            tab[0][j][1] = j;
        }
        int[] ins = insert.get(), del = delete.get(), upd = update.get();
        int insLength, delLength, updLength, minLength;
        int[] min;
        for(int i = 1; i <= s1.length(); i++ ) {
            for(int j = 1; j <= s2.length(); j++ ) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    upd[0] = tab[i-1][j-1][0];
                    upd[1] = tab[i-1][j-1][1];
                } else {
                    upd[0] = tab[i-1][j-1][0] + 1;
                    upd[1] = tab[i-1][j-1][1] + 1;
                }
                ins[0] = tab[i-1][j][0] + 1;
                ins[1] = tab[i-1][j][1];
                del[0] = tab[i][j-1][0];
                del[1] = tab[i][j-1][1] + 1;

                insLength = ins[0] * ins[0] + ins[1] * ins[1];
                delLength = del[0] * del[0] + del[1] * del[1];
                updLength = upd[0] * upd[0] + upd[1] * upd[1];

                minLength = insLength;
                min = ins;
                if (delLength < insLength) {
                    minLength = delLength;
                    min = del;
                }
                if (updLength < minLength) {
                    min = upd;
                }

                tab[i][j][0] = min[0];
                tab[i][j][1] = min[1];
            }
        }
        return tab[s1.length()][s2.length()];
    }

    protected static int levenstein(String s1, String s2) {
        check(s1, s2);
        int[] tab = TABLE.get();
        int s1length = s1.length();
        int s2length = s2.length();
        for (int i = 0; i <= s1length; i++) {
            tab[i * s2length * 2] = i;
        }
        for (int j = 0; j <= s2length; j++) {
            tab[j * 2 + 1] = j;
        }
        int ins0, ins1, del0, del1, upd0, upd1;
        int insLength, delLength, updLength;
        int tabIndex;
        for (int i = 1; i <= s1length; i++) {
            for (int j = 1; j <= s2length; j++) {
                tabIndex = (i - 1) * s2length * 2 + (j - 1) * 2;
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    upd0 = tab[tabIndex];
                    upd1 = tab[tabIndex + 1];
                } else {
                    upd0 = tab[tabIndex] + 1;
                    upd1 = tab[tabIndex + 1] + 1;
                }
                tabIndex = (i - 1) * s2length * 2 + j * 2;
                ins0 = tab[tabIndex] + 1;
                ins1 = tab[tabIndex + 1];
                tabIndex = i * s2length * 2 + (j - 1) * 2;
                del0 = tab[tabIndex];
                del1 = tab[tabIndex + 1] + 1;

                insLength = ins0 * ins0 + ins1 * ins1;
                delLength = del0 * del0 + del1 * del1;
                updLength = upd0 * upd0 + upd1 * upd1;

                tabIndex = i * s2length * 2 + j * 2;
                if (insLength < delLength) {
                    if (insLength < updLength) {
                        tab[tabIndex] = ins0;
                        tab[tabIndex + 1] = ins1;
                    } else {
                        tab[tabIndex] = upd0;
                        tab[tabIndex + 1] = upd1;
                    }
                } else {
                    if (delLength < updLength) {
                        tab[tabIndex] = del0;
                        tab[tabIndex + 1] = del1;
                    } else {
                        tab[tabIndex] = upd0;
                        tab[tabIndex + 1] = upd1;
                    }
                }
            }

        }
        tabIndex = s1length * s2length * 2 + s2length * 2;
        ins0 = tab[tabIndex];
        del0 = tab[tabIndex + 1];
        return ins0 *ins0 + del0 * del0;
    }

    public static double length(double[] vector) {
        double ret = 0;
        for (double d: vector) {
            ret += d * d;
        }
        return ret;
    }

}