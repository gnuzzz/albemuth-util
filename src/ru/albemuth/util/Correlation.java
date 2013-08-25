package ru.albemuth.util;

public class Correlation {

    private String name;
    private Statistics xStatistics;
    private Statistics yStatistics;
    private Statistics xyStatistics;

    public Correlation(String name) {
        this.name = name;
        this.xStatistics = new Statistics("x");
        this.yStatistics = new Statistics("y");
        this.xyStatistics = new Statistics("xy");
    }

    public String getName() {
        return name;
    }

    public synchronized void addValues(double x, double y) {
        xStatistics.addValue(x);
        yStatistics.addValue(y);
        xyStatistics.addValue(x*y);
    }

    public synchronized void clear() {
        xStatistics.clear();
        yStatistics.clear();
        xyStatistics.clear();
    }

    public double correlation() {
        return (xyStatistics.getAverage() - xStatistics.getAverage() * yStatistics.getAverage())/Math.sqrt(xStatistics.getDispersion() * yStatistics.getDispersion());
    }

}
