package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.*;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class EighthYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        double sum = 0;
        for (int i = 0; i < params.length; i++) {
          sum += (-params[i]) * sin(sqrt(abs(params[i])));
        }
        return sum;
    }

    @Override
    public float getMinRange() {
        return -750;
    }

    @Override
    public float getMaxRange() {
        return 750;
    }
}
