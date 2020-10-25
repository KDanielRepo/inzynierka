import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
        Map<Integer, Float> map = softmax();
        Float value = map
                .values()
                .stream()
                .sorted(Comparator.naturalOrder())
                .findFirst()
                .get();

        return Integer.parseInt(map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .toString()
                .substring(8)
                .replaceAll("[\\[\\]]", ""));
    }

    public Integer generateMoveWithoutBlocks() {
        List<Integer> avMoves = new ArrayList<>();
        Map<Integer, Float> map = softmax();
        Integer move = -1;
        for (int i = map.size() - 1; i >= 0; i--) {
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
        blocks.stream().distinct().forEach(e -> {
            if (avMoves.contains(e)) {
                avMoves.remove(e);
            }
        });
        return avMoves.get(0);
    }

    public Map<Integer, Float> softmax() {
        Map<Integer, Float> probabilities = new HashMap<>();
        AtomicReference<Double> tempSum = new AtomicReference<>(0d);
        for (int i = 0; i < brain.getOutputLayer().size(); i++) {
            brain.getOutputLayer().forEach(perceptron -> {
                perceptron.getInputs().forEach(dendrite -> {
                    tempSum.updateAndGet(v -> v + Math.exp(dendrite.getValue()));
                });
            });
            Float sum = tempSum.get().floatValue();
            Float value = brain.getOutputLayer().get(i).getOutput();
            Float probability = value / sum;
            probabilities.put(i, probability);
        }
        return probabilities;
    }

    public void setCurrentInputs(Integer[][] matrix) {
        List<Float> list = new ArrayList<>();
        for (Integer[] i : matrix) {
            for (Integer j : i) {
                list.add(j.floatValue());
            }
        }
        for (Perceptron p : brain.getGivenLayer(0)) {
            p.replacePerceptronValues(list);
        }
        brain.updatePerceptronValues();
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
        blocks.add(block);
    }

    private Predicate<Integer> between(int from, int to) {
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
