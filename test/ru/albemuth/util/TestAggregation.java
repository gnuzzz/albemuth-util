package ru.albemuth.util;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

            Map<Integer, List<MyStruct>> groups = Aggregation.group(list).by(idConvertor);
            assertEquals(4, groups.values().size());

            assertEquals(10, Aggregation.sum(groups.get(1)).by(valueConvertor));
            assertEquals(6, Aggregation.sum(groups.get(2)).by(valueConvertor));
            assertEquals(3, Aggregation.sum(groups.get(3)).by(valueConvertor));
            assertEquals(100, Aggregation.sum(groups.get(4)).by(valueConvertor));

            List<T2<Integer, Integer>> sums = new ArrayList<T2<Integer, Integer>>(groups.size());
            for (Integer id: groups.keySet()) {
                sums.add(new T2<Integer, Integer>(id, Aggregation.sum(groups.get(id)).by(valueConvertor)));
            }
            final T2<Integer, Integer> minSum = Collections.min(sums, new Comparator<T2<Integer, Integer>>() {
                @Override
                public int compare(T2<Integer, Integer> o1, T2<Integer, Integer> o2) {
                    return o1.getV2() - o2.getV2();
                }
            });
            assertEquals(3, minSum.getV2().intValue());

            Statistics stats = Aggregation.statistics(sums).by(new Convertor.DoubleConvertor<T2<Integer, Integer>>() {
                @Override
                public double doubleValue(T2<Integer, Integer> sum) {
                    return sum.v2;
                }
            });
            assertEquals(3, stats.getMin(), 0);
            assertEquals(100, stats.getMax(), 0);

            Iterator<MyStruct> it = new FilteredCollection<MyStruct>(list) {
                @Override
                public boolean accept(MyStruct myStruct) {
                    return myStruct.id == minSum.getV1();
                }
            }.iterator();
            MyStruct minMyStruct = it.hasNext() ? it.next() : null;
            assertNotNull(minMyStruct);

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
