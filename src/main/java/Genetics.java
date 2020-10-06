import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Genetics {
    private List<Brain> genePool = new ArrayList<>();
    private List<Brain> pcPool = new ArrayList<>();
    private int convergence = 0;
    private Brain best;
    private int population;
    private int generation;
    private List<Brain> bestBrains;
    private Mutex mutex;
    private boolean groupset;
    private boolean generated;
    private int currentGeneration;
    private int weights;
    private Double lowerHalf;
    private Double upperHalf;

    public Genetics() {
        best = new Brain();
        bestBrains = new ArrayList<>();
        population = 100;
        generation = 500;
        groupset = false;
        currentGeneration = 0;
        weights = 0;
    }

    public Genetics(int population) {
        this.population = population;
    }

    public void createOffspring() {
        getRealFittest();
        //ustawianie PC kazdego osobnika
        for (int i = 0; i < getGenePool().size(); i++) {
            getGenePool().get(i).setPc(ThreadLocalRandom.current().nextFloat() * getGenePool().get(i).getScore());
        }
        //wybieranie osobnikow do puli c
        List<Brain> sorted = getGenePool().stream().sorted(Comparator.comparing(Brain::getPc).reversed()).collect(Collectors.toList());
        for (int i = 0; i < sorted.size(); i++) {
            if (getPcPool().size() < population) {
                getPcPool().add(sorted.get(i));
                sorted.remove(i);
            }
        }
        setGenePool(sorted);
        //uzupelnienie puli do liczby parzystej
        while (getPcPool().size() < population || getPcPool().size() % 2 != 0) {
            int random = ThreadLocalRandom.current().nextInt(0, getGenePool().size());
            getPcPool().add(getGenePool().get(random));
            getGenePool().remove(random);
        }
        //sprawdzenie czy najlepszy osobnik jest w puli
        if (!getPcPool().contains(best)) {
            getPcPool().remove(ThreadLocalRandom.current().nextInt(0, getPcPool().size()));
            getPcPool().add(best);
        }
        //krzyzowanie
        int[] a = new int[population];
        System.out.println("rozmiar pc: " + getPcPool().size());
        int weights = 0;//getPcPool().get(0).getPerceptronCount();
        for (int j = 0; j < getPcPool().get(0).getPerceptronMap().values().size(); j++) {
            for (int k = 0; k < getPcPool().get(0).getGivenLayer(j).values().size(); k++) {
                for (int l = 0; l < getPcPool().get(0).getGivenPerceptron(k).getInputs().keys().size(); l++) {
                    weights++;
                }
            }
        }
        for (int i = 0; i < getPcPool().size() / 2; i++) {
            int random = ThreadLocalRandom.current().nextInt(0, population);
            if (a[random] == 0) {
                a[random] = 1;
            } else {
                while (a[random] != 0) {
                    random = ThreadLocalRandom.current().nextInt(0, population);
                }
            }
            Brain brain1 = getPcPool().get(random);
            int random2 = ThreadLocalRandom.current().nextInt(0, population);
            if (a[random2] == 0) {
                a[random2] = 1;
            } else {
                while (a[random2] != 0) {
                    random2 = ThreadLocalRandom.current().nextInt(0, population);
                }
            }
            Brain brain2 = getPcPool().get(random2);
            Brain child1 = new Brain();
            child1.createDefaultPerceptronMap();
            child1.setPerceptronMap(brain1.getPerceptronMap());
            Brain child2 = new Brain();
            child2.createDefaultPerceptronMap();
            child2.setPerceptronMap(brain2.getPerceptronMap());

            Double lowerHalf = Math.floor((double) weights / 2);
            Double upperHalf = Math.ceil((double) weights / 2);
            int cut = ThreadLocalRandom.current().nextInt(1, lowerHalf.intValue());
            int cut2 = ThreadLocalRandom.current().nextInt(1, upperHalf.intValue());

            for (int k = 0; k < cut; k++) {
                //child1.replaceGivenPerceptron(k,brain1.getGivenPerceptron(k));
                child1.replaceGivenWeightByIndex(k, brain1.getGivenWeightByIndex(k));
            }
            for (int k = 0; k < cut2; k++) {
                //child2.replaceGivenPerceptron(k,brain2.getGivenPerceptron(k));
                child2.replaceGivenWeightByIndex(k, brain2.getGivenWeightByIndex(k));
            }
            for (int k = cut; k < brain1.getPerceptronCount(); k++) {
                //child2.replaceGivenPerceptron(k,brain1.getGivenPerceptron(k));
                child2.replaceGivenWeightByIndex(k, brain1.getGivenWeightByIndex(k));
            }
            for (int k = cut2; k < brain2.getPerceptronCount(); k++) {
                //child1.replaceGivenPerceptron(k,brain1.getGivenPerceptron(k));
                child1.replaceGivenWeightByIndex(k, brain2.getGivenWeightByIndex(k));
            }
            getGenePool().add(child1);
            getGenePool().add(child2);
        }
        mutate();
        setGroupset(true);
        setGenerated(true);
    }

    private void getWeightCount() {
        for (int j = 0; j < getPcPool().get(0).getPerceptronMap().values().size(); j++) {
            for (int k = 0; k < getPcPool().get(0).getGivenLayer(j).values().size(); k++) {
                for (int l = 0; l < getPcPool().get(0).getGivenPerceptron(k).getInputs().keys().size(); l++) {
                    weights++;
                }
            }
        }
        lowerHalf = Math.floor((double) weights / 2);
        upperHalf = Math.ceil((double) weights / 2);
    }

    public void createOffspringCorrect() {
        getRealFittest();
        replaceWorstBrains();
        //ustawianie PC kazdego osobnika
        for (int i = 0; i < getGenePool().size(); i++) {
            getGenePool().get(i).setPc(ThreadLocalRandom.current().nextFloat() * getGenePool().get(i).getScore());
        }
        //wybieranie osobnikow do puli c
        List<Brain> sorted = getGenePool().stream().sorted(Comparator.comparing(Brain::getPc).reversed()).collect(Collectors.toList());
        for (int i = 0; i < sorted.size() / 2; i++) {
            if (getPcPool().size() < population) {
                getPcPool().add(sorted.get(i));
                sorted.remove(i);
            }
        }
        setGenePool(sorted);
        //uzupelnienie puli do liczby parzystej
        while (getPcPool().size() < population / 2 || getPcPool().size() % 2 != 0) {
            int random = ThreadLocalRandom.current().nextInt(0, getGenePool().size());
            getPcPool().add(getGenePool().get(random));
            getGenePool().remove(random);
        }
        //sprawdzenie czy najlepszy osobnik jest w puli
        if (!getPcPool().contains(best)) {
            getPcPool().remove(ThreadLocalRandom.current().nextInt(0, getPcPool().size()));
            getPcPool().add(best);
        }
        setGenePool(new ArrayList<>());
        //krzyzowanie
        int[] a = new int[population / 2];
        if (weights == 0) {
            getWeightCount();
        }
        for (int i = 0; i < getPcPool().size(); i++) {
            int random = ThreadLocalRandom.current().nextInt(0, population / 2);
            if (a[random] == 0) {
                a[random] = 1;
            } else {
                while (a[random] != 0) {
                    random = ThreadLocalRandom.current().nextInt(0, population / 2);
                }
            }
            Brain brain1 = getPcPool().get(random);
            int random2 = ThreadLocalRandom.current().nextInt(0, population / 2);
            if (a[random2] == 0) {
                a[random2] = 1;
            } else {
                while (a[random2] != 0) {
                    random2 = ThreadLocalRandom.current().nextInt(0, population / 2);
                }
            }
            Brain brain2 = getPcPool().get(random2);
            Brain child1 = new Brain();
            child1.createDefaultPerceptronMap();
            child1.setPerceptronMap(brain1.getPerceptronMap());
            Brain child2 = new Brain();
            child2.createDefaultPerceptronMap();
            child2.setPerceptronMap(brain2.getPerceptronMap());

            int cut = ThreadLocalRandom.current().nextInt(1, lowerHalf.intValue());
            for (int k = cut; k < weights; k++) {
                child2.replaceGivenWeightByIndex(k, brain1.getGivenWeightByIndex(k));
                child1.replaceGivenWeightByIndex(k, brain2.getGivenWeightByIndex(k));
            }
            getGenePool().add(child1);
            getGenePool().add(child2);
        }
        System.out.println("rozmiar pc: " + getPcPool().size());
        System.out.println("gp to: " + getGenePool().size());
        mutate();
        setGroupset(true);
        setGenerated(true);
    }

    public void createOffspringNew() {
        //ustawianie PC kazdego osobnika
        for (int i = 0; i < getGenePool().size(); i++) {
            getGenePool().get(i).setPc(ThreadLocalRandom.current().nextFloat());
        }
        getTwoFittest();
        List<Brain> parents = bestBrains;
        getGenePool().clear();
        //krzyzowanie
        int[] a = new int[population];
        for (int i = 0; i < (population - 2) / 2; i++) {
            Brain brain1 = parents.get(0);
            Brain brain2 = parents.get(1);
            Brain child1 = new Brain();
            child1.createDefaultPerceptronMap();
            Brain child2 = new Brain();
            child2.createDefaultPerceptronMap();

            Double lowerHalf = Math.floor((double) brain1.getPerceptronCount() / 2 / 2);
            Double upperHalf = Math.ceil((double) brain2.getPerceptronCount() / 2);
            int cut = ThreadLocalRandom.current().nextInt(1, lowerHalf.intValue());
            int cut2 = ThreadLocalRandom.current().nextInt(1, upperHalf.intValue());

            for (int k = 0; k < cut; k++) {
                child1.replaceGivenPerceptron(k, brain1.getGivenPerceptron(k));
            }
            for (int k = 0; k < cut2; k++) {
                child2.replaceGivenPerceptron(k, brain2.getGivenPerceptron(k));
            }
            for (int k = cut; k < brain1.getPerceptronCount(); k++) {
                child2.replaceGivenPerceptron(k, brain1.getGivenPerceptron(k));
            }
            for (int k = cut2; k < brain2.getPerceptronCount(); k++) {
                child1.replaceGivenPerceptron(k, brain2.getGivenPerceptron(k));
            }
            getGenePool().add(child1);
            getGenePool().add(child2);
        }
        getGenePool().add(parents.get(0));
        getGenePool().add(parents.get(1));
        setGroupset(true);
        setGenerated(true);
    }

    public void resetPcPool() {
        pcPool = new ArrayList<>();
    }

    public void mutate() {
        for (int i = 0; i < getGenePool().size() - 1; i++) {
            for (int j = 0; j < getGenePool().get(i).getPerceptronCount() - 1; j++) {
                float temp = (float) getGenePool().get(i).getPerceptronCount();
                float probability = 1f / 50f * (0.1f * getCurrentGeneration());
                float random = ThreadLocalRandom.current().nextFloat();
                if (random < probability) {
                    float mutation = ThreadLocalRandom.current().nextFloat();
                    int mutationNumber = 0;
                    try {
                        mutationNumber = ThreadLocalRandom.current().nextInt(0, getGenePool().get(i).getGivenPerceptron(j).getWeights().size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    getGenePool().get(i).replaceGivenWeightByIndex(mutationNumber, mutation);
                }
            }
        }
    }

    public void getAverageFitness() {
        int sum = 0;
        for (int i = 0; i < getGenePool().size(); i++) {
            sum += getGenePool().get(i).getScore();
        }
        System.out.println("rozmiar GP to: " + getGenePool().size());
        System.out.println("Srednia to: " + sum / population);
        //System.out.println(sum/getGenePool().size());
    }

    public int calculateGlobalFitness() {
        int sum = 0;
        for (int i = 0; i < getGenePool().size(); i++) {
            sum += getGenePool().get(i).getScore();
        }
        return sum;
    }

    public void calculateRFitness() {
        for (int i = 0; i < getGenePool().size(); i++) {
            getGenePool().get(i).setFitness((getGenePool().get(i).getScore() / calculateGlobalFitness()));
        }
    }

    public int getFittest() {
        int fit = 0;
        for (int i = 0; i < getGenePool().size(); i++) {
            if (fit < getGenePool().get(i).getScore()) {
                fit = getGenePool().get(i).getScore();
                if (best.getScore() < fit) {
                    //System.out.println("tempbest: "+best.getScore()+" , gentemp: "+fit);
                    convergence = 0;
                    best = getGenePool().get(i);
                }
            }
        }
        convergence++;
        //System.out.println("best of this gen: "+fit);
        return fit;
    }

    public void getRealFittest() {
        List<Brain> sorted = getGenePool().stream().sorted(Comparator.comparing(Brain::getScore).reversed()).collect(Collectors.toList());
        if (best == null || sorted.get(0).getScore() > best.getScore()) {
            best = sorted.get(0);
        }
        System.out.println("Najlepszy osobnik uzyskal wynik :" + best.getScore() + " o indeksie : " + best.getLp() + " id: " + best);
    }

    public void replaceWorstBrains() {
        List<Brain> sorted = getGenePool().stream().sorted(Comparator.comparing(Brain::getScore)).collect(Collectors.toList());
        for (int i = 0; i < 10; i++) {
            Brain brain = new Brain();
            brain.createDefaultPerceptronMap();
            sorted.remove(sorted.get(i));
            sorted.add(brain);
        }
    }

    public void getTwoFittest() {
        List<Brain> sorted = getGenePool().stream().sorted(Comparator.comparing(Brain::getScore).reversed()).collect(Collectors.toList());
        /*for (int i = 0; i < sorted.size(); i++) {
            System.out.println(sorted.get(i).getScore());
        }*/
        List<Brain> best = new ArrayList<>();
        best.add(sorted.get(0));
        System.out.println("Najlepszy: " + best.get(0).getScore());
        best.add(sorted.get(1));
        System.out.println("Drugi najlepszy: " + best.get(1).getScore());
        if (bestBrains.size() == 0) {
            bestBrains = best;
        }
        for (int i = 1; i >= 0; i--) {
            for (int j = 1; j >= 0; j--) {
                if (best.get(i).getScore() > bestBrains.get(j).getScore()) {
                    bestBrains.remove(j);
                    bestBrains.add(best.get(i));
                }
            }
        }
    }


    public Brain getBest() {
        return best;
    }

    public void setBest(Brain best) {
        this.best = best;
    }

    public List<Brain> getGenePool() {
        try {
            mutex.lock();
            return genePool;
        } catch (Exception e) {

        } finally {
            mutex.unlock();
        }
        return null;
    }

    public void setGenePool(List<Brain> genePool) {
        this.genePool = genePool;
    }

    public List<Brain> getPcPool() {
        return pcPool;
    }

    public void setPcPool(List<Brain> pcPool) {
        this.pcPool = pcPool;
    }

    public int getConvergence() {
        return convergence;
    }

    public void setConvergence(int convergence) {
        this.convergence = convergence;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getGeneration() {
        try {
            mutex.lock();
            return generation;
        } catch (Exception e) {

        } finally {
            mutex.unlock();
        }
        return 0;
    }

    public void setGeneration(int generation) {
        try {
            mutex.lock();
            this.generation = generation;
        } catch (Exception e) {

        } finally {
            mutex.unlock();
        }
    }

    public Mutex getMutex() {
        return mutex;
    }

    public void setMutex(Mutex mutex) {
        this.mutex = mutex;
    }

    public boolean isGroupset() {
        try {
            mutex.lock();
            return groupset;
        } catch (Exception e) {

        } finally {
            mutex.unlock();
        }
        return false;
    }

    public void setGroupset(boolean groupset) {
        try {
            mutex.lock();
            this.groupset = groupset;
        } catch (Exception e) {

        } finally {
            mutex.unlock();
        }
    }

    public boolean isGenerated() {
        try {
            mutex.lock();
            return generated;
        } catch (Exception e) {

        } finally {
            mutex.unlock();
        }
        return false;
    }

    public void setGenerated(boolean generated) {
        try {
            mutex.lock();
            this.generated = generated;
        } catch (Exception e) {

        } finally {
            mutex.unlock();
        }
    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }

    public void setCurrentGeneration(int currentGeneration) {
        this.currentGeneration = currentGeneration;
    }
}
