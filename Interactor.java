import java.io.BufferedReader;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileReader;

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

    public static StringTokenizer bookTickets(
        int numPassengers,
        char choice,
        int train_no,
        String date,
        String firstTry,
        String commaSepNames)
    {
        StringTokenizer resultTokens = new StringTokenizer("");
        String sql = String.format("SELECT book_tickets(%d , '%s' , %d , '%s' , '%s' ,%s );",
            numPassengers,
            choice,
            train_no,
            date,
            firstTry,
            commaSepNames);
        try
        {
            Statement statement = connection.createStatement();
            System.out.println(sql);
            ResultSet r = statement.executeQuery(sql);
            r.next();
            resultTokens = new StringTokenizer(r.getString("book_tickets"),",{}");
            if (resultTokens.hasMoreTokens()==false)
            {
                System.out.println(r.getString("book_tickets"));
            }
            //System.out.println(r.getString("book_tickets"));
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return resultTokens;
    }
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
            System.out.println("2. Add train routes via ./data/routes.txt file");
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
                        String sql = String.format("INSERT INTO schedules VALUES('%s','%s',%s,%d,%d,'%s','%s')",
                                a.stationCode, b.stationCode, trainNumber, a.departureDay, b.arrivalDay,
                                a.departureTime, b.arrialTime);
                        Statement statement = connection.createStatement();
                        statement.execute(sql);
                    }
                }
                System.out.println("Route added successfully");
            } 
            else 
            {
                String filePath = "./data/routes.txt";
                FileReader routesList = new FileReader(filePath);
                BufferedReader br = new BufferedReader(routesList);
                String newLine;
                while((newLine = br.readLine())!=null)
                {
                    String trainNumber;
                    int numberOfStations;
                    ArrayList<station> path = new ArrayList<station>();
                    trainNumber = newLine;
                    numberOfStations = Integer.parseInt(br.readLine());
                    for (int i = 0; i < numberOfStations; i++) 
                    {
                        station s = new station();
                        StringTokenizer routeTokens = new StringTokenizer(br.readLine());
                        s.stationCode = routeTokens.nextToken();
                        s.arrivalDay = Integer.parseInt(routeTokens.nextToken());
                        s.arrialTime = routeTokens.nextToken();
                        s.departureDay = Integer.parseInt(routeTokens.nextToken());
                        s.departureTime = routeTokens.nextToken();
                        path.add(s);
                    }
                    for (int i = 0; i < numberOfStations ; i++)
                    {
                        for (int j = i + 1; j < numberOfStations; j++) 
                        {
                            station a = path.get(i);
                            station b = path.get(j);
                            String sql = String.format("INSERT INTO schedules VALUES('%s','%s',%s,%d,%d,'%s','%s')",
                                    a.stationCode, b.stationCode, trainNumber, a.departureDay, b.arrivalDay,
                                    a.departureTime, b.arrialTime);
                            Statement statement = connection.createStatement();
                            statement.execute(sql);
                        }
                    }
                    
                }
                br.close();
                System.out.println("Routes added successfully");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void search() 
    {
        System.out.println("Please Enter starting point of Journey: ");
        String start = sc.next();
        System.out.println("Please Enter end point of Journey: ");
        String destination = sc.next();
        try
        {
            StringTokenizer resultTokens = new StringTokenizer("");
            // checking direct paths
            String sql = String.format(
                "SELECT S.train_number,S.from_station,S.SDT,S.to_station,S.DAT FROM schedules as S WHERE S.from_station = '%s' and S.to_station = '%s' and S.source_day=1",start,destination
            );
            Statement statement = connection.createStatement();
            System.out.println(sql);
            ResultSet r = statement.executeQuery(sql);
            r.next();
            resultTokens = new StringTokenizer(r.getString("train_number"),",{}");
            if (resultTokens.hasMoreTokens()==false)
            {
                System.out.println(r.getString("train_number"));
            }


            // checking single hop paths
            sql = String.format(
                "SELECT S1.train_number,S1.from_station,S1.SDT,S1.to_station,S1.DAT,S2.train_number,S2.from_station,S2.SDT,S2.to_station,S2.DAT FROM schedules as S1, schedules as S2 WHERE S1.train_number!=S2.train_number AND S1.from_station = '%s' AND S1.to_station = S2.from_station AND S2.to_station = '%s' AND S1.source_day = 1 AND (S2.SDT - S1.DAT <= '04:00:00') AND (S2.SDT-S1.DAT>='00:00:00');",start,destination
            );
            statement = connection.createStatement();
            System.out.println(sql);
            r = statement.executeQuery(sql);
            r.next();
            resultTokens = new StringTokenizer(r.getString("train_number"),",{}");
            if (resultTokens.hasMoreTokens()==false)
            {
                System.out.println(r.getString("train_number"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void releaseIntoBooking() {
        try
        {
            System.out.println("Choose one option\n");
            System.out.println("1. Enter details manually");
            System.out.println("2. Read through the file ./data/Trainschedule.txt");
            
            System.out.println("Your Choice:");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 1) 
            {
                System.out.println("Enter train number");
                System.out.println("Note: Train must be present in the trains relation");
                String trainNum = sc.nextLine();
                System.out.println("Enter date of departure from the source in YYYY-MM-DD format");
                String depDate = sc.nextLine();
                System.out.println("Enter total number of AC coaches on that day");
                int acNum = sc.nextInt();
                sc.nextLine();
                System.out.println("Enter total number of Sleeper coaches on that day");
                int slNum = sc.nextInt();
                sc.nextLine();
                String sql = String.format("CALL release_into_booking(%s, '%s', %d, %d)",trainNum, depDate, acNum, slNum);
                Statement statement = connection.createStatement();
                statement.execute(sql);
                System.out.println("Released for booking successfully!");
            }
            else
            {
                String filePath = "./data/Trainschedule.txt";
                FileReader routesList = new FileReader(filePath);
                BufferedReader br = new BufferedReader(routesList);
                String newLine;
                while(true)
                {
                    newLine=br.readLine();
                    if (newLine.equals("#"))
                    {
                        break;
                    }
                    StringTokenizer runsTokens = new StringTokenizer(newLine);
                    String trainNum =  runsTokens.nextToken();
                    String depDate = runsTokens.nextToken();
                    int acNum = Integer.parseInt(runsTokens.nextToken());
                    int slNum = Integer.parseInt(runsTokens.nextToken());
                    String sql = String.format("CALL release_into_booking(%s, '%s', %d, %d)",trainNum, depDate, acNum, slNum);
                    Statement statement = connection.createStatement();
                    statement.execute(sql);
                }
                br.close();
                System.out.println("All the trains released for booking successfully!");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void bookTicketsInteractive() 
    {
        try{
        System.out.println("Please Enter Number of passengers");
        int numPassengers = sc.nextInt();
        sc.nextLine();
        System.out.println("Please Enter Your Choice(A/S)");
        String choice = sc.nextLine();
        System.out.println("Please Enter the train number");
        int train_no = sc.nextInt();
        sc.nextLine();
        System.out.println("Please Enter the date");
        String date = sc.nextLine();
        String firstTry = "false";
        ArrayList<String> passengerNames = new ArrayList<String>();
        for(int i = 0;i<numPassengers;i++)
            {
                System.out.println(String.format("Please Enter the name of %d numbered passeneger",i+1));
                String name = sc.nextLine();
                passengerNames.add(name);
            }
        String commaSeperatedNames = passengerNames.toString().replace("[", "'").replace("]", "'").replace(" ","'").replace(",","',");
        StringTokenizer resultTokens = bookTickets(numPassengers,choice.charAt(0), train_no, date, firstTry, commaSeperatedNames);
        String exitCode = resultTokens.nextToken();
        switch (exitCode) {
            case "-1":
                System.out.println("Booking Failed: Train not yet released into the booking system for the mentioned date or train number is invalid\n");
                break;
            case "-2":
                System.out.println("Booking Failed: Not enough Tickets\n");
                break;
            case "0":
                String PNR = resultTokens.nextToken();
                System.out.println(String.format("Tickets Booked with PNR %s", PNR));
                System.out.println(String.format("Train No: %05d    Departure Date: %s",train_no, date));
                System.out.println("");
                System.out.println("Passenger Name    Coach  Type  Berth Number");
                for (int i=0;i<numPassengers;i++)
                {
                    String berth = resultTokens.nextToken();
                    String coach = resultTokens.nextToken();
                    String berthType =  resultTokens.nextToken();

                    System.out.println(String.format("%-16s  %-5s  %-4s  %s",passengerNames.get(i),coach,berthType,berth));
                }
                System.out.println("\n");
                break;
            default:
                System.out.println("Some unknown error has occured. Please contact Admin with Exit Code: "+firstTry+exitCode);
        }
        
        }
        catch(Exception e){
            e.printStackTrace();
        }
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
            sc.nextLine();
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
                    bookTicketsInteractive();
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


//  SELECT S.train_number,S.from_station,S.SDT,S.to_station,S.DAT FROM schedules as S WHERE S.from_station = 'CDG' and S.to_station = 'NDLS' and S.source_day=1

// SELECT S1.train_number,S1.from_station,S1.SDT,S1.to_station,S1.DAT,S2.train_number,S2.from_station,S2.SDT,S2.to_station,S2.DAT FROM schedules as S1, schedules as S2 WHERE S1.train_number!=S2.train_number AND S1.from_station = 'CDG' AND S1.to_station = S2.from_station AND S2.to_station = 'NDLS' AND S1.source_day = 1 AND (S2.SDT - S1.DAT <= '04:00:00') AND (S2.SDT-S1.DAT>='00:00:00');