package ru.albemuth.util;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestAggregation {

    @Test
    public void test() {
        try {
            List<MyStruct> list = new ArrayList<MyStruct>();
            list.add(new MyStruct(1, 1));
            list.add(new MyStruct(1, 2));
            list.add(new MyStruct(1, 3));
            list.add(new MyStruct(1, 4));
            list.add(new MyStruct(2, 1));
            list.add(new MyStruct(2, 2));
            list.add(new MyStruct(2, 3));
            list.add(new MyStruct(3, 1));
            list.add(new MyStruct(3, 2));
            list.add(new MyStruct(4, 100));

            Convertor<MyStruct, Integer> idConvertor = new Convertor<MyStruct, Integer>() {
                @Override
                public Integer map(MyStruct myStruct) {
                    return myStruct.id;
                }
            };

            Convertor.IntConvertor<MyStruct> valueConvertor = new Convertor.IntConvertor<MyStruct>() {
                @Override
                public int intValue(MyStruct myStruct) {
                    return myStruct.value;
                }
            };

            Aggregation.Sum<Integer, MyStruct> aggregate = new Aggregation.Sum<Integer, MyStruct>(valueConvertor);

            Map<Integer, Aggregation.Sum<Integer, MyStruct>> groups = Aggregation.select(aggregate).from(list).orderBy(idConvertor);
            assertEquals(4, groups.values().size());
            assertEquals(6, groups.get(2).sum);
            assertEquals(3, groups.get(3).sum);
            assertEquals(100, groups.get(4).sum);

            final Aggregation.Sum<Integer, MyStruct> minAggregate = Collections.min(groups.values(), new Comparator<Aggregation.Sum<Integer, MyStruct>>() {
                @Override
                public int compare(Aggregation.Sum<Integer, MyStruct> o1, Aggregation.Sum<Integer, MyStruct> o2) {
                    return o1.sum - o2.sum;
                }
            });
            assertEquals(3, minAggregate.getSum());

            MyStruct minMyStruct = Collections.min(
                    new FilteredCollection<MyStruct>(list) {
                        @Override
                        public boolean accept(MyStruct myStruct) {
                            return myStruct.id == minAggregate.getId();
                        }
                    },
                    new Comparator<MyStruct>() {
                        @Override
                        public int compare(MyStruct o1, MyStruct o2) {
                            return o1.value - o2.value;
                        }
                    }
            );
            assertEquals(3, minMyStruct.id);
            assertEquals(1, minMyStruct.value);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    class MyStruct {
        int id;
        int value;

        MyStruct(int id, int value) {
            this.id = id;
            this.value = value;
        }
    }
}

/*class C1<K, V> {

}

class C2<K, V> {

}

class C3<C2<K, V>> extends C1<K, V> {

        }*/