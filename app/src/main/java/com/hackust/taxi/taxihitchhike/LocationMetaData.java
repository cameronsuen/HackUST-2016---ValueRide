package com.hackust.taxi.taxihitchhike;

/**
 * Created by cameron on 4/16/2016.
 */
public class LocationMetaData {
    private String id;
    private String result;

    public LocationMetaData(String s1, String s2)
    {
        id = s1;
        result = s2;
    }

    public String getID() { return id; }
    public String getResult() { return result; }


}
