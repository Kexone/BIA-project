package cz.bia;


import cz.bia.model.IFunction;
import org.jzy3d.maths.Coord3d;

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
    private double min;
    private double max;
    private boolean discrete;
    private IFunction function;
    private Coord3d[] newPopulation;

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

    public Coord3d generateInvidual() {
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
    public Coord3d soma(){
        return Arrays.stream(this.population).min(Comparator.comparing(c -> c.z)).orElseThrow(() -> new RuntimeException("there is no best"));
    }

    private boolean checkNeighbours(Coord3d x) {
       // Coord3d minimum = Coord3d(x.x);
        return false;
    }
}
