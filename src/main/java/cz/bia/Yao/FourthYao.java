package cz.bia.Yao;

import cz.bia.model.IFunction;

import java.util.Arrays;

import static java.lang.Math.abs;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class FourthYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        double max = abs(params[0]);
        double[] param = new double[params.length];
        for (int i = 0; i < params.length; i++) {
            if (i > 0 && i < params.length) {
                if (max < abs(param[i])) {
                    max = abs(param[i]);
                }
            }
        }
        return max;
    }

    @Override
    public float getMinRange() {
        return -100;
    }

    @Override
    public float getMaxRange() {
        return 100;
    }
}
