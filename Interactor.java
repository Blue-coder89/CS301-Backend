import java.io.BufferedReader;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * Main Class to controll the program flow
 */
public class Interactor 
{

    public static Scanner sc = new Scanner(System.in);
    public static Connection connection = openConnection();

    //For storing a train information
    public static class station {
        String stationCode;
        int arrivalDay;
        String arrialTime;
        int departureDay;
        String departureTime;
    };

    public static Connection openConnection() 
    {

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

    public static void addTrains()
    {
        System.out.println("Choose one option");
        System.out.println("1. Add trains manually");
        System.out.println("2. Load trains from data/trains.txt file");

        int choice = sc.nextInt();
        sc.nextLine();
        if (choice==1)
        {
            System.out.println("Enter train number(atmost 5 digits):");
            String trainNum = sc.nextLine();
            System.out.println("Enter name of the train:");
            String trainName = sc.nextLine();

            String sqlQuery = String.format("CALL add_train(%s, '%s')", trainNum, trainName);
            try
            {
                Statement statement = connection.createStatement();
                statement.execute(sqlQuery);
                System.out.println("Train loaded successfully!");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                FileReader trainsList = new FileReader("./data/trains.txt");
                BufferedReader br = new BufferedReader(trainsList);
                String newLine;
                while((newLine = br.readLine())!=null)
                {
                    String trainNum = newLine;
                    String trainName = br.readLine();
                    String sqlQuery = String.format("CALL add_train(%s, '%s')", trainNum, trainName);
                    Statement statement = connection.createStatement();
                    statement.execute(sqlQuery);
                }
                br.close();
                System.out.println("Trains loaded successfully!");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        
    }

    public static void addStationNames() {
        try 
        {
            int choice;
            System.out.println("\n1. Add a station in interactive mode");
            System.out.println("2. Load stations via ./data/stations.txt file");
            System.out.println("\nYour Choice: ");
            choice = sc.nextInt();
            sc.nextLine();
            if (choice == 1) 
            {
                System.out.println("Please Enter Station Name:");
                String stationName = sc.nextLine();
                System.out.println("Please Enter Station Code(Upto 4 Characters)");
                String stationCode = sc.nextLine();
                String sql = String.format("INSERT INTO stations(station_name, station_code) VALUES('%s','%s')", stationName, stationCode);
                Statement statement = connection.createStatement();
                statement.execute(sql);
                System.out.println("Station Loaded");
            } 
            else 
            {
                String filePath = "./data/stations.txt";
                FileReader stationList = new FileReader(filePath);
                BufferedReader br = new BufferedReader(stationList);
                String newLine;
                while((newLine = br.readLine())!=null)
                {
                    StringTokenizer tokenizer = new StringTokenizer(newLine,":"); 
                    String stationName = tokenizer.nextToken();
                    String stationCode = tokenizer.nextToken();
                    String sql = String.format("INSERT INTO stations(station_name, station_code) VALUES('%s','%s')",
                            stationName, stationCode);
                    Statement statement = connection.createStatement();
                    statement.execute(sql);
                }
                br.close();
                System.out.println("Stations loaded successfully!");
            }
        } catch (Exception E) {
            System.out.println(E);
        }
    }

    public static void addTrainRoutes() {
        try 
        {
            System.out.println("Adding Train Route Mode\n");
            int choice;
            System.out.println("1. Add a train route in interactive mode");
            System.out.println("2. Add trains via file");
            choice = sc.nextInt();
            sc.nextLine();
            if (choice == 1) 
            {
                String trainNumber;
                int numberOfStations;
                ArrayList<station> path = new ArrayList<station>();
                System.out.println("Please Enter Train Number\n");
                trainNumber = sc.nextLine();
                System.out.println("Please Enter Number of stations in its path\n");
                numberOfStations = sc.nextInt();
                sc.nextLine();
                System.out.println("Please Enter the path as seen by the stations\n");
                for (int i = 0; i < numberOfStations; i++) 
                {
                    station s = new station();
                    System.out.println(String.format("Enter the station code for the %dth stop",i));
                    s.stationCode = sc.nextLine();
                    System.out.println("Enter the day of arrival(-1 if this station is source)");
                    s.arrivalDay = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter the time of arrival in HH:MM:SS format(-1 if this station is source)");
                    s.arrialTime = sc.nextLine();
                    System.out.println("Enter the day of departure(-1 if this station is destination)");
                    s.departureDay = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter the time of departure in HH:MM:SS format(-1 if this station is destination)");
                    s.departureTime = sc.nextLine();
                    path.add(s);
                }
                for (int i = 0; i < numberOfStations ; i++)
                {
                    for (int j = i + 1; j < numberOfStations; j++) 
                    {
                        station a = path.get(i);
                        station b = path.get(j);
                        String sql = String.format("INSERT INTO schedules VALUES('%s','%s',%d,%d,%d,'%s','%s')",
                                a.stationCode, b.stationCode, trainNumber, a.departureDay, b.arrivalDay,
                                a.departureTime, b.arrialTime);
                        Statement statement = connection.createStatement();
                        ResultSet r = statement.executeQuery(sql);
                        r.next();
                    }
                }
                System.out.println("Route added successfully");
            } 
            else {
                String filePath = "";
                InputStream is = new FileInputStream(filePath);
                InputStreamReader inputStream = new InputStreamReader(is);
                BufferedReader fileScan = new BufferedReader(inputStream);
                while (true) {
                    String query = fileScan.readLine();
                    StringTokenizer tokenizer = new StringTokenizer(query);
                    int trainNumber = Integer.parseInt(tokenizer.nextToken());
                    int numberOfStations = Integer.parseInt(tokenizer.nextToken());
                    ArrayList<station> path = new ArrayList<station>();
                    for (int i = 0; i < numberOfStations; i++) {
                        query = fileScan.readLine();
                        tokenizer = new StringTokenizer(query);
                        station s = new station();
                        s.stationCode = tokenizer.nextToken();
                        s.arrivalDay = Integer.parseInt(tokenizer.nextToken());
                        s.arrialTime = tokenizer.nextToken();
                        s.departureDay = Integer.parseInt(tokenizer.nextToken());
                        s.departureTime = tokenizer.nextToken();
                        path.add(s);
                    }
                    for (int i = 0; i < numberOfStations - 1; i++)
                        for (int j = i + 1; j < numberOfStations; j++) {
                            station a = path.get(i);
                            station b = path.get(j);
                            String sql = String.format("INSERT INTO schedules VALUES('%s','%s',%d,%d,%d,'%s','%s')",
                                    a.stationCode, b.stationCode, trainNumber, a.departureDay, b.arrivalDay,
                                    a.departureTime, b.arrialTime);
                            Statement statement = connection.createStatement();
                            statement.execute(sql);
                        }

                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void search() 
    {
        System.out.println("Please Enter ta\rting point of Journey: ");
        String start = sc.next();
        System.out.println("Please Enter end point of Journey: ");
        String destination = sc.next();

    }

    public static void releaseIntoBooking() {
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

    public static void bookTickets() 
    {

    }

    
    public static void main(String[] args) throws IOException 
    {
        System.out.println("Choose one option\n");
        while (true) {
            int choice;
            System.out.println("1. Add new Trains");
            System.out.println("2. Add new Stations");
            System.out.println("3. Add train routes");
            System.out.println("4. Search trains between stations");
            System.out.println("5. Release trains for booking");
            System.out.println("6. Book tickets");
            System.out.println("7. QUIT \n");
            System.out.println("PLEASE ENTER YOUR CHOICE: ");
            choice = sc.nextInt();
            switch (choice)
            {
                case 1:
                    addTrains();
                    break;
                case 2:
                    addStationNames();
                    break;
                case 3:
                    addTrainRoutes();
                    break;
                case 4:
                    search();
                    break;
                case 5:
                    releaseIntoBooking();
                    break;
                case 6:
                    bookTickets();
                    break;
                default:
                    break;
            }
            if (choice==7)
            {
                break;
            }

        }
    }
}
