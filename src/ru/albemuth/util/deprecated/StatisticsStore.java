/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 11.06.2004
 * Time: 17:08:06
 * To change this template use Options | File Templates.
 */
package ru.albemuth.util.deprecated;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Collection;

public class StatisticsStore {

    protected static final Logger LOG = Logger.getLogger(StatisticsStore.class);

    private static final StatisticsStore STORE = new StatisticsStore();

    private HashMap namedStore;

    private StatisticsStore() {
        namedStore = new HashMap();
    }

    public static StatisticsStore getInstance() {
        return STORE;
    }

    public void calculateStatistics(String aName, long aProcessTime, int aPerNumber) {
        TimeStore ns = (TimeStore)namedStore.get(aName);
        if (ns == null) {
            synchronized (this) {
                ns = (TimeStore)namedStore.get(aName);
                if (ns == null) {
                    ns = new TimeStore(aName);
                    namedStore.put(aName, ns);
                }
            }
        }
        ns.add(aProcessTime, aPerNumber);
    }

    public void calculateCountStatistics(String aName, int aCount, int aPerNumber) {
        CountStore cs = (CountStore)namedStore.get(aName);
        if (cs == null) {
            synchronized(this) {
                cs = (CountStore)namedStore.get(aName);
                if (cs == null) {
                    cs = new CountStore(aName);
                    namedStore.put(aName, cs);
                }
            }
        }
        cs.add(aCount, aPerNumber);
    }

    public Collection getStores() {
        return namedStore.values();
    }

    public void refresh() {
        namedStore.clear();
    }

}
