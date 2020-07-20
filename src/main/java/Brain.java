import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Brain {
    private Integer lp;
    private Integer score;
    private Integer fitness;
    private Float pc;
    private Multimap<Integer,Multimap<Integer,Perceptron>> perceptronMap;

    public Brain(){}

    public Brain(Integer lp, Integer score, Integer fitness, Float pc, Multimap<Integer, Multimap<Integer,Perceptron>> perceptronMap) {
        this.lp = lp;
        this.score = score;
        this.fitness = fitness;
        this.pc = pc;
        this.perceptronMap = perceptronMap;
    }

    //Zrobic automatyczne polaczenia po każdym cyklu
    //Zrobic automatyczne generowanie losowych wag
    public void createPerceptronMap(Integer layers, List<Integer> columns){
        perceptronMap = ArrayListMultimap.create();
        for (int k = 0; k < layers; k++) {//3
            Multimap<Integer,Perceptron> temp = ArrayListMultimap.create();
            for (int l = 0; l < columns.get(k); l++) {
                temp.put(l,new Perceptron());
            }
            perceptronMap.put(k,temp);
        }
        /*for (int j = 0; j < i; j++) {
            perceptronMap.put(j,new Perceptron());
        }*/
    }



    public Integer getLp() {
        return lp;
    }

    public void setLp(Integer lp) {
        this.lp = lp;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getFitness() {
        return fitness;
    }

    public void setFitness(Integer fitness) {
        this.fitness = fitness;
    }

    public Float getPc() {
        return pc;
    }

    public void setPc(Float pc) {
        this.pc = pc;
    }

    public Multimap<Integer, Multimap<Integer, Perceptron>> getPerceptronMap() {
        return perceptronMap;
    }

    public void setPerceptronMap(Multimap<Integer, Multimap<Integer, Perceptron>> perceptronMap) {
        this.perceptronMap = perceptronMap;
    }
}
