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
    private final double fOrFinalTemp;
    private final double crOrRadius;
    private double temperature;
    private final double beta = 0.3;
    private final double min;
    private final double max;
    private boolean discrete;
    private IFunction function;
    private Coord3d[] newPopulation;
    private final double  step = 0.1;
    private final double pathLength = 3.0;
    private final double PRT = 0.3;


    public Population(double min, double max,double F, double CR, double temp, boolean discrete, IFunction function) {
        this.min = min;
        this.max = max;
        this.discrete = discrete;
        this.function = function;
        this.fOrFinalTemp = F;
        this.crOrRadius = CR;
        this.temperature = temp;

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
    public Coord3d[] getMutation() {

        return newPopulation;
    }

    public Coord3d getBest(Coord3d[] pop){
        return Arrays.stream(pop).min(Comparator.comparing(c -> c.z)).orElseThrow(() -> new RuntimeException("there is no best"));
    }

    public Coord3d getWorst(Coord3d[] pop){
        return Arrays.stream(pop).max(Comparator.comparing(c -> c.z)).orElseThrow(() -> new RuntimeException("there is no worst"));
    }

    public Coord3d differencial() {
        newPopulation = new Coord3d[population.length];
        Coord3d noise = new Coord3d();
        for (int i = 0; i < population.length; i++) {
            newPopulation[i] = new Coord3d();
            Coord3d[] solution =  {population[rand.nextInt(population.length)], population[rand.nextInt(population.length)], population[rand.nextInt(population.length)]};
            noise.x = (float) (solution[0].x +  fOrFinalTemp * (solution[1].x - solution[2].x));
            noise.y = (float) (solution[0].y +  fOrFinalTemp * (solution[1].y - solution[2].y));

                double random = rand.nextDouble();
                if(random < crOrRadius) {
                    newPopulation[i].x = discrete ? (int) noise.x : noise.x;
                }
                else if( random >= crOrRadius) {
                    newPopulation[i].x = discrete ? (int) population[i].x : population[i].x;
                }
                random = rand.nextDouble();
                if(random < crOrRadius) {
                    newPopulation[i].y = discrete ? (int) noise.y : noise.y;
                }
                else if( random >= crOrRadius) {
                    newPopulation[i].y = discrete ? (int) population[i].y : population[i].y;
                }
            if(discrete) {
                newPopulation[i].z = (int) function.calculate(new double[]{newPopulation[i].x, newPopulation[i].y});
            }
            else {
                newPopulation[i].z = (float) function.calculate(new double[]{newPopulation[i].x, newPopulation[i].y});
            }
            if(newPopulation[i].x < min || newPopulation[i].x > max || newPopulation[i].y < min || newPopulation[i].y > max ) {
                i--;
                continue;
            }
            if(population[i].z < newPopulation[i].z) {
                newPopulation[i] = population[i];
            }
          //  System.out.println("Old X:" + population[i].x +" Y:" +population[i].y +" Z:" +population[i].z );
          //  System.out.println("New X:" + newPopulation[i].x +" Y:" + newPopulation[i].y +" Z:" + newPopulation[i].z );
        }
        this.population = this.newPopulation;
        return this.getBest(population);
    }

    public Coord3d annealing(int countPopulation, Coord3d invidual){

        Coord3d currenctSolution = invidual;
        for(int i = 0; i < countPopulation; i++) {
            generateNeighbours(countPopulation, invidual);
            Coord3d newSolution = population[rand.nextInt(countPopulation)];
            if(probabilityCheck(currenctSolution, newSolution) > rand.nextDouble()) {
                currenctSolution = newSolution;
            }
        }
        this.chill();
        return currenctSolution;
    }

    private double probabilityCheck(Coord3d currentSolution, Coord3d newSolution) {
        if(newSolution.z < currentSolution.z){
            return 1;
        }
        else
            return Math.exp(-((newSolution.z - currentSolution.z)) / this.temperature);
    }

    private void generateNeighbours(int countPopulation, Coord3d invidual) {
        population = new Coord3d[countPopulation];
        double minX = invidual.x - crOrRadius;
        double maxX = invidual.x + crOrRadius;
        double minY = invidual.y - crOrRadius;
        double maxY = invidual.y + crOrRadius;

        for(int i = 0; i < countPopulation; i++) {

            double x = (maxX - minX) * rand.nextDouble() + minX ;
            double y = (maxY - minY) * rand.nextDouble() + minY ;
            if(x < minX || x > maxX || y < minY || y > maxY ||
                    x < min || x > max || y < min || y > max) {
                i--;
                continue;
            }
            double z = function.calculate(new double[]{x, y});
            if(discrete) {
                population[i] = new Coord3d((int)x, (int)y, (int)z);
            }
            else
                population[i] = new Coord3d(x, y, z);
        }
    }

    public Coord3d generateIndividual() {
        double x = (max - min) * rand.nextDouble() + min ;
        double y = (max - min) * rand.nextDouble() + min ;
        double z = function.calculate(new double[]{x, y});
        if(discrete) {
            return new Coord3d((int)x, (int)y, (int)z);
        }
        else
            return new Coord3d(x, y, z);

    }

    private void chill() {
        System.out.println("old temp: " + temperature);
        this.temperature = this.temperature / (1 + (this.beta * this.temperature));
    }


    public boolean isCompleted() {
        System.out.println("temp: " + temperature);
        return this.temperature <= this.fOrFinalTemp;
    }
    public Coord3d soma(float[] individual, Coord3d king, int individualIndex){
        Coord3d newBest = new Coord3d();
        Coord3d bestJump = new Coord3d(individual[0], individual[1], individual[2]);
        double[] kingParameters = {king.x, king.y, king.z} ;
        final float[] startParams = individual.clone();
         float[] params = individual.clone();
        for(double t = step; t <= pathLength; t+=step) {
            int[] prtVector = genPTRVector();
            for(int i = 0; i < population[0].toArray().length -1 ; i++) {
                params[i] = (float) ( startParams[i] +  ( kingParameters[i] - startParams[i]) * t * prtVector[i]);
                if(discrete){
                    params[i] = (int) params[i];
                }
            }
            params[2] = (float) function.calculate(new double[]{params[0], params[1]});
            newBest = new Coord3d(params[0], params[1], params[2]);
            if(newBest.z < bestJump.z) {
                bestJump  = newBest;
            }
        }
        while(bestJump.x > max) {
            bestJump.x = (float) (bestJump.x - (max - min));
        }
        while(bestJump.y > max) {
            bestJump.y = (float) (bestJump.y - (max - min));
        }
        while(bestJump.x < min) {
            bestJump.x = (float) (bestJump.x + (max - min));
        }
        while(bestJump.y < min) {
            bestJump.y = (float) (bestJump.y + (max - min));
        }
        bestJump.z = (float) function.calculate(new double[]{bestJump.x, bestJump.y});
        System.out.println("OLD X:" + startParams[0] +" Y:" + startParams[1] +" Z:" + startParams[2] );
        System.out.println("New X:" + newBest.x +" Y:" + newBest.y +" Z:" + newBest.z );
        population[individualIndex] = bestJump;
        return bestJump;
    }

    private int[] genPTRVector() {
        int[] prt = new int[population.length];
        for(int i = 0; i < prt.length; i++) {
            prt[i] = rand.nextDouble() < this.PRT ? 1 : 0;
        }
        return prt;
    }

    private boolean checkNeighbours(Coord3d x) {
       // Coord3d minimum = Coord3d(x.x);
        return false;
    }
}
