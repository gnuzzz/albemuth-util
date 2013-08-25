package ru.albemuth.util;

import java.util.*;

public class Distribution<O> {

    public Map<O, Group<O>> calculate(Iterator<O> objectsIterator) {
        return calculate(objectsIterator, null);
    }

    public Map<O, Group<O>> calculate(Iterator<O> objectsIterator, Statistics stats) {
        Map<O, Group<O>> distribution = new HashMap<O, Group<O>>();
        for (; objectsIterator.hasNext(); ) {
            O object = objectsIterator.next();
            if (stats != null) {stats.addValue(getDoubleValue(object));}
            Group<O> group = distribution.get(object);
            if (group == null) {
                group = new Group<O>(object);
                distribution.put(object, group);
            }
            group.setNumber(group.getNumber() + 1);
        }
        return distribution;
    }

    public Map<O, Group<O>> calculate(Collection<O> objects, Statistics stats) {
        return calculate(objects.iterator(), stats);
    }

    public Map<O, Group<O>> calculate(Collection<O> objects) {
        return calculate(objects.iterator());
    }

    public double getDoubleValue(O object) {
        return 0;
    }

    public static <O extends Comparable> List<Group<O>> valueSortedGroups(Map<O, Group<O>> distribution) {
        return sortedGroups(distribution, new Comparator<Group<O>>() {
            public int compare(Group<O> group1, Group<O> group2) {
                return group1.getValue().compareTo(group2.getValue());
            }
        });
    }

    public static <O> List<Group<O>> numberSortedGroups(Map<O, Group<O>> distribution) {
        return sortedGroups(distribution, new Comparator<Group<O>>() {
            public int compare(Group<O> group1, Group<O> group2) {
                return group2.getNumber() - group1.getNumber();
            }
        });
    }

    public static <O> List<Group<O>> sortedGroups(Map<O, Group<O>> distribution, Comparator<Group<O>> comparator) {
        List<Group<O>> groups = new ArrayList<Group<O>>(distribution.values());
        Collections.sort(groups, comparator);
        return groups;
    }

    public static <O> void printGroups(List<Group<O>> groups) {
        for (Group group: groups) {
            System.out.println(group.getValue() + "\t" + group.getNumber());
        }
    }

    /*public static <V extends Number> void printGroups(List<Group<V>> groups, V min, V max, V step) {
        if (groups.size() >= 1) {
            boolean isLong = min instanceof Long;
            int minGroupIndex = minGroupIndex(groups, min);
            if (minGroupIndex >= 0) {
                int overMinNumbers= 0;
                for (ListIterator<Group<V>> li = groups.listIterator(); li.hasNext() && li.nextIndex() < minGroupIndex; ) {
                    Group<V> group = li.next();
                    overMinNumbers += group.getNumber();
                }
                groups.get(minGroupIndex).setNumber(groups.get(minGroupIndex).getNumber() + overMinNumbers);
                ArrayList<Group<V>> groupsList = new ArrayList<Group<V>>();
                for (ListIterator<Group<V>> li = groups.listIterator(minGroupIndex); li.hasNext(); ) {
                    groupsList.add(li.next());
                }
                groups = groupsList;
            } else {
                LinkedList<Group<V>> groupsList = new LinkedList<Group<V>>();
                for (double d = min.doubleValue(); d < groups.get(0).getValue().doubleValue(); d += step.doubleValue()) {
                    if (isLong) {
                        long l = (long)d;
                        System.out.println(l + "\t" + 0);
                    } else {
                        System.out.println(d + "\t" + 0);
                    }
                }
                groupsList.addAll(groups);
                groups = groupsList;
            }
            int maxGroupIndex = maxGroupIndex(groups, max);
            if (maxGroupIndex >= 0) {
                int overMaxNumbers = 0;
                for (ListIterator<Group<V>> li = groups.listIterator(maxGroupIndex + 1); li.hasNext(); ) {
                    Group<V> group = li.next();
                    overMaxNumbers += group.getNumber();
                    li.remove();
                }
                groups.get(maxGroupIndex).setNumber(groups.get(maxGroupIndex).getNumber() + overMaxNumbers);
                printGroups(groups);
            } else {
                printGroups(groups);
                for (double d = groups.get(groups.size() - 1).getValue().doubleValue() + step.doubleValue(); d <= max.doubleValue(); d += step.doubleValue()) {
                    if (isLong) {
                        long l = (long)d;
                        System.out.println(l + "\t" + 0);
                    } else {
                        System.out.println(d + "\t" + 0);
                    }
                }
            }
        }
    }*/


    protected static <V extends Number> int minGroupIndex(List<Group<V>> groups, V min) {
        if (groups.get(0).getValue().doubleValue() <= min.doubleValue()) {
            int index = 0;
            for (ListIterator<Group<V>> li = groups.listIterator(); li.hasNext(); index = li.nextIndex()) {
                Group<V> group = li.next();
                if (group.getValue().doubleValue() >= min.doubleValue()) {
                    break;
                }
            }
            return index;
        } else {
            return -1;
        }
    }

    protected static <V extends Number> int maxGroupIndex(List<Group<V>> groups, V max) {
        if (groups.get(groups.size() - 1).getValue().doubleValue() >= max.doubleValue()) {
            int index = 0;
            for (ListIterator<Group<V>> li = groups.listIterator(); li.hasNext(); index = li.nextIndex()) {
                Group<V> group = li.next();
                if (group.getValue().doubleValue() >= max.doubleValue()) {
                    break;
                }
            }
            return index;
        } else {
            return -1;
        }
    }

    public static final class Group<O> {

        private O value;
        private int number;

        public Group(O value) {
            this.value = value;
        }

        public O getValue() {
            return value;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

    }

}
