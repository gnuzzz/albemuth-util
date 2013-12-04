package ru.albemuth.util;

import java.util.*;

public class Aggregation<Value> {

    protected Iterator<Value> iterator;

    public Aggregation(Iterator<Value> iterator) {
        this.iterator = iterator;
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

    public static <Id, Value> Map<Id, List<Value>> groups(Map<Id, Group<Id, Value>> groups) {
        Map<Id, List<Value>> valuesGroups = new HashMap<Id, List<Value>>(groups.size());
        for (Group<Id, Value> group: groups.values()) {
            valuesGroups.put(group.getId(), group.getValues());
        }
        return valuesGroups;
    }

    public static <Value> Aggregation<Value> group(Iterator<Value> iterator) {
        return new Aggregation<Value>(iterator);
    }

    public static <Value> Aggregation<Value> group(Collection<Value> values) {
        return new Aggregation<Value>(values.iterator());
    }

    public static <Value> Aggregation<Value> group(Value... values) {
        return new Aggregation<Value>(Arrays.asList(values).iterator());
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

    public static <Value> SumIntAggregation<Value> sum(Iterator<Value> iterator) {
        return new SumIntAggregation<Value>(iterator);
    }

    public static <Value> SumIntAggregation<Value> sum(Collection<Value> values) {
        return new SumIntAggregation<Value>(values.iterator());
    }

    public static <Value> SumIntAggregation<Value> sum(Value... values) {
        return new SumIntAggregation<Value>(Arrays.asList(values).iterator());
    }

    public static <Value> SumLongAggregation<Value> sumLong(Iterator<Value> iterator) {
        return new SumLongAggregation<Value>(iterator);
    }

    public static <Value> SumLongAggregation<Value> sumLong(Collection<Value> values) {
        return new SumLongAggregation<Value>(values.iterator());
    }

    public static <Value> SumLongAggregation<Value> sumLong(Value... values) {
        return new SumLongAggregation<Value>(Arrays.asList(values).iterator());
    }

    public static <Value> SumDoubleAggregation<Value> sumDouble(Iterator<Value> iterator) {
        return new SumDoubleAggregation<Value>(iterator);
    }

    public static <Value> SumDoubleAggregation<Value> sumDouble(Convertor.DoubleConvertor<Value> convertor, Collection<Value> values) {
        return new SumDoubleAggregation<Value>(values.iterator());
    }

    public static <Value> SumDoubleAggregation<Value> sumDouble(Convertor.DoubleConvertor<Value> convertor, Value... values) {
        return new SumDoubleAggregation<Value>(Arrays.asList(values).iterator());
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

    public static class SumIntAggregation<Value> extends Aggregation<Value> {

        public SumIntAggregation(Iterator<Value> iterator) {
            super(iterator);
        }

        public int by(Convertor.IntConvertor<Value> convertor) {
            int ret = 0;
            for (; iterator.hasNext(); ) {
                ret += convertor.intValue(iterator.next());
            }
            return ret;
        }
    }

    public static class SumLongAggregation<Value> extends Aggregation<Value> {

        public SumLongAggregation(Iterator<Value> iterator) {
            super(iterator);
        }

        public long by(Convertor.LongConvertor<Value> convertor) {
            long ret = 0;
            for (; iterator.hasNext(); ) {
                ret += convertor.longValue(iterator.next());
            }
            return ret;
        }
    }

    public static class SumDoubleAggregation<Value> extends Aggregation<Value> {

        public SumDoubleAggregation(Iterator<Value> iterator) {
            super(iterator);
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
