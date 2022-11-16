import java.io.BufferedReader;
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

class QueryRunner implements Runnable
{
    //  Declare socket for client access
    protected Socket socketConnection;

    public QueryRunner(Socket clientSocket)
    {
        this.socketConnection =  clientSocket;
    }
    public static Connection openConnection() {

        Properties properties = new Properties();
        Connection connection = null;

        String path = "./database.properties";

        try(FileInputStream fin = new FileInputStream(path);) {

            properties.load(fin);

            try {
                Class.forName(properties.getProperty("JDBC_DRIVER"));

//              opening connection
                connection = (Connection) DriverManager.getConnection(properties.getProperty("DB_URL"),properties.getProperty("USER"),properties.getProperty("PASS"));

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

    public static StringTokenizer bookTickets(Connection connection,
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

    public void run()
    {
      try
        {
            //  Reading data from client
            
            InputStreamReader inputStream = new InputStreamReader(socketConnection
                                                                  .getInputStream()) ;
            BufferedReader bufferedInput = new BufferedReader(inputStream) ;
            OutputStreamWriter outputStream = new OutputStreamWriter(socketConnection
                                                                     .getOutputStream()) ;
            BufferedWriter bufferedOutput = new BufferedWriter(outputStream) ;
            PrintWriter printWriter = new PrintWriter(bufferedOutput, true) ;
            
            String clientCommand = "" ;
            String queryInput = "" ;
            Connection connection  = openConnection();
            while(true)
            {
                // Read client query
                clientCommand = bufferedInput.readLine();
                // System.out.println("Recieved data <" + clientCommand + "> from client : " 
                //                      + socketConnection.getRemoteSocketAddress().toString());
                //                      System.out.println("Start");

                //  Tokenize here
                StringTokenizer tokenizer = new StringTokenizer(clientCommand);
                queryInput = tokenizer.nextToken();
                // System.out.println(queryInput);
                // System.out.println("End");
                if(queryInput.equals("#"))
                {
                    String returnMsg = "Connection Terminated - client : " 
                                        + socketConnection.getRemoteSocketAddress().toString();
                    System.out.println(returnMsg);
                    printWriter.println("ALL REQUESTS PROCESSED FOR THE USER"); 
                    inputStream.close();
                    bufferedInput.close();
                    outputStream.close();
                    bufferedOutput.close();
                    printWriter.close();
                    socketConnection.close();
                    connection.close(); 
                    //System.out.println("Count: "+cnt);

                    return;
                }
                String exitCode="old";
                //-------------- your DB code goes here----------------------------
                try
                {
                    int numPassengers = Integer.parseInt(queryInput);
                    ArrayList<String> passengerNames = new ArrayList<String>();
                    for (int i=0;i<numPassengers;i++)
                    {
                        String tmpName=tokenizer.nextToken();
                        
                        if (i!=(numPassengers-1))
                        {
                            tmpName=tmpName.substring(0,tmpName.length()-1);
                        }
                        passengerNames.add(tmpName);
                    }
                    int train_no = Integer.parseInt(tokenizer.nextToken()) ;
                    String date = tokenizer.nextToken();
                    String choice=tokenizer.nextToken();
                    String firstTry= "true";
                    String commaSepNames = passengerNames.toString().replace("[", "'").replace("]", "'").replace(", ",", '").replace(",","',");
                    StringTokenizer resultTokens = bookTickets(connection, numPassengers, choice.charAt(0), train_no, date, firstTry, commaSepNames);

                    exitCode = resultTokens.nextToken();
                    if (exitCode.equals("-3"))
                    {
                        
                        firstTry = "false";
                        resultTokens = bookTickets(connection, numPassengers, choice.charAt(0), train_no, date, firstTry, commaSepNames);  
                        exitCode = resultTokens.nextToken();
                    }

                    
                    switch (exitCode) {
                        case "-1":
                            printWriter.println("Booking Failed: Train not yet released into the booking system for the mentioned date or train number is invalid\n");
                            break;
                        case "-2":
                            printWriter.println("Booking Failed: Not enough Tickets\n");
                            break;
                        case "0":
                            String PNR = resultTokens.nextToken();
                            printWriter.println(String.format("Tickets Booked with PNR %s", PNR));
                            printWriter.println(String.format("Train No: %05d    Departure Date: %s",train_no, date));
                            printWriter.println("");
                            printWriter.println("Passenger Name    Coach  Type  Berth Number");
                            for (int i=0;i<numPassengers;i++)
                            {
                                String berth = resultTokens.nextToken();
                                String coach = resultTokens.nextToken();
                                String berthType =  resultTokens.nextToken();

                                printWriter.println(String.format("%-16s  %-5s  %-4s  %s",passengerNames.get(i),coach,berthType,berth));
                            }
                            printWriter.println("\n");
                            break;
                        default:
                            printWriter.println("Some unknown error has occured. Please contact Admin with Exit Code: "+firstTry+exitCode);
                    }
                    
                //    throw new InterruptedException("Error in Queries");
                } 
                catch (Exception e)
                {
                    System.out.println(clientCommand);
                    e.printStackTrace();
                }
                // responseQuery = "******* Dummy result ******";

                // //----------------------------------------------------------------
                
                // //  Sending data back to the client
                
                // // System.out.println("\nSent results to client - " 
                // //                     + socketConnection.getRemoteSocketAddress().toString() );
                
            }

        }
        catch(IOException e)
        {
            return;
        } catch (SQLException e) {
            
            e.printStackTrace();
        }
    }
}

/**
 * Main Class to controll the program flow
 */
public class ServiceModule 
{
    static int serverPort = 7005;
    static int numServerCores = 90 ;
    //------------ Main----------------------
    public static void main(String[] args) throws IOException 
    {    
        // Creating a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(numServerCores);
        
        //Creating a server socket to listen for clients
        ServerSocket serverSocket = new ServerSocket(serverPort); //need to close the port
        Socket socketConnection = null;
        
        // Always-ON server
        while(true)
        {
            System.out.println("Listening port : " + serverPort 
                                + "\nWaiting for clients...");
            socketConnection = serverSocket.accept();   // Accept a connection from a client
            System.out.println("Accepted client :" 
                                + socketConnection.getRemoteSocketAddress().toString() 
                                + "\n");
            //  Create a runnable task
            Runnable runnableTask = new QueryRunner(socketConnection);
            //  Submit task for execution   
            executorService.submit(runnableTask);   
        }

    }
}

