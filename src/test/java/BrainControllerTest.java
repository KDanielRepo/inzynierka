import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RunWith(JUnit4.class)
public class BrainControllerTest {

    @Test
    public void trashTest(){
        Multimap map = HashMultimap.create();

        map.put("game", 1);
        map.put("game", 2);

        map.put("book", 4);
        map.put("book", 3);

        Iterable iter = map.get("book");
        map.replaceValues("game", iter);

        System.out.println(map);
    }

    @Test
    public void setCurrentInputsTest(){
        BrainController brainController = createBrainController();
        System.out.println(brainController.getBrain().getOutputLayer().values());
        long start = System.nanoTime();
        brainController.setCurrentInputs(randomValues());
        long end = System.nanoTime();
        System.out.println("wynik: "+(end-start)*0.000000001);
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
