import java.io.BufferedReader;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;

/**
 * Main Class to controll the program flow
 */
public class Interactor {

    public static Scanner sc = new Scanner(System.in);

    public static class station {
        String stationNumber;
        int arrivalDay;
        String arrialTime;
        int departureDay;
        String departureTime;
    };

    public static Connection openConnection() {

        Properties properties = new Properties();
        Connection connection = null;

        String path = "./database.properties";

        try (FileInputStream fin = new FileInputStream(path);) {

            properties.load(fin);

            try {
                Class.forName(properties.getProperty("JDBC_DRIVER"));

                // opening connection
                connection = (Connection) DriverManager.getConnection(properties.getProperty("DB_URL"),
                        properties.getProperty("USER"), properties.getProperty("PASS"));

            } catch (ClassNotFoundException e) {
                System.out.println("This is from openConnection method");
                e.printStackTrace();
            } catch (Exception f) {
                System.out.println("This is from openConnection method");
                f.printStackTrace();
            }
        } catch (IOException io) {
            System.out.println("This is from openConnection method");
            io.printStackTrace();
        }
        return connection;
    }

    public static StringTokenizer sqlTickets(Connection connection,
            int numPassengers,
            String choice,
            int train_no,
            String date,
            String firstTry,
            String commaSepNames) {
        StringTokenizer resultTokens = new StringTokenizer("");
        String sql = String.format("SELECT book_tickets(%d , '%s' , %d , '%s' , '%s' ,%s );",
                numPassengers,
                choice,
                train_no,
                date,
                firstTry,
                commaSepNames);
        try {
            Statement statement = connection.createStatement();
            ResultSet r = statement.executeQuery(sql);
            r.next();
            resultTokens = new StringTokenizer(r.getString("book_tickets"), ",{}");
            if (resultTokens.hasMoreTokens() == false) {
                System.out.println(r.getString("book_tickets"));
            }
            // System.out.println(r.getString("book_tickets"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultTokens;
    }

    public void addTrainInteractive() {
        try {
            String trainName;
            int trainNumber;
            int numberOfStations;
            ArrayList<station> path = new ArrayList<station>();
            System.out.println("Please Enter Train Name\n");
            trainName = sc.nextLine();
            System.out.println("Please Enter Train Number\n");
            trainNumber = sc.nextInt();
            System.out.println("Please Enter Number of stations in its path\n");
            numberOfStations = sc.nextInt();
            System.out.println("Please Enter the path as seen by the stations\n");
            for (int i = 0; i < numberOfStations; i++) {
                System.out.println(
                        "Please Enter the station number,arrial day,arrival time,departure day,departure time\n");
                station s = new station();
                s.stationNumber = sc.nextLine();
                s.arrivalDay = sc.nextInt();
                s.arrialTime = sc.nextLine();
                s.departureDay = sc.nextInt();
                s.departureTime = sc.nextLine();
                path.add(s);
            }
            for (int i = 0; i < numberOfStations - 1; i++)
                for (int j = i + 1; j < numberOfStations; j++) {
                    station a = path.get(i);
                    station b = path.get(j);
                    String sql = String.format("INSERT INTO schedules VALUES('%s','%s',%d,%d,%d,'%s','%s')",
                            a.stationNumber, b.stationNumber, trainNumber, a.departureDay, b.arrivalDay,
                            a.departureTime, b.arrialTime);
                    Statement statement = connection.createStatement();
                    ResultSet r = statement.executeQuery(sql);
                    r.next();
                }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void addTrainByFile() {

    }

    public void addStationNames() {
        try {
            System.out.println("Please Enter Station Name and Station Code\n");
            String stationName = sc.nextLine();
            String stationCode = sc.nextLine();
            String sql = String.format("INSERT INTO schedules VALUES('%s','%s')",stationName,stationCode
                    );
            Statement statement = connection.createStatement();
            ResultSet r = statement.executeQuery(sql);
            r.next();

        } catch (Exception E) {
            System.out.println(E);
        }
    }

    public void Search() {
        System.out.println("Please Enter ta\rting point of Journey: ");
        String start = sc.next();
        System.out.println("Please Enter end point of Journey: ");
        String destination = sc.next();

    }

    public void bookTickets() {
        try {
            String exitCode = "old";
            // Taking booking information
            System.out.println("Enter Train Number: ");
            Integer train_no = sc.nextInt();
            System.out.println("Enter date of Journey: ");
            String date = sc.next();
            System.out.println("Enter number of Passengers: ");
            Integer numPassenger = sc.nextInt();

            ArrayList<String> passengerNames = new ArrayList<String>();
            System.out.println("Enter Passenger Names: ");
            for (int i = 0; i < numPassenger; i++) {
                String name = sc.next();
                passengerNames.add(name);
            }
            System.out.println("Enter choice of coach: ");
            String choice = sc.next();

            // Executing query in SQL
            String firstTry = "true";
            String commaSepNames = passengerNames.toString().replace("[", "'").replace("]", "'").replace(" ", "'")
                    .replace(",", "',");
            StringTokenizer resultTokens = sqlTickets(connection, numPassenger, choice, train_no, date, firstTry,
                    commaSepNames);

            exitCode = resultTokens.nextToken();
            if (exitCode.equals("-3")) {
                firstTry = "false";
                resultTokens = sqlTickets(connection, numPassenger, choice, train_no, date, firstTry, commaSepNames);
                exitCode = resultTokens.nextToken();
            }

            switch (exitCode) {
                case "-1":
                    System.out.println(
                            "Booking Failed: Train not yet released into the booking system for the mentioned date or train number is invalid\n");
                    break;
                case "-2":
                    System.out.println("Booking Failed: Not enough Tickets\n");
                    break;
                case "0":
                    String PNR = resultTokens.nextToken();
                    System.out.println(String.format("Tickets Booked with PNR %s", PNR));
                    System.out.println(String.format("Train No: %05d    Departure Date: %s", train_no, date));
                    System.out.println("");
                    System.out.println("Passenger Name    Coach  Type  Berth Number");
                    for (int i = 0; i < numPassenger; i++) {
                        String berth = resultTokens.nextToken();
                        String coach = resultTokens.nextToken();
                        String berthType = resultTokens.nextToken();

                        System.out.println(
                                String.format("%-16s  %-5s  %-4s  %s", passengerNames.get(i), coach, berthType, berth));
                    }
                    System.out.println("\n");
                    break;
                default:
                    System.out.println("Some unknown error has occured. Please contact Admin with Exit Code: "
                            + firstTry + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void releaseIntoBooking() {
        System.out.println("Choose one option\n");
        System.out.println("1. Read through files");
        System.out.println("2. Enter details manually");
        System.out.println("Your Choice:");
        int choice = sc.nextInt();
        if (choice == 2) {
            System.out.println("Enter train number");
            System.out.println("Note: Train must be present in the trains relation");
            int trainNum = sc.nextInt();
            System.out.println("Enter date of departure from the source in YYYY-MM-DD format");
            String depDate = sc.nextLine();
            System.out.println("Enter total number of AC coaches on that day");
            int acNum = sc.nextInt();
            System.out.println("Enter total number of Sleeper coaches on that day");
            int slNum = sc.nextInt();
        }
    }

    public static Connection connection = openConnection();
       

    void main(String[] args) throws IOException {
        System.out.println("Choose one option");
        while (true) {
            int choice;
            System.out.println("1. ADD a new Train Route in Interactive mode\n");
            System.out.println("2. ADD new Train Route by file\n");
            System.out.println("3. ADD new Station Names\n");
            System.out.println("4. SEARCH TRAINS BETWEEN STATIONS\n");
            System.out.println("5. RELEASE TRAINS FOR BOOKING");
            System.out.println("6. BOOK JOURNEY TICEKTS");
            System.out.println("7. QUIT \n");
            System.out.println("PLEASE ENTER YOUR CHOICE: \n");
            choice = sc.nextInt();
            if (choice == 1) {
            } else if (choice == 2) {
            } else
                break;

        }
    }
}
