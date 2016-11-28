package cz.bia.algorithm;

import cz.bia.Population;
import cz.bia.model.IFunction;
import org.jzy3d.maths.Coord3d;

/**
 * Created by Jakub Sevcik on 23.11.2016.
 */
public class SomaAllToOne extends Algorithm {

    private final double PRT;
    private final double pathLength;
    private final double step;
    private final double minDiv;
    private Population pop;
    public static boolean toClose;


    public SomaAllToOne(IFunction function, double max, double min, boolean isDiscrete, double PRT, double step, double pathLength,double minDiv){
        this.function = function;
        this.min = min;
        this.max = max;
        this.discrete = isDiscrete;
        this.PRT = PRT;
        this.pathLength = pathLength;
        this.step = step;
        this.minDiv = minDiv;
    }

    @Override
    public Coord3d start(Population pop) {
        this.pop = pop;
        Coord3d newBest = new Coord3d();
        for(int individualIndex = 0; individualIndex < pop.getPopulation().length; individualIndex++) {
            float[] individ = pop.getPopulation()[individualIndex].toArray();
            Coord3d bestJump = new Coord3d(individ[0], individ[1], individ[2]);
            double[] kingParameters = {individual.x, individual.y, individual.z};
            final float[] startParams = individ.clone();
            float[] params = individ.clone();
            for (double t = step; t <= pathLength; t += step) {
                int[] prtVector = genPTRVector();
                for (int i = 0; i < pop.getPopulation()[0].toArray().length - 1; i++) {
                    params[i] = (float) (startParams[i] + (kingParameters[i] - startParams[i]) * t * prtVector[i]);
                    if (isDiscrete()) {
                        params[i] = (int) params[i];
                    }
                }
                params[2] = (float) function.calculate(new double[]{params[0], params[1]});
                newBest = new Coord3d(params[0], params[1], params[2]);
                if (newBest.z < bestJump.z) {
                    bestJump = newBest;
                }
            }
            while (bestJump.x > max) {
                bestJump.x = (float) (bestJump.x - (max - min));
            }
            while (bestJump.y > max) {
                bestJump.y = (float) (bestJump.y - (max - min));
            }
            while (bestJump.x < min) {
                bestJump.x = (float) (bestJump.x + (max - min));
            }
            while (bestJump.y < min) {
                bestJump.y = (float) (bestJump.y + (max - min));
            }
            bestJump.z = (float) function.calculate(new double[]{bestJump.x, bestJump.y});
            System.out.println("OLD X:" + startParams[0] + " Y:" + startParams[1] + " Z:" + startParams[2]);
            System.out.println("New X:" + newBest.x + " Y:" + newBest.y + " Z:" + newBest.z);
            this.pop.setOnePopulation(individualIndex, bestJump);
        }
        toClose = isClose();
        return getBest(pop.getPopulation());
    }

    private int[] genPTRVector() {
        int[] prt = new int[pop.getPopulation().length];
        for(int i = 0; i < prt.length; i++) {
            prt[i] = getDoubleRandom() < this.PRT ? 1 : 0;
        }
        return prt;
    }

    private boolean isClose() {
        if(this.minDiv  >= Math.abs(this.getBest(pop.getPopulation()).z - this.getWorst(pop.getPopulation()).z )) {
            return true;
        }
         return false;
    }
}
