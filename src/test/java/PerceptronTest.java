import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PerceptronTest {

    @Test
    public void sumTest(){
        Perceptron p = createPerceptron();
        Float a = 10f;
        p.normalize(a);
        System.out.println(a);
        long start = System.nanoTime();
        p.calculateSum();
        long end = System.nanoTime();
        System.out.println("wynik: "+(end-start)*0.000000001);
    }

    @Test
    public void replacePerceptronValueTest(){
        Perceptron p1 = createPerceptron();
        System.out.println(p1.getInputs());
        long start = System.nanoTime();
        p1.replacePerceptronValue(0,4f);
        long end = System.nanoTime();
        System.out.println("wynik: "+(end-start)*0.000000001);
        System.out.println(p1.getInputs());
        Assert.assertEquals(p1.getInput(0),new Float(4));
    }

    @Test
    public void replacePerceptronWeightTest(){
        Perceptron p1 = createPerceptron();
        System.out.println(p1.getInputs());
        long start = System.nanoTime();
        p1.replacePerceptronWeight(0,5f);
        long end = System.nanoTime();
        System.out.println("wynik: "+(end-start)*0.000000001);
        System.out.println(p1.getInputs());

        Assert.assertEquals(p1.getWeight(0),new Float(5));
    }

    private Perceptron createPerceptron(){
        Perceptron p = new Perceptron();
        p.getInputs().put(1f,1f);
        p.getInputs().put(2f,2f);
        p.getInputs().put(3f,3f);
        return p;
    }
}
