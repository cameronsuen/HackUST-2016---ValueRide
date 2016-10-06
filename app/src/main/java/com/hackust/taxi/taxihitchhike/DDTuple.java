package com.hackust.taxi.taxihitchhike;

/**
 * Created by cameron on 4/17/2016.
 */
public class DDTuple {
    private double distance;
    private double duration;

    public DDTuple(double d1, double d2)
    {
        distance = d1;
        duration = d2;
    }

    public double getDistance() {
        return distance;
    }

    public double getDuration() {
        return duration;
    }

}
