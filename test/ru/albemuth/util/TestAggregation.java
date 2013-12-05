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

            final Convertor.IntConvertor<MyStruct> valueConvertor = new Convertor.IntConvertor<MyStruct>() {
                @Override
                public int intValue(MyStruct myStruct) {
                    return myStruct.value;
                }
            };

            Map<Integer, List<MyStruct>> groups = Aggregation.group(list).by(idConvertor);
            assertEquals(4, groups.values().size());

            Map<Integer, Integer> counts = Aggregation.distribution(groups);
            assertEquals(4, counts.get(1).intValue());
            assertEquals(3, counts.get(2).intValue());
            assertEquals(2, counts.get(3).intValue());
            assertEquals(1, counts.get(4).intValue());

            Map<Integer, T2<Integer, Integer>> sums = Aggregation.distribution(groups, new Convertor<List<MyStruct>, T2<Integer, Integer>>() {
                @Override
                public T2<Integer, Integer> map(List<MyStruct> myStructs) {
                    return new T2<Integer, Integer>(myStructs.isEmpty() ? null : myStructs.get(0).id, Aggregation.sum(myStructs).by(valueConvertor));
                }
            });
            assertEquals(10, sums.get(1).getV2().intValue());
            assertEquals(6, sums.get(2).getV2().intValue());
            assertEquals(3, sums.get(3).getV2().intValue());
            assertEquals(100, sums.get(4).getV2().intValue());


            final T2<Integer, Integer> minSum = Collections.min(sums.values(), new Comparator<T2<Integer, Integer>>() {
                @Override
                public int compare(T2<Integer, Integer> o1, T2<Integer, Integer> o2) {
                    return o1.getV2() - o2.getV2();
                }
            });
            assertEquals(3, minSum.getV2().intValue());

            Statistics stats = Aggregation.statistics(sums.values()).by(new Convertor.DoubleConvertor<T2<Integer, Integer>>() {
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
