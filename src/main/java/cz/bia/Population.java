package cz.bia;


import cz.bia.model.IFunction;
import org.jzy3d.maths.Coord3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Population {

    private Coord3d[] population;
    private Random rand = new Random();
    private final double min;
    private final double max;
    private boolean discrete;
    private IFunction function;


    public Population(double min, double max, boolean discrete, IFunction function) {
        this.min = min;
        this.max = max;
        this.discrete = discrete;
        this.function = function;

    }
    public void randomPopulation(int countPopulation) {
        this.population = new Coord3d[countPopulation];
            for(int i = 0; i < countPopulation; i++) {

                double x = (max - min) * rand.nextDouble() + min ;
                double y = (max - min) * rand.nextDouble() + min ;
                double z = function.calculate(new double[]{x, y});
                if(discrete) {
                    population[i] = new Coord3d((int)x, (int)y, (int)z);
                }
                else
                    population[i] = new Coord3d(x, y, z);
            }
        }

    public Coord3d[] getPopulation() {

        return population;
    }

    public void setPopulation(Coord3d[] pop) {
        this.population = pop;
    }

    public void setOnePopulation(int index, Coord3d invidiual) {
        this.population[index] = invidiual;
    }
}
