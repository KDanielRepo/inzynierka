import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

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
        Dendrite dendrite = new Dendrite();
        dendrite.setValue(1f);
        dendrite.setWeight(1f);

        Dendrite dendrite2 = new Dendrite();
        dendrite2.setValue(1f);
        dendrite2.setWeight(1f);

        Dendrite dendrite3 = new Dendrite();
        dendrite3.setValue(1f);
        dendrite3.setWeight(1f);

        p.setInputs(Arrays.asList(dendrite,dendrite2,dendrite3));
        return p;
    }
}
