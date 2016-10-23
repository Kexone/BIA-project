package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.pow;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class SixteenthYao implements IFunction {
    @Override
    public double calculate(double[] params) {

        return 4* pow(params[0],2) - 2.1f * pow(params[0],4) + 0.333f * pow(params[0],6)
                + params[0] * params[1] - 4 * pow(params[1],2) + 4 * pow(params[1],4);
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
