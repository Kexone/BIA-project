package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.pow;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class FifthYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        double sum = 0;
        for (int i = 0; i < params.length-1; i++) {
            sum += (100 * pow((params[i+1] - pow(params[i],2)),2)) + pow((params[i]+1),2);
        }
        return sum;
    }

    @Override
    public float getMinRange() {
        return -2.f;
    }

    @Override
    public float getMaxRange() {
        return 2.f;
    }
}
