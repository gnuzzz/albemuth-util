package ru.albemuth.util;

import java.util.*;

public class Aggregation<Value> {

    protected Iterator<Value> iterator;

    public Aggregation(Iterator<Value> iterator) {
        this.iterator = iterator;
    }

    public static <Id, Value> Map<Id, List<Value>> groups(Map<Id, Group<Id, Value>> groups) {
        Map<Id, List<Value>> valuesGroups = new HashMap<Id, List<Value>>(groups.size());
        for (Group<Id, Value> group: groups.values()) {
            valuesGroups.put(group.getId(), group.getValues());
        }
        return valuesGroups;
    }

    public static <Id, Value> Map<Id, Integer> distribution(Map<Id, List<Value>> groups) {
        return distribution(groups, new Convertor<List<Value>, Integer>() {
            @Override
            public Integer map(List<Value> values) {
                return values.size();
            }
        });
    }

    public static <Id, Value, GroupValue> Map<Id, GroupValue> distribution(Map<Id, List<Value>> groups, Convertor<List<Value>, GroupValue> convertor) {
        Map<Id, GroupValue> distribution = new HashMap<Id, GroupValue>(groups.size());
        for (Id id: groups.keySet()) {
            distribution.put(id, convertor.map(groups.get(id)));
        }
        return distribution;
    }

    public static <Value> GroupAggregation<Value> group(Iterator<Value> iterator) {
        return new GroupAggregation<Value>(iterator);
    }

    public static <Value> GroupAggregation<Value> group(Collection<Value> values) {
        return new GroupAggregation<Value>(values.iterator());
    }

    public static <Value> GroupAggregation<Value> group(Value... values) {
        return new GroupAggregation<Value>(Arrays.asList(values).iterator());
    }

    public static <Value> int count(Iterator<Value> iterator) {
        int counter = 0;
        for (; iterator.hasNext(); ) {counter++;}
        return counter;
    }

    public static <Value> int count(Collection<Value> values) {
        return values.size();
    }

    public static <Value> int count(Value... values) {
        return values.length;
    }

    public static <Value> SumAggregation<Value> sum(Iterator<Value> iterator) {
        return new SumAggregation<Value>(iterator);
    }

    public static <Value> SumAggregation<Value> sum(Collection<Value> values) {
        return new SumAggregation<Value>(values.iterator());
    }

    public static <Value> SumAggregation<Value> sum(Value... values) {
        return new SumAggregation<Value>(Arrays.asList(values).iterator());
    }

    public static <Value> StatisticsAggregation<Value> statistics(Iterator<Value> iterator) {
        return new StatisticsAggregation<Value>(iterator);
    }

    public static <Value> StatisticsAggregation<Value> statistics(Collection<Value> values) {
        return statistics(values.iterator());
    }

    public static <Value> StatisticsAggregation<Value> statistics(Value... values) {
        return statistics(Arrays.asList(values));
    }

    public static class GroupAggregation<Value> extends Aggregation<Value> {

        public GroupAggregation(Iterator<Value> iterator) {
            super(iterator);
        }

        public <Id> Map<Id, Group<Id, Value>> by(Map<Id, Group<Id, Value>> groups, Convertor<Value, Id> convertor) {
            for (; iterator.hasNext(); ) {
                Value value = iterator.next();
                Id id = convertor.map(value);
                Group<Id, Value> group = groups.get(id);
                if (group == null) {
                    group = new Group<Id, Value>(id);
                    groups.put(id, group);
                }
                group.process(value);
            }
            return groups;
        }

        public <Id> Map<Id, List<Value>> by(Convertor<Value, Id> convertor) {
            return groups(by(new HashMap<Id, Group<Id, Value>>(), convertor));
        }

    }

    public static class SumAggregation<Value> extends Aggregation<Value> {

        public SumAggregation(Iterator<Value> iterator) {
            super(iterator);
        }

        public byte by(Convertor.ByteConvertor<Value> convertor) {
            byte ret = 0;
            for (; iterator.hasNext(); ) {
                ret += convertor.byteValue(iterator.next());
            }
            return ret;
        }

        public short by(Convertor.ShortConvertor<Value> convertor) {
            short ret = 0;
            for (; iterator.hasNext(); ) {
                ret += convertor.shortValue(iterator.next());
            }
            return ret;
        }

        public int by(Convertor.IntConvertor<Value> convertor) {
            int ret = 0;
            for (; iterator.hasNext(); ) {
                ret += convertor.intValue(iterator.next());
            }
            return ret;
        }

        public long by(Convertor.LongConvertor<Value> convertor) {
            long ret = 0;
            for (; iterator.hasNext(); ) {
                ret += convertor.longValue(iterator.next());
            }
            return ret;
        }

        public float by(Convertor.FloatConvertor<Value> convertor) {
            float ret = 0;
            for (; iterator.hasNext(); ) {
                ret += convertor.floatValue(iterator.next());
            }
            return ret;
        }

        public double by(Convertor.DoubleConvertor<Value> convertor) {
            double ret = 0;
            for (; iterator.hasNext(); ) {
                ret += convertor.doubleValue(iterator.next());
            }
            return ret;
        }
    }

    public static class StatisticsAggregation<Value> extends Aggregation<Value> {

        public StatisticsAggregation(Iterator<Value> iterator) {
            super(iterator);
        }

        public Statistics by(Convertor.DoubleConvertor<Value> convertor) {
            Statistics stats = new Statistics(getClass().getName());
            for (; iterator.hasNext(); ) {
                stats.addValue(convertor.doubleValue(iterator.next()));
            }
            return stats;
        }
    }

    public static class Group<Id, Value> extends T2<Id, List<Value>>{

        public Group(Id id) {
            super(id, new ArrayList<Value>());
        }

        public Id getId() {
            return v1;
        }

        public List<Value> getValues() {
            return v2;
        }

        public void process(Value value) {
            getValues().add(value);
        }

    }

}
