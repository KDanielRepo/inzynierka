import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        /*Perceptron p = new Perceptron();
        p.getInputs().put(-0.5f,4f);

        Perceptron p1 = new Perceptron();
        p1.getInputs().put(-0.3f,8f);

        Perceptron p2 = new Perceptron();
        p2.getInputs().put(-0.5f,p.getOutput());
        p2.getInputs().put(0.5f,p1.getOutput());
        System.out.println(p2.activation());*/
        /*Brain brain = new Brain();
        List<Integer> list = new ArrayList<>();
        list.add(16);
        list.add(4);
        brain.createPerceptronMap(2,list);
        brain.getPerceptronMap().values().stream().forEach(m ->{
            System.out.println("Rozmiar warstwy: "+m.values().size());
            m.values().stream().forEach(o ->{
                Perceptron p = (Perceptron) o;
                o.getInputs().values().stream().forEach(e ->{
                    System.out.println("Wartość na wejsciu: "+e.toString());
                });
                System.out.println("Wartość na wyjściu: "+o.getOutput());
                System.out.println("Wartość aktywacji to: "+o.activation());
                System.out.println("++++++++++++++++++++++++++");
            });
            System.out.println("________________________________");
        });*/
        /*Float sum = 10f;
        Float lambda = 1.0507f;
        Float alpha = 1.6732f;
        Float output = 0f;
        if(sum<0){
            Double a = (alpha*Math.exp(sum)-alpha)*lambda;
            output = a.floatValue();
        }else{
            output = sum * lambda;
        }*/
        //Float output = Math.max(0, -15f);
        //double output = 1/(1+Math.exp(-64));
        /*Brain brain = new Brain();
        brain.createDefaultPerceptronMap();
        Multimap<Integer,Perceptron> p = brain.getPerceptronMap().get(0).stream().findFirst().get();
        Perceptron a = p.get(16).stream().findFirst().get();
        System.out.println(a.getInputs());*/

        List<Integer> a = Arrays.asList(1,2,3);
        List<Integer> b = new ArrayList<>();
        b.add(1);
        b.add(2);
        b.add(3);
        /*a.stream().forEach(e->{
            System.out.println(e);
        });
        for (int i = 0; i < a.size(); i++) {
            a.remove(i);
        }
        System.out.println(a.size());*/

        b.stream().forEach(e->{
            System.out.println(e);
        });
        b.clear();
        System.out.println(b.size());

    }
}
