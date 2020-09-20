import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

@RunWith(JUnit4.class)
public class BrainExporterTest {

    @Test
    public void exportToXmlTest(){

    }

    @Test
    public void importFromXmlTest(){

    }

    private Brain createBrain(){
        Brain brain = new Brain();
        brain.createDefaultPerceptronMap();
        return brain;
    }
}
