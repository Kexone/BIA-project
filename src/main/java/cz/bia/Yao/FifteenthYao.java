package cz.bia.Yao;

import cz.bia.model.IFunction;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class FifteenthYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        return 0;
    }

    @Override
    public float getMinRange() {
        return -5;
    }

    @Override
    public float getMaxRange() {
        return 5;
    }
}
