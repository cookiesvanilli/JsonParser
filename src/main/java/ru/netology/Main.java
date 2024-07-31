package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        //CSV-JSON
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, csvFileName);
        String csvJson = listToJson(list);
        String csvJsonFile = "data.json";
        writeString(csvJson, csvJsonFile);

        //XML-JSON
        String xmlFileName = "data.xml";
        List<Employee> list2 = parseXML(xmlFileName);
        String xmlJson = listToJson(list2);
        String xmlJsonFile = "data2.json";
        writeString(xmlJson, xmlJsonFile);


    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) throws FileNotFoundException {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String jsonFile) {
        try (FileWriter fileWriter = new FileWriter(jsonFile)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String xmlFile) throws Exception {
        List<Employee> employeeList = new ArrayList<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(xmlFile));
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) node;
                Employee employee = new Employee();

                employee.setId(Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent()));
                employee.setFirstName(eElement.getElementsByTagName("firstName").item(0).getTextContent());
                employee.setLastName(eElement.getElementsByTagName("lastName").item(0).getTextContent());
                employee.setCountry(eElement.getElementsByTagName("country").item(0).getTextContent());
                employee.setAge(Integer.parseInt(eElement.getElementsByTagName("age").item(0).getTextContent()));

                employeeList.add(employee);
            }
        }
        return employeeList;
    }


}