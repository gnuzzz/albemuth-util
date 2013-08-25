package ru.albemuth.util.deprecated;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 21.12.2005
 * Time: 17:43:23
 * To change this template use Options | File Templates.
 */
public class CountStore {

    private String name;
    private int count;

    protected CountStore(String aName) {
        name = aName;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    protected synchronized void add(int aCount, int aPerNumber) {
        count += aCount;
        if (aPerNumber > 0 && count % aPerNumber == 0) {
            StatisticsStore.LOG.info(name + " count: " + count);
        }
    }

}
