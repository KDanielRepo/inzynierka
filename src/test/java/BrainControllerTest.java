import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class BrainControllerTest {
    private Brain brain;
    private BrainController brainController;
    private Integer[][] matrix;
    @Test
    public void ControllerTest(){
        brain = createBrain();
        brainController = new BrainController();
        matrix = createMatrix();
        brainController.setBrain(brain);
        brainController.setCurrentInputs(matrix);
        /*for(Perceptron p : brainController.getBrain().getGivenLayer(0).values()){
            Assert.assertEquals(p.getInput(0),);
        }*/
    }
    public Brain createBrain(){
        Brain brain = new Brain();
        List<Integer> integers = new ArrayList<>();
        integers.add(16);
        integers.add(4);
        brain.createPerceptronMap(2,integers);
        return brain;
    }
    public Integer[][] createMatrix(){
        Integer[][] matrix = new Integer[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = (i*j)+j;
            }
        }
        return matrix;
    }
    public void addToMatrix(Integer[][] matrix){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] += (i*j)+j;
            }
        }
    }
}
