import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    static StringBuilder stringBuilder = new StringBuilder();
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        File file = new File("data.csv");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Employee> list = parseCSV(columnMapping, fileName);

        List<Employee> employees = parseXML("data.xml");
        String json1 = listToJson(employees);
        writeString(json1);



    }

    static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }

    static void writeString(String json) {

        try (FileWriter file = new FileWriter("data.json")) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static List<Employee> parseXML(String name) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(name));
        Node root = doc.getDocumentElement();
        read(root);
        for (int i=0 ; i<stringBuilder.length(); i++){
            if (stringBuilder.charAt(0) == ',') {
            stringBuilder.deleteCharAt(0);
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        String str = String.valueOf(stringBuilder);
        String[] strings = str.split(",");
        for (int i = 0; i<strings.length; i++){
            String str1 = strings[0];
            String[] str2 = str1.split(" ");
            int age = Integer.parseInt(str2[0]);
            String country = str2[1];
            String firstname = str2[2];
            int id = Integer.parseInt(str2[3]);
            String lastname = str2[4];
            list.add(new Employee(id,firstname,lastname,country,age));
        }
        return list;
    }
    private  static void read(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                NamedNodeMap map = element.getAttributes();
                for (int a = 0; a < map.getLength(); a++) {
                     String value = map.item(a).getNodeValue();
                     stringBuilder.append(value);
                     stringBuilder.append(" ");
                }
                stringBuilder.append(",");
                read(node_);
            }

        }

    }



}






