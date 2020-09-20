import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

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
    public void exportBrainToXml(File file, Brain brain) throws IOException, JAXBException {
        if(!file.exists()){
            file.createNewFile();
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(Brain.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
        marshaller.marshal(brain,file);
    }

    public Brain importBrainFromXml(File file, Brain brain) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Brain.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return brain = (Brain) unmarshaller.unmarshal(file);
    }


}
