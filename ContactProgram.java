import java.sql.*;

public class ContactProgram {
    public static void main(String[] args){
        String jdbcURL = "jdbc:postgresql://localhost:5432/railways"; // jdbc url for postgres database
        String username = "postgres";
        String password = "";
        try{
            Connection connection  = DriverManager.getConnection(jdbcURL,username,password);
            System.out.println("Connected to PostgresSQL server");
            String sql = "SELECT book_tickets(2 , 'A' , 13111 , '2022-10-29' , 'true' ,'Arshdeep', 'Shahnawaz' );";
            Statement statement = connection.createStatement();
            ResultSet r = statement.executeQuery(sql);
        
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error in connecting to postgressql server");
            e.printStackTrace();
        }
    }
}