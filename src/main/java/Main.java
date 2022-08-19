
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws JAXBException, ClassNotFoundException, SQLException, IllegalAccessException {
        JAXBContext context = JAXBContext.newInstance(Dataset.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Dataset dataset = (Dataset) unmarshaller.unmarshal(new File("src/main/java/dataset.xml"));
        List<Employee> employeeList = dataset.getEmployeeList();

        //Creating a JDBC connection and storing the values
        String url = "jdbc:postgresql://localhost:5432/EmployeesManager";
        String userName = "postgres";
        String password = "Kyouma#001";
        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(url, userName, password);

        for(Employee emp : employeeList){
            //String query = "INSERT INTO employees (first_name, last_name, email, gender, password) VALUES (?,?,?,?,?)";
            Class<?> clap = Employee.class;
            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO employees (");
            for(Field field: clap.getDeclaredFields()){
                field.setAccessible(true);
                if(field.getName().equals("id")){
                    continue;
                }
                if(field.isAnnotationPresent(XmlElement.class)){
                    query.append(field.getAnnotation(XmlElement.class).name()).append(",");
                }else{
                    query.append(field.getName()).append(",");
                }
            }
            query.deleteCharAt(query.length()-1);
            query.append(") VALUES (");
            for(Field field: clap.getDeclaredFields()){
                field.setAccessible(true);
                if(field.getName().equals("id")){
                    continue;
                }
                query.append("'").append(field.get(emp)).append("'").append(",");
            }
            query.deleteCharAt(query.length()-1);
            query.append(");");


            PreparedStatement preparedStatement = con.prepareStatement(query.toString());
            preparedStatement.executeUpdate();
        }
    }
}
