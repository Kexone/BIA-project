package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class TwelfthYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        double sum1 = 0;
        double sum2 = 0;
        double yD = 1 + 0.25f * (params[params.length-1] + 1);
        for (int i = 0; i < params.length; i++) {
            double y = 1 + 0.25f * (params[i] + 1);
            double u = CalculateU(params[i]);
            sum1 +=  pow((y-1),2) * 10 * sin(pow(PI * y,2)) * (1 + pow(sin(3 * PI * params[i]),2)) + pow(yD + 1,2);
            sum2 += u;
        }

        return (PI/params.length)  + sum1 + sum2 ;

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
        if (param >  10) {
            return 100 * pow((param - 10), 4);
        }
        else if ( -10 < param && param > 10) {
            return 0;
        }
        else if ( param < -10) {
            return 100 * pow((-param - 10), 4);
        }
        return 0;
    }
}
