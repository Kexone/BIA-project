package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.pow;

/**
 * Created by Jakub Sevcik on 22.09.2016.
 */
public class FirstYao implements IFunction {

    @Override
    public double calculate(double[] params) {
            float sum = 0;
            for(int i = 0; i < params.length; i++) {
                sum+= pow(params[i],2);
            }
            return sum;
    }

    @Override
    public float getMinRange() {
        return -5.0f;
    }

    @Override
    public float getMaxRange() {
        return 5.0f;
    }
}
