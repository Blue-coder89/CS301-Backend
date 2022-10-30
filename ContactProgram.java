import java.sql.*;
public class ContactProgram {
    public static void main(String[] args){
        String jdbcURL = "jdbc:postgresql://localhost:5432/railways"; // jdbc url for postgres database
        String username = "postgres";
        String password = "";
        try{
            Class.forName("org.postgresql.Driver");
            Connection connection  = DriverManager.getConnection(jdbcURL,username,password);
            System.out.println("Connected to PostgresSQL server");
            String sql = "SELECT book_tickets(2 , 'A' , 13111 , '2022-10-29' , 'true' ,'A', 'S' );";
            Statement statement = connection.createStatement();
            ResultSet r = statement.executeQuery(sql);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in connecting to postgressql server");
            System.exit(0);
        }
    }
}