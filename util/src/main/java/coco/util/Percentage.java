package coco.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Percentage extends Number {
    private static NumberFormat percentFormatter = new DecimalFormat("0.00#%");

    private double value;

    public Percentage(double value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    public String toString() {
        return percentFormatter.format(value);
    }
}
