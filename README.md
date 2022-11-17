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

### _Files in input folder_
- each line of the file should follow format mentioned below:
  - "Number of passengers" "passenger names seperated by comma" "train number" "date" "coach type"

### _Files in data folder_
- #### **_Train_**
       line 1: <Train-No>
       line 2: <Train-Name>
    
- #### **_Train-Schedules_**

       <Train-No> <date of journey> <no:of Ac-coaches> <no:of sleeper-coaches>
- #### *Station*

       <Station-Name>: <Station-Code>
       
- #### *Routes*

        line 1: <Train-Number>
        line 2: <Number-of-Stations-in-Path>
        // For each station in order as seen by train, enter following details,
            <Station-Code> <Day-of-arrival> <Time-of-arrival> <Day-of-Departure> <Time-of-Departure>


