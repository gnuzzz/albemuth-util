package ru.albemuth.util;

import java.util.*;

public class Aggregation<Id, Value, Ag extends Aggregation.Aggregate<Id, Value>> {

    private Ag aggregate;
    private Iterator<Value> it;

    public Aggregation(Ag aggregate) {
        this.aggregate = aggregate;
    }

    protected void setIt(Iterator<Value> it) {
        this.it = it;
    }

    public Aggregation(Ag aggregate, Iterator<Value> it) {
        this.aggregate = aggregate;
        this.it = it;
    }

    public Map<Id, Ag> by(Map<Id, Ag> groups, Convertor<Value, Id> convertor) {
        for (; it.hasNext(); ) {
            Value value = it.next();
            Id id = convertor.map(value);
            Ag group = groups.get(id);
            if (group == null) {
                group = (Ag)aggregate.clone();
                group.setId(id);
                groups.put(id, group);
            }
            group.process(value);
        }
        return groups;
    }

    public Map<Id, Ag> orderBy(Convertor<Value, Id> convertor) {
        return by(new HashMap<Id, Ag>(), convertor);
    }

    public Aggregation<Id, Value, Ag> from(Collection<Value> values) {
        setIt(values.iterator());
        return this;
    }

    public Aggregation<Id, Value, Ag> from(Iterator<Value> valuesIterator) {
        setIt(valuesIterator);
        return this;
    }

    public Aggregation<Id, Value, Ag> from(Value... values) {
        setIt(Arrays.asList(values).iterator());
        return this;
    }

    public static <Id, Value, Ag extends Aggregate<Id, Value>> Aggregation<Id, Value, Ag> select(Ag aggregate) {
        return new Aggregation<Id, Value, Ag>(aggregate);
    }

    public static abstract class Aggregate<Id, Value> implements Cloneable {

        protected Id id;

        public Id getId() {
            return id;
        }

        public void setId(Id id) {
            this.id = id;
        }

        public abstract void process(Value value);

        @Override
        public Aggregate<Id, Value> clone() {
            try {
                return  (Aggregate<Id, Value>)super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Can't clone aggregate " + getClass().getName(), e);
            }
        }
    }

    public static class Count<Id, Value> extends Aggregate<Id, Value> {

        protected int count;

        public int getCount() {
            return count;
        }

        @Override
        public void process(Value value) {
            count++;
        }

    }

    public abstract static class Statistics<Id, Value> extends Aggregate<Id, Value> {

        protected ru.albemuth.util.Statistics stats;

        public ru.albemuth.util.Statistics getStats() {
            return stats;
        }

        @Override
        public void process(Value value) {
            stats.addValue(doubleValue(value));
        }

        public abstract double doubleValue(Value value);
    }

    public static class SumByte<Id, Value> extends Aggregate<Id, Value> {

        protected Convertor.ByteConvertor<Value> convertor;
        protected byte sum;

        public SumByte(Convertor.ByteConvertor<Value> convertor) {
            this.convertor = convertor;
        }

        public byte getSum() {
            return sum;
        }

        @Override
        public void process(Value value) {
            sum += convertor.byteValue(value);
        }

    }

    public static class SumShort<Id, Value> extends Aggregate<Id, Value> {

        protected Convertor.ShortConvertor<Value> convertor;
        protected short sum;

        public SumShort(Convertor.ShortConvertor<Value> convertor) {
            this.convertor = convertor;
        }

        public short getSum() {
            return sum;
        }

        @Override
        public void process(Value value) {
            sum += convertor.shortValue(value);
        }

    }

    public static class Sum<Id, Value> extends Aggregate<Id, Value> {

        protected Convertor.IntConvertor<Value> convertor;
        protected int sum;

        public Sum(Convertor.IntConvertor<Value> convertor) {
            this.convertor = convertor;
        }

        public int getSum() {
            return sum;
        }

        @Override
        public void process(Value value) {
            sum += convertor.intValue(value);
        }

    }

    public static class SumLong<Id, Value> extends Aggregate<Id, Value> {

        protected Convertor.LongConvertor<Value> convertor;
        protected long sum;

        public SumLong(Convertor.LongConvertor<Value> convertor) {
            this.convertor = convertor;
        }

        public long getSum() {
            return sum;
        }

        @Override
        public void process(Value value) {
            sum += convertor.longValue(value);
        }

    }

    public static class SumFloat<Id, Value> extends Aggregate<Id, Value> {

        protected Convertor.FloatConvertor<Value> convertor;
        protected float sum;

        public SumFloat(Convertor.FloatConvertor<Value> convertor) {
            this.convertor = convertor;
        }

        public float getSum() {
            return sum;
        }

        @Override
        public void process(Value value) {
            sum += convertor.floatValue(value);
        }

    }

    public static class SumDouble<Id, Value> extends Aggregate<Id, Value> {

        protected Convertor.DoubleConvertor<Value> convertor;
        protected double sum;

        public SumDouble(Convertor.DoubleConvertor<Value> convertor) {
            this.convertor = convertor;
        }

        public double getSum() {
            return sum;
        }

        @Override
        public void process(Value value) {
            sum += convertor.doubleValue(value);
        }

    }

}
