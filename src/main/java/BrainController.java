import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BrainController {
    private Brain brain;
    private List<Integer> blocks;
    private Integer currentMove;

    public BrainController() {
        brain = new Brain();
        blocks = new ArrayList<>();
        brain.createDefaultPerceptronMap();
        /*List<Integer> list = new ArrayList<>();
        list.add(16);
        list.add(4);
        brain.createPerceptronMap(2, list);*/
    }

    public Integer generateMove() {
        Float value = Float.parseFloat(brain.getOutputLayer().values()
                .stream()
                .max(Comparator.naturalOrder())
                .toString()
                .substring(8)
                .replaceAll("[\\[\\]]", ""));
        Integer move = Integer.parseInt(brain.getOutputLayer()
                .entries()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .toString()
                .substring(8)
                .replaceAll("[\\[\\]]", ""));
        return move;
    }

    public Integer generateMoveWithoutBlocks() {
        List<Float> floats = new ArrayList<>(brain.getOutputLayer().values());
        floats.sort(Comparator.naturalOrder());

        Integer move = -1;
        for (int j = floats.size() - 1; j > 0; j--) {
            int finalJ = j;
            move = Integer.parseInt(brain.getOutputLayer()
                    .entries()
                    .stream()
                    .filter(entry -> floats.get(finalJ).equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .toString()
                    .replaceAll("[\\[\\]a-z,A-Z]", ""));
            for (Integer anI : blocks) {
                if (move == anI) {
                    floats.remove(j);
                }
            }
        }
        move = Integer.parseInt(brain.getOutputLayer()
                .entries()
                .stream()
                .filter(entry -> floats.get(floats.size() - 1).equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .toString()
                .replaceAll("[\\[\\]a-z,A-Z]", ""));
        return move;
    }

    public void activateAll(){
        for (Multimap<Integer, Perceptron> mp : brain.getPerceptronMap().values()){
            //System.out.println(mp.size());
            for (Perceptron p : mp.values()){
                /*if(mp.size()==8){
                    System.out.println(p.getInputs());
                    System.out.println(p.getWeights());
                    System.out.println(p.getOutput());
                    System.out.println("-----------");
                }*/
                p.activation();
            }
        }
    }

    public void setCurrentInputs(Integer[][] matrix) {
        List<Integer> list = new ArrayList<>();
        for (Integer[] i : matrix) {
            list.addAll(Arrays.asList(i));
        }
        int i = 0;
        for (Perceptron p : brain.getGivenLayer(0).values()) {
            //if(i<16)
            p.replacePerceptronValue(0, list.get(i).floatValue());
            i++;
        }
        activateAll();
        brain.updatePerceptronValues();
    }

    public void training(){

    }

    public Brain getBrain() {
        return brain;
    }

    public void setBrain(Brain brain) {
        this.brain = brain;
    }

    public Integer getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(Integer currentMove) {
        this.currentMove = currentMove;
    }

    public void addBlock(int block) {
        /*Multimap<Integer,Perceptron> p = getBrain().getPerceptronMap().get(0).stream().findFirst().get();
        Perceptron a = p.get(16+block).stream().findFirst().get();
        a.replacePerceptronValue(0,1f);*/
        blocks.add(block);
    }
    public void clearBlocks(){
        Multimap<Integer,Perceptron> p = getBrain().getPerceptronMap().get(0).stream().findFirst().get();
        Multimaps.filterKeys(p,between(14,20)).values().stream().forEach(e->{
            e.replacePerceptronValue(0,0);
        });
    }
    private Predicate<Integer> between(int from, int to){
        return new Predicate<Integer>() {
            @Override
            public boolean apply(@Nullable Integer integer) {
                return (integer.compareTo(from) >= 0 && integer.compareTo(to) <= 0);
            }
        };
    }

    public boolean isNotBlocked() {
        return blocks.size() == 0;
    }

    public List<Integer> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Integer> blocks) {
        this.blocks = blocks;
    }
}
