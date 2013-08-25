package ru.albemuth.util.deprecated;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 21.12.2005
 * Time: 17:42:31
 * To change this template use Options | File Templates.
 */
public class TimeStore {

    private String name;
    private long time;
    private int count;

    protected TimeStore(String aName) {
        name = aName;
    }

    public String getName() {
        return name;
    }

    protected synchronized void add(long aTime, int aPerNumber) {
        time += aTime;
        count++;
        if (count % aPerNumber == 0) {
            StatisticsStore.LOG.info(name + " average process time: " + ((double)time/count));
            time = 0;
            count = 0;
        }
    }

}
