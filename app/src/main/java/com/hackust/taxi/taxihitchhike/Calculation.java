package com.hackust.taxi.taxihitchhike;

/**
 * Created by cameron on 4/17/2016.
 */
public class Calculation {
    public static double fee_cal(double dis) {
        double distance = dis * 1000;
        double fee = 22;
        distance -= 2000;
        while (distance > 0) {
            distance -= 200;
            if (fee >= 78) {
                fee += 1;
            } else {
                fee += 1.6;
            }
        }
        return fee;
    }

    public static int Roundoff(float x) {
        int y = (int) (x + 0.5);
        return y;
    }

    /*
    * @float1: total distance of passenger A   (route 1 || S1-M-S2-K  &&  N-D)
    * @float2: total distance of passenger B   (route 2 || S2-K-N-D)
    * @float3: new total distance           (route 2+ route 3 || S1-M-S2-K-N-D)
    * @float4: joined price for A
    * @float5: joined price for B
    */
    public static double price_for_two(double route1, double route2,
                               double new_dis, double feeA, double feeB, boolean user) {  //0: requester(A) 1: searcher(B)

        double WAITING_COST = (1.6 * 2);

        double route3 = new_dis - route2;
        double alone_priceA = fee_cal(route1),
                alone_priceB = fee_cal(route2),
                join_price = fee_cal(route2 + route3);
        double average = join_price / new_dis;
        if (user) {
            feeA = (route1 - route2 / 2) * average;
            return feeA;
        } else {
            feeB = (route2 * 3 / 2 + route3 - route1) * average;
            return feeB;
        }


    }
}
