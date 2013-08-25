package ru.albemuth.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class TestDistribution {

    @Test
    public void testPrintGroupsExt() {
        try {
            /*List<Distribution.Group<Long>> groups = groups();

            TestCase.assertEquals(-1, Distribution.minGroupIndex(groups, 0L));
            TestCase.assertEquals(-1, Distribution.minGroupIndex(groups, 25L));
            TestCase.assertEquals(0, Distribution.minGroupIndex(groups, 50L));
            TestCase.assertEquals(1, Distribution.minGroupIndex(groups, 75L));
            
            TestCase.assertEquals(3, Distribution.maxGroupIndex(groups, 125L));
            TestCase.assertEquals(4, Distribution.maxGroupIndex(groups, 150L));
            TestCase.assertEquals(-1, Distribution.maxGroupIndex(groups, 175L));
            TestCase.assertEquals(-1, Distribution.maxGroupIndex(groups, 200L));

            Distribution.printGroups(groups(), 0L, 150L, 25L);
            System.out.println();
            Distribution.printGroups(groups(), 25L, 150L, 25L);
            System.out.println();
            Distribution.printGroups(groups(), 50L, 150L, 25L);
            System.out.println();
            Distribution.printGroups(groups(), 75L, 150L, 25L);
            System.out.println();
            System.out.println();
            System.out.println();
            Distribution.printGroups(groups(), 50L, 125L, 25L);
            System.out.println();
            Distribution.printGroups(groups(), 50L, 150L, 25L);
            System.out.println();
            Distribution.printGroups(groups(), 50L, 175L, 25L);
            System.out.println();
            Distribution.printGroups(groups(), 50L, 200L, 25L);
            System.out.println(); */

        } catch (Exception e) {
            e.printStackTrace();
            TestCase.fail();
        }
    }

    private List<Distribution.Group<Long>> groups() {
        List<Distribution.Group<Long>> groups = new ArrayList<Distribution.Group<Long>>();
        Distribution.Group<Long> group;
        group = new Distribution.Group<Long>(50L);
        group.setNumber(1);
        groups.add(group);
        group = new Distribution.Group<Long>(75L);
        group.setNumber(1);
        groups.add(group);
        group = new Distribution.Group<Long>(100L);
        group.setNumber(1);
        groups.add(group);
        group = new Distribution.Group<Long>(125L);
        group.setNumber(1);
        groups.add(group);
        group = new Distribution.Group<Long>(150L);
        group.setNumber(1);
        groups.add(group);
        return groups;
    }

}
