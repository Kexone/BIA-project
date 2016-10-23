package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.*;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class TenthYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        double sum1 = 0;
        double sum2 = 0;
    for ( int i = 0; i < params.length; i++) {
        sum1 +=  pow(params[i],2);
        sum2 +=  cos(2 * PI * params[i]);
    }
        return (- 20 * exp( -0.2f * sqrt(sum1/params.length)) - exp(sum2/params.length) + 20 + exp(1));
    }

    @Override
    public float getMinRange() {
        return -30;
    }

    @Override
    public float getMaxRange() {
        return 30;
    }
}
