package cz.bia.Yao;

import cz.bia.model.IFunction;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class ThirdYao implements IFunction{
    @Override
    public double calculate(double[] params) {
        double sum = 0;
        for(int i = 0; i < params.length; i++) {
            for (int j = 0; j <i; j++) {
                sum += params[j];
            }
        }
        return sum;
    }

    @Override
    public float getMinRange() {
        return -200;
    }

    @Override
    public float getMaxRange() {
        return 200;
    }
}
