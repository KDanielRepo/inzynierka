import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

@RunWith(JUnit4.class)
public class BrainExporterTest {

    @Test
    public void exportToCsfTest(){
        Brain brain = createBrain();
        BrainExporter brainExporter = new BrainExporter();
        try {
            brainExporter.exportBrainToCsf(new File("brainWeight.csf"),brain);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void importFromCsfTest(){
        Brain brain = createBrain();
        BrainExporter brainExporter = new BrainExporter();
        try {
            brainExporter.importBrainFromCsf(new File("brainWeight.csf"),brain);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Brain createBrain(){
        Brain brain = new Brain();
        brain.createDefaultPerceptronMap();
        return brain;
    }
}
