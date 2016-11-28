package cz.bia.algorithm;

import cz.bia.Population;
import cz.bia.model.IFunction;
import org.jzy3d.maths.Coord3d;

/**
 * Created by Jakub Sevcik on 23.11.2016.
 */
public class SimulatingAnnealing extends Algorithm {

    private final double radius;
    private double temperature;
    private final double finalTemp;
    private final double beta;
    private Population pop;
    private Coord3d individual;
    public static boolean isCompleted;

    public SimulatingAnnealing(IFunction function, double max, double min, boolean isDiscrete, double radius, double temperature, double finalTemp, double beta) {
        this.function = function;
        this.min = min;
        this.max = max;
        this.discrete = isDiscrete;
        this.radius = radius;
        this.temperature = temperature;
        this.finalTemp = finalTemp;
        this.beta = beta;
    }

    @Override
    public Coord3d start(Population pop) {
        this.pop = pop;
        Coord3d currenctSolution = individual;
        int countPopulation = pop.getPopulation().length;
        for(int i = 0; i < countPopulation; i++) {
            generateNeighbours(countPopulation, individual);
            Coord3d newSolution = pop.getPopulation()[getIntegerRandom(countPopulation)];
            if(probabilityCheck(currenctSolution, newSolution) > getDoubleRandom()) {
                currenctSolution = newSolution;
            }
        }
        this.chill();
        isCompleted = isCompleted();
        return currenctSolution;
    }

    private void chill() {
        System.out.println("old temp: " + temperature);
        this.temperature = this.temperature / (1 + (this.beta * this.temperature));
    }



    private boolean isCompleted() {
        System.out.println("temp: " + temperature);
        return this.temperature <= this.finalTemp;
    }
    private double probabilityCheck(Coord3d currentSolution, Coord3d newSolution) {
        if(newSolution.z < currentSolution.z){
            return 1;
        }
        else
            return Math.exp(-((newSolution.z - currentSolution.z)) / this.temperature);
    }

    private void generateNeighbours(int countPopulation, Coord3d individual) {
        Coord3d[] population = new Coord3d[countPopulation];
        double minX = individual.x - radius;
        double maxX = individual.x + radius;
        double minY = individual.y - radius;
        double maxY = individual.y + radius;

        for(int i = 0; i < countPopulation; i++) {

            double x = (maxX - minX) * getDoubleRandom() + minX ;
            double y = (maxY - minY) * getDoubleRandom() + minY ;
            if(x < minX || x > maxX || y < minY || y > maxY ||
                    x < min || x > max || y < min || y > max) {
                i--;
                continue;
            }
            double z = function.calculate(new double[]{x, y});
            if(isDiscrete()) {
                population[i] = new Coord3d((int)x, (int)y, (int)z);
            }
            else
                population[i] = new Coord3d(x, y, z);
        }
        pop.setPopulation(population);
    }


}
