package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class ThirteenthYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        double sum1 = 0;
        double sum2 = 0;
        double part1 = 0;
        for (int i = 0; i < params.length-1; i++) {
            sum1 += pow(params[i]-1,2);
            sum1 *=  1 + pow(sin(3 * PI * params[i+1]),2);
            part1 =  pow(sin(3 * PI * params[i+1]),2);
        }
        for (int i = 0; i < params.length; i++) {
          double u = CalculateU(params[i]);
            sum2 += u;
        }
        return 0.1f * part1 + sum1 + (params.length - 1) * ( 1 + pow(sin(2 * PI * params.length),2)) + sum2;
    }

    @Override
    public float getMinRange() {
        return -5;
    }

    @Override
    public float getMaxRange() {
        return 5;
    }

    private double CalculateU(double param) {
        if (param >  5) {
            return 100 * pow((param - 5), 4);
        }
        else if ( -5 < param && param > 5) {
            return 0;
        }
        else if ( param < -5) {
            return 100 * pow((-param - 5), 4);
        }
        return 0;
    }
}
