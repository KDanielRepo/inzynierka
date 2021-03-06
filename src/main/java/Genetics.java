import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Genetics {
    private List<Brain> genePool = new ArrayList<>();
    private List<Brain> pcPool = new ArrayList<>();
    private int convergence = 0;
    private Brain best;
    private int population;

    public Genetics(){}

    public void createOffspring() {
        //ustawianie PC kazdego osobnika
        for (int i = 0; i < getGenePool().size(); i++) {
            getGenePool().get(i).setPc(ThreadLocalRandom.current().nextFloat());
        }
        //wybieranie osobnikow do puli c
        for (int i = 0; i < getGenePool().size(); i++) {
            if(getGenePool().get(i).getPc()>0.6f&&getPcPool().size()<population){
                getPcPool().add(getGenePool().get(i));
                getGenePool().remove(i);
            }
        }
        //uzupelnienie puli do liczby parzystej
        while(getPcPool().size()<population || getPcPool().size()%2!=0){
            int random = ThreadLocalRandom.current().nextInt(0,getGenePool().size());
            getPcPool().add(getGenePool().get(random));
            getGenePool().remove(random);
        }
        //sprawdzenie czy najlepszy osobnik jest w puli
        int testt = 0;
        for (int i = 0; i < getPcPool().size(); i++) {
            if(getPcPool().get(i).getScore()!=best.getScore()){
                testt++;
            }
            if(testt==getPcPool().size()){
                getPcPool().remove(ThreadLocalRandom.current().nextInt(0,getPcPool().size()));
                getPcPool().add(best);
            }
        }
        //krzyzowanie
        int[] a = new int[population];
        for (int i = 0; i < getPcPool().size()/2; i++) {
            int random = ThreadLocalRandom.current().nextInt(0,population);
            if(a[random]==0){
                a[random]=1;
            }else{
                while (a[random]!=0){
                    random = ThreadLocalRandom.current().nextInt(0,population);
                }
            }
            Brain brain1 = getPcPool().get(random);
            int random2 = ThreadLocalRandom.current().nextInt(0,population);
            if(a[random2]==0){
                a[random2]=1;
            }else{
                while (a[random2]!=0){
                    random2 = ThreadLocalRandom.current().nextInt(0,population);
                }
            }
            Brain brain2 = getPcPool().get(random2);
            Brain child1 = new Brain();
            Brain child2 = new Brain();

            //Czym są geny w tym brainie???? (moze wagi na neuronach)
            //Gdzie zrobic cut????
            //Zamien inty w cut na floaty i zamien je pozniej na int
            Double lowerHalf = Math.floor((double) brain1.getPerceptronMap().size()/2);
            Double upperHalf = Math.ceil((double) brain2.getPerceptronMap().size()/2);
            int cut = ThreadLocalRandom.current().nextInt(1,lowerHalf.intValue());
            int cut2 = ThreadLocalRandom.current().nextInt(1,upperHalf.intValue());
            for (int k = 0; k < cut; k++) {
                child1.getPerceptronMap().put(k, Iterables.get(brain1.getPerceptronMap().values(),k));
            }
            for(int k = 0; k<cut2;k++){
                child2.getPerceptronMap().put(k, Iterables.get(brain2.getPerceptronMap().values(),k));
            }
            for (int k = cut; k < brain1.getPerceptronMap().size(); k++) {
                child2.getPerceptronMap().put(k, Iterables.get(brain1.getPerceptronMap().values(),k));
            }
            for (int k = cut2; k < brain2.getPerceptronMap().size(); k++) {
                child1.getPerceptronMap().put(k, Iterables.get(brain2.getPerceptronMap().values(),k));
            }
            getGenePool().add(child1);
            getGenePool().add(child2);
        }
    }
    public void resetPcPool(){
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
    public void getAverageFitness(){
        int sum=0;
        for (int i = 0; i < getGenePool().size(); i++) {
            sum+=getGenePool().get(i).getScore();
        }
        //System.out.println(sum/getGenePool().size());
    }
    public int calculateGlobalFitness(){
        int sum = 0;
        for (int i = 0; i < getGenePool().size(); i++) {
            sum+=getGenePool().get(i).getScore();
        }
        return sum;
    }
    public void calculateRFitness(){
        for (int i = 0; i < getGenePool().size(); i++) {
            getGenePool().get(i).setFitness((getGenePool().get(i).getScore()/calculateGlobalFitness()));
        }
    }

    public int getFittest(){
        int fit = 0;
        for (int i = 0; i < getGenePool().size(); i++) {
            if(fit<getGenePool().get(i).getScore()){
                fit = getGenePool().get(i).getScore();
                if(best.getScore()<fit){
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



    public Brain getBest(){
        return best;
    }

    public void setBest(Brain best) {
        this.best = best;
    }

    public List<Brain> getGenePool() {
        return genePool;
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
}
