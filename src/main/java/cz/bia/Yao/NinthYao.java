package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class NinthYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        double sum = 0;
        for (int i = 0; i < params.length; i++) {
            sum += (pow(params[i],2) - 10 * cos(2 * PI * params[i]) + 10 );
        }
        return sum;
    }

    @Override
    public float getMinRange() {
        return -8;
    }

    @Override
    public float getMaxRange() {
        return 8;
    }
}
