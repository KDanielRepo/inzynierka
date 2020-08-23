import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GameInstanceTest {
    @Test
    public void workingTest(){
        GameInstance gameInstance = new GameInstance();
        GameInstance gameInstance1 = new GameInstance();
        Genetics genetics = new Genetics();
        genetics.setPopulation(2);
        //gameInstance.setIndex(0);
        gameInstance.setTries(9);
        gameInstance.start();
        gameInstance.setGenetics(genetics);

        //gameInstance1.setIndex(0);
        gameInstance1.setTries(9);
        gameInstance1.start();
        gameInstance1.setGenetics(genetics);
    }
}
