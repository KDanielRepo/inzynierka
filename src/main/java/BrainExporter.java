import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//<brain>
//  <layer>
//      <perceptron>
//          <input>
//              <weight = x>
//              <value = y>
//          </input>
//          <input>
//              <weight = x>
//              <value = y>
//          </input>
//      <perceptron>
//  </layer>
//</brain>
public class BrainExporter {
    public void exportBrainToCsf(File file, Brain brain) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        String text = "";
        for (int i = 0; i < brain.getPerceptronCount(); i++) {
            for (int j = 0; j < brain.getGivenPerceptron(i).getWeights().size(); j++) {
                text += brain.getGivenPerceptron(i).getWeight(j);
                text += "'";
            }
            text += ";";
        }
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(text);
        bufferedWriter.flush();
        bufferedWriter.close();
        fileWriter.close();
    }

    public void importBrainFromCsf(File file, Brain brain) throws IOException {
        //1'2'3'4'5';1'2'3;
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> weights = new ArrayList<>();
        String text = bufferedReader.readLine();
        weights.addAll(Arrays.asList(text.split(";")));
        List<String> perceptronWeights;

        for (int i = 0; i < weights.size(); i++) {
            perceptronWeights = Arrays.asList(weights.get(i).split("'"));
            List<Float> floats = new ArrayList<>();
            for (int j = 0; j < perceptronWeights.size(); j++) {
                floats.add(Float.parseFloat(perceptronWeights.get(j)));
            }
            brain.getGivenPerceptron(i).replacePerceptronWeights(floats);
        }
    }


}
