import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(JUnit4.class)
public class BrainControllerTest {
    private Brain brain;
    private BrainController brainController;
    private Integer[][] matrix;

    @Test
    public void ControllerTest() {
        brain = createBrain();
        brainController = new BrainController();
        matrix = createMatrix();
        brainController.setBrain(brain);
        brainController.setCurrentInputs(matrix);
        /*for(Perceptron p : brainController.getBrain().getGivenLayer(0).values()){
            Assert.assertEquals(p.getInput(0),);
        }*/
    }

    @Test
    public void loopTest() {
        List<Integer> list = new ArrayList<>();
        list.add(4);
        list.add(2);
        list.add(3);
        list.add(1);
        list.remove(4);
        System.out.println(list.size());
    }

    @Test
    public void test() {
        Brain brain = new Brain();
        Multimap<Float, Float> multimap = HashMultimap.create();
        multimap.put(0.1337f, 0.420f);
        Perceptron p = new Perceptron();
        p.setInputs(multimap);
        brain.createDefaultPerceptronMap();
        brain.replaceGivenPerceptron(17, p);
        /*Double lowerHalf = Math.floor((double) brain.getPerceptronCount()/2);
        Double upperHalf = Math.ceil((double) brain.getPerceptronCount()/2);
        System.out.println(lowerHalf);
        System.out.println(upperHalf);*/
        System.out.println(0.432f * 0f * (1 + Math.exp(0)));
        System.out.println(1 + Math.exp(0));
        System.out.println(1 - Math.exp(-0) / (1 + Math.exp(0)));
    }

    public Brain createBrain() {
        Brain brain = new Brain();
        List<Integer> integers = new ArrayList<>();
        integers.add(16);
        integers.add(4);
        brain.createPerceptronMap(2, integers);
        return brain;
    }

    public Integer[][] createMatrix() {
        Integer[][] matrix = new Integer[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = (i * j) + j;
            }
        }
        return matrix;
    }

    public void addToMatrix(Integer[][] matrix) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] += (i * j) + j;
            }
        }
    }
}
