package cz.bia.algorithm;

import cz.bia.Population;
import cz.bia.model.IFunction;
import org.jzy3d.maths.Coord3d;

import static java.lang.Math.pow;

/**
 * Created by Jakub Sevcik on 29.11.2016.
 */
public class NaiveDirectedSearch extends Algorithm{

    public NaiveDirectedSearch(IFunction function, double max, double min, boolean isDiscrete) {
        this.function = function;
        this.min = min;
        this.max = max;
        this.discrete = isDiscrete;
    }

    @Override
    public Coord3d start(Population pop) {
        int stepCounter = 0;
        int failedDims = 0;
        double initialStep = 0.1;
        double discountFactor = 0.1;
        double desiredStepSize = 20.0;
        double stepSize;
        Coord3d[] population = pop.getPopulation().clone();
        do {
            stepSize = initialStep * pow(discountFactor, stepCounter);
            for (int i = 0; i < population.length; i++) {
                float[] individual = population[i].toArray();
                double[] dir = new double[individual.length - 1];
                for (int j = 0; j < individual.length - 1; j++) {
                    dir[j] = this.getDoubleRandom() * 2 - 1;
                    individual[j] += dir[j] * stepSize;
                }
                double newFitness = function.calculate(new double[]{individual[0], individual[1]});
                if (newFitness < population[i].z) {
                    population[i] = new Coord3d(individual[0], individual[1], newFitness);
                    if (isDiscrete()) {
                        population[i] = new Coord3d((int) individual[0], (int) individual[1], (int) newFitness);
                    }
                } else {
                    for (int j = 0; j < individual.length - 1; j++) {
                        individual[j] += -dir[j] * stepSize;
                    }
                    newFitness = function.calculate(new double[]{individual[0], individual[1]});
                    if (newFitness < population[i].z) {
                        population[i] = new Coord3d(individual[0], individual[1], newFitness);
                        if (isDiscrete()) {
                            population[i] = new Coord3d((int) individual[0], (int) individual[1], (int) newFitness);
                        }
                    } else {
                        failedDims++;
                    }
                    if (failedDims == population.length) {
                        stepCounter++;
                        failedDims = 0;
                    }
                }
            }
        }while(stepSize > desiredStepSize);
        pop.setPopulation(population);
        return this.getBest(pop.getPopulation());
    }
}
