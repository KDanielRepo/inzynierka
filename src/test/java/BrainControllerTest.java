import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ThreadLocalRandom;

@RunWith(JUnit4.class)
public class BrainControllerTest {

    @Test
    public void setCurrentInputsTest(){
        BrainController brainController = createBrainController();
        System.out.println(brainController.getBrain().getOutputLayer().values());
        brainController.setCurrentInputs(randomValues());
        System.out.println(brainController.getBrain().getOutputLayer().values());
    }

    private Integer[][] randomValues(){
        Integer[][] random = new Integer[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                random[i][j] = ThreadLocalRandom.current().nextInt(1,10);
            }
        }
        return random;
    }

    private BrainController createBrainController(){
        Brain brain = new Brain();
        brain.createDefaultPerceptronMap();
        BrainController brainController = new BrainController();
        brainController.setBrain(brain);
        return brainController;
    }
}
