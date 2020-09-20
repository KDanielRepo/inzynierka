import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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

    public Integer generateMove(){
        Map<Integer, Float> map = softmax();
        Float value = Float.parseFloat(map
                .values()
                .stream()
                .max(Comparator.naturalOrder())
                .toString()
                .substring(8)
                .replaceAll("[\\[\\]]",""));

        return Integer.parseInt(map
                .entrySet()
                .stream()
                .filter(entry->value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .toString()
                .substring(8)
                .replaceAll("[\\[\\]]",""));
    }

    public Integer generateMoveWithoutBlocks(){
        List<Integer> avMoves = new ArrayList<>();
        Map<Integer,Float> map = softmax();
        Integer move = -1;
        for (int i = map.size()-1; i >=0 ; i--) {
            int finalI = i;
            move = Integer.parseInt(map
                    .entrySet()
                    .stream()
                    .filter(entry -> map.get(finalI).equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .toString()
                    .substring(8)
                    .replaceAll("[\\[\\]]", ""));
            avMoves.add(move);
        }
        blocks.stream().distinct().forEach(e->{
            if(avMoves.contains(e)){
                avMoves.remove(e);
            }
        });
        return avMoves.get(0);
    }

    public Map<Integer,Float> softmax(){
        Map<Integer,Float> probabilities = new HashMap<>();
        for (int i = 0; i < brain.getOutputLayer().size(); i++) {
            Float sum = brain.getOutputLayer().values().stream().map(Math::exp).reduce(0d,Double::sum).floatValue();
            Float value = Iterables.get(brain.getOutputLayer().values(),i);
            Float probability =  value/sum;
            probabilities.put(i,probability);
        }
        return probabilities;
    }

    public void activateAll(){
        for (Multimap<Integer, Perceptron> mp : brain.getPerceptronMap().values()){
            for (Perceptron p : mp.values()){
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
            for (int k = 0; k < 16; k++) {
                p.replacePerceptronValue(k, list.get(k).floatValue());
            }
            i++;
        }
        //activateAll();
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
