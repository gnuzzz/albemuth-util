package ru.albemuth.util;

public class Statistics {

    private String name;
    private Listener listener;
    private int valuesNumber;
    private double average;
    private double average2;
    private double dispersion;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    private double sum = 0;

    public Statistics(String name) {
        this.name = name;
        this.listener = Listener.NOP;
    }

    public Statistics(String name, Listener listener) {
        this.name = name;
        this.listener = listener;
    }

    public Statistics(String name, int perNumber) {
        this(name, new PerNumberListener(perNumber) {

            protected void statisticsUpdated(Statistics stats) {
                System.out.println(stats.getName() + "[Average: " + stats.getAverage() + ", dispersion: " + stats.getDispersion() + "]");
            }

        });
    }

    public Statistics(String name, int perNumber, int period) {
        this(name, new PeriodPerNumberListener(perNumber, period) {

            protected void statisticsUpdated(Statistics stats) {
                System.out.println(stats.getName() + "[Average: " + stats.getAverage() + ", dispersion: " + stats.getDispersion() + "]");
            }

        });
    }

    public void init(Statistics statistics) {
        this.valuesNumber = statistics.valuesNumber;
        this.average = statistics.average;
        this.average2 = statistics.average2;
        this.dispersion = statistics.dispersion;
    }

    public String getName() {
        return name;
    }

    public int getValuesNumber() {
        return valuesNumber;
    }

    public double getAverage() {
        return average;
    }

    public double getDispersion() {
        return dispersion;
    }

    public double get3sigma() {
        return 3 * Math.sqrt(dispersion);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getSum() {
        return sum;
    }

    protected double calculateAverage(double average, double value, int valuesNumber) {
        return (value + valuesNumber * average)/(valuesNumber + 1);
    }

    public synchronized void addValue(double value) {
        average = calculateAverage(average, value, valuesNumber);
        average2 = calculateAverage(average2,  value * value, valuesNumber);
        dispersion = average2 - average * average;
        if (value < min) {
            min = value;
        }
        if (value > max) {
            max = value;
        }
        sum += value;
        valuesNumber++;
        listener.valueAdded(this);
    }

    public synchronized void clear() {
        average = 0;
        average2= 0;
        dispersion = 0;
        valuesNumber = 0;
    }

    public static abstract class Listener {

        public static final Listener NOP        = new Listener() {
            protected final void statisticsUpdated(Statistics stats) {/*do nothing*/}
        };

        protected void valueAdded(Statistics stats) {
            statisticsUpdated(stats);
        }

        protected abstract void statisticsUpdated(Statistics stats);

    }

    public static abstract class PerNumberListener extends Listener {

        private int perNumber;

        protected PerNumberListener(int perNumber) {
            this.perNumber = perNumber;
        }

        protected void valueAdded(Statistics stats) {
            if (stats.getValuesNumber() % perNumber == 0) {
                statisticsUpdated(stats);
            }
        }

    }

    public static abstract class PeriodPerNumberListener extends PerNumberListener {

        private int period;

        protected PeriodPerNumberListener(int perNumber, int period) {
            super(perNumber);
            this.period = period;
        }

        protected void valueAdded(Statistics stats) {
            super.valueAdded(stats);
            if (stats.getValuesNumber() % period == 0) {
                stats.clear();
            }
        }

    }

}
