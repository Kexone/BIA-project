package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.abs;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class SecondYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        double sum = 0;
        double pi = 1;
        for (int i = 0; i < params.length; i++ ) {
            pi*= abs(params[i]);
            sum += abs(params[i]);
        }
        return sum + pi;
    }

    @Override
    public float getMinRange() {
        return -12.5f;
    }

    @Override
    public float getMaxRange() {
        return 12.5f;
    }
}
