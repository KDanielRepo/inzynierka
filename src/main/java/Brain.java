import com.google.common.collect.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Brain implements Comparable<Brain> {
    private Integer lp;
    private Integer score;
    private Integer fitness;
    private Float pc;
    private Multimap<Integer, Perceptron> perceptronMap;

    public Brain() {
        lp = 0;
        score = 0;
        fitness = 0;
        pc = 0f;
    }

    public Brain(Integer lp, Integer score, Integer fitness, Float pc, Multimap<Integer, Perceptron> perceptronMap) {
        this.lp = lp;
        this.score = score;
        this.fitness = fitness;
        this.pc = pc;
        this.perceptronMap = perceptronMap;
    }

    public List<Perceptron> getGivenLayer(int layer) {
        List<Perceptron> givenLayer = new ArrayList<>(getPerceptronMap().get(layer));
        return givenLayer;
    }

    public List<Perceptron> getOutputLayer() {
        return new ArrayList<>(perceptronMap.get(perceptronMap.keySet().size()-1));
    }

    public Integer getPerceptronCount() {
        Integer count = 0;
        for (Perceptron p : getPerceptronMap().values()) {
            count++;
        }
        return count;
    }

    public Perceptron getGivenPerceptron(int index) {
        return Iterables.get(getPerceptronMap().values(), index);
    }

    public void replaceGivenWeightByIndex(int index, Float weightValue) {
        AtomicInteger tempIndex = new AtomicInteger(0);
        getPerceptronMap().values().forEach(perceptron -> {
            perceptron.getInputs().forEach(dendrite -> {
                if (tempIndex.get() == index) {
                    dendrite.setWeight(weightValue);
                }
                tempIndex.getAndIncrement();
            });
        });
    }

    public Float getGivenWeightByIndex(int index) {
        AtomicInteger tempIndex = new AtomicInteger(0);
        AtomicReference<Float> weight = new AtomicReference<>(0f);
        getPerceptronMap().values().stream().forEach(perceptron -> {
            perceptron.getInputs().forEach(dendrite -> {
                if (tempIndex.get() == index) {
                    weight.set(dendrite.getWeight());
                }
                tempIndex.getAndIncrement();
            });
        });
        return weight.get();
    }

    public void createPerceptronMap(List<Integer> rows) {
        perceptronMap = HashMultimap.create();
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i); j++) {
                Perceptron perceptron = new Perceptron();
                if (i == 0) {
                    perceptron.setLayer(i);
                    Float weight = ThreadLocalRandom.current().nextFloat();
                    Dendrite dendrite = new Dendrite();
                    dendrite.setValue(0f);
                    dendrite.setWeight(weight);
                    dendrite.setIn(perceptron);
                } else {
                    for (int k = 0; k < rows.get(i - 1); k++) {
                        Float weight = ThreadLocalRandom.current().nextFloat();
                        Dendrite dendrite = new Dendrite();
                        dendrite.setValue(0f);
                        dendrite.setWeight(weight);
                        dendrite.setIn(perceptron);
                        dendrite.setOut(Iterables.get(perceptronMap.get(i - 1), k));
                    }
                }
                perceptronMap.put(i, perceptron);
            }
        }
    }

    public void updatePerceptronValues() {
        for(Perceptron perceptron : getPerceptronMap().values()){
            if(perceptron.getLayer()>0){
                for(Dendrite dendrite : perceptron.getInputs()){
                    dendrite.setValue(dendrite.getOut().getOutput());
                }
            }
            perceptron.activation();
        }
    }

    public void createDefaultPerceptronMap() {
        List<Integer> rows = Arrays.asList(8, 8, 8, 8, 4);
        perceptronMap = HashMultimap.create();
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i); j++) {
                Perceptron perceptron = new Perceptron();
                List<Dendrite> dendrites = new ArrayList<>();
                if (i == 0) {
                    perceptron.setLayer(i);
                    for (int k = 0; k < 16; k++) {
                        Float weight = ThreadLocalRandom.current().nextFloat();
                        Dendrite dendrite = new Dendrite();
                        dendrite.setValue(0f);
                        dendrite.setWeight(weight);
                        dendrite.setIn(perceptron);
                        dendrites.add(dendrite);
                    }
                    perceptron.setInputs(dendrites);
                } else {
                    for (int k = 0; k < rows.get(i - 1); k++) {
                        Float weight = ThreadLocalRandom.current().nextFloat();
                        Dendrite dendrite = new Dendrite();
                        dendrite.setValue(Iterables.get(perceptronMap.get(i - 1), k).getOutput());
                        dendrite.setWeight(weight);
                        dendrite.setIn(perceptron);
                        dendrite.setOut(Iterables.get(perceptronMap.get(i - 1), k));
                        dendrites.add(dendrite);
                    }
                    perceptron.setLayer(i);
                    perceptron.setInputs(dendrites);
                }
                perceptronMap.put(i, perceptron);
            }
        }
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

    public Multimap<Integer, Perceptron> getPerceptronMap() {
        return perceptronMap;
    }

    public void setPerceptronMap(Multimap<Integer, Perceptron> perceptronMap) {
        this.perceptronMap = perceptronMap;
    }

    @Override
    public int compareTo(Brain o) {
        if (getScore() == null) {
            return 0;
        } else {
            return getScore().compareTo(o.getScore());
        }
    }

}
