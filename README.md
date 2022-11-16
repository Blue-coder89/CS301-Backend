# Backend Part

## Opening postgres and loading the database
- Load the Database from [ticketreservation repository](https://github.com/skhan-org/TicketReservationSystem).
- Open VSCODE.
- Clone the repository.
- Type ``` sudo -s -u postgres ``` in terminal.
- Type ``` psql railways ``` in terminal.

## Starting the backend part
- Compile the Following files using javac
  - sendQuery.java
  - invokeWorkers.java
  - client.java
  - ServiceModule.java
  - Interactor.java
- Open a terminal and run ServiceModule file by typing ``` java ServiceModule ```
- Now the server has started to take query

## Steps to send the query written in Input Folder
- Store the input files in the Input folder
- Open another terminal and run client file by typing ``` java client ```
- Check the output in the output folder


## File format
### files in input folder
- each line of the file should follow format mentioned below:
  - "Number of passengers" "passenger names seperated by comma" "train number" "date" "coach type"

### files in data folder
- routes.txt should follow the format mentioned below:
  - line1 : "train number"
  - line2 : "number of stations" (say n)
  - line3 to line(3 + n): "stationCode"  "arrival day" "arrival time" "departure day" "departure time"



