package com.hackust.taxi.taxihitchhike;

/**
 * Created by cameron on 4/17/2016.
 */
public class RouteInfo {
    private long time;
    private long distance;
    private double currentCost;
    private double costSaved;

    public RouteInfo(long s1, long d1, double d2, double d3)
    {
        time = s1;
        distance = d1;
        currentCost = d2;
        costSaved = d3;
    }

    public long getTime()
    {
        return time;
    }

    public long getDistance()
    {
        return distance;
    }

    public double getCurrentCost()
    {
        return currentCost;
    }

    public double getCostSaved()
    {
        return costSaved;
    }
}
