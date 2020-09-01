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

    public Genetics() {
        best = new Brain();
        bestBrains = new ArrayList<>();
        population = 100;
        generation = 50;
        groupset = false;
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
        for (int i = 0; i < sorted.size() - 1; i++) {
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
            Brain child2 = new Brain();
            child2.createDefaultPerceptronMap();

            //Czym są geny w tym brainie???? (moze wagi na neuronach)
            //Gdzie zrobic cut????
            //Zamien inty w cut na floaty i zamien je pozniej na int
            Double lowerHalf = Math.floor((double) brain1.getPerceptronCount() / 2);
            Double upperHalf = Math.ceil((double) brain2.getPerceptronCount() / 2);
            int cut = ThreadLocalRandom.current().nextInt(1, lowerHalf.intValue());
            int cut2 = ThreadLocalRandom.current().nextInt(1, upperHalf.intValue());

            for (int k = 0; k < cut; k++) {
                //child1.getPerceptronMap().values().stream().forEach();
                child1.replaceGivenPerceptron(k, brain1.getGivenPerceptron(k));
                //child1.getPerceptronMap().put(k, Iterables.get(brain1.getPerceptronMap().values(),k));
            }
            for (int k = 0; k < cut2; k++) {
                child2.replaceGivenPerceptron(k, brain2.getGivenPerceptron(k));
                //child2.getPerceptronMap().put(k, Iterables.get(brain2.getPerceptronMap().values(),k));
            }
            for (int k = cut; k < brain1.getPerceptronCount(); k++) {
                child2.replaceGivenPerceptron(k, brain1.getGivenPerceptron(k));
                //child2.getPerceptronMap().put(k, Iterables.get(brain1.getPerceptronMap().values(),k));
            }
            for (int k = cut2; k < brain2.getPerceptronCount(); k++) {
                child1.replaceGivenPerceptron(k, brain2.getGivenPerceptron(k));
                //child1.getPerceptronMap().put(k, Iterables.get(brain2.getPerceptronMap().values(),k));
            }
            getGenePool().add(child1);
            getGenePool().add(child2);
        }
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

    //Narazie tego nie potrzebuje, dodam moze pozniej
    /*public void mutate(){
        for (int i = 0; i < getGenePool().size(); i++) {
            for (int j = 0; j < getGenePool().get(i).getPerceptronMap().size(); j++) {
                float temp = (float) getGenePool().get(i).getPerceptronMap().size();
                float probability = 1/temp;
                float random = ThreadLocalRandom.current().nextFloat()/100;
                if(random<probability){
                    int rrandom = ThreadLocalRandom.current().nextInt(0,3);
                    while (rrandom==getGenePool().get(i).getMoves().get(j)){
                        rrandom = ThreadLocalRandom.current().nextInt(0,3);
                    }
                    getGenePool().get(i).setMove(j,rrandom);
                }
            }
        }
    }*/
    public void getAverageFitness() {
        int sum = 0;
        System.out.println("Rozmiar to: " + getGenePool().size());
        for (int i = 0; i < getGenePool().size(); i++) {
            sum += getGenePool().get(i).getScore();
        }
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
        best = sorted.get(0);
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
        try{
            mutex.lock();
            return genePool;
        }catch (Exception e){

        }finally {
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
        try{
            mutex.lock();
            return generation;
        }catch (Exception e){

        }finally {
            mutex.unlock();
        }
        return 0;
    }

    public void setGeneration(int generation) {
        try{
            mutex.lock();
            this.generation = generation;
        }catch (Exception e){

        }finally {
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
        try{
            mutex.lock();
            return groupset;
        }catch (Exception e){

        }finally {
            mutex.unlock();
        }
        return false;
    }

    public void setGroupset(boolean groupset) {
        try{
            mutex.lock();
            this.groupset = groupset;
        }catch (Exception e){

        }finally {
            mutex.unlock();
        }
    }

    public boolean isGenerated() {
        try{
            mutex.lock();
            return generated;
        }catch (Exception e){

        }finally {
            mutex.unlock();
        }
        return false;
    }

    public void setGenerated(boolean generated) {
        try{
            mutex.lock();
            this.generated = generated;
        }catch (Exception e){

        }finally {
            mutex.unlock();
        }
    }
}
