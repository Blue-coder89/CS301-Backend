# Backend Part

## Opening postgres and loading the database
- Load the Database from [ticketreservation repository](https://github.com/skhan-org/TicketReservationSystem).
- Clone the repository.
- Type ``` sudo -s -u postgres ``` in terminal.
- Type ``` psql ``` in terminal.
- Load the database by the command ``` \i setup.sql ```
- Create a file **database.properties** in the directory(Containing ServiceModule.java) with the following contents. Put the user, password and db_url of your own database.
```
JDBC_DRIVER=org.postgresql.Driver
USER=postgres
PASS=1234
DB_URL=jdbc:postgresql://localhost:5432/railways
```

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
       # (At the end)
- #### *Station*

       <Station-Name>:<Station-Code>
       
- #### *Routes*

        line 1: <Train-Number>
        line 2: <Number-of-Stations-in-Path>
        // For each station in order as seen by train, enter following details,
            <Station-Code> <Day-of-arrival> <Time-of-arrival> <Day-of-Departure> <Time-of-Departure>

## Using Interactor
- **_How to Run?_**
```
    Javac Interactor.java
    Java Interactor
```
- **_Operations:_**
 - _Search:_
    [![](https://mermaid.ink/img/eyJjb2RlIjoiZmxvd2NoYXJ0IExSXG4gICAgWihQcmVzcyA3KS0tPiB8U2VhcmNoIEZ1bmN0aW9ufCBZKEVudGVyIE5hbWUgb2YgU291cmNlIFN0YXRpb24pIC0tPiB8RWc6IENER3wgVyhFbnRlciBOYW1lIG9mIERlc3RpbmF0aW9uIFN0YXRpb24pLS0-IHxFZzogTkRMU3wgWChPdXRwdXQgYWxsIHBhdGhzIGZyb20gU291cmNlIHRvIERlc3RpbmF0aW9uKSIsIm1lcm1haWQiOnsidGhlbWUiOiJkZWZhdWx0In0sInVwZGF0ZUVkaXRvciI6ZmFsc2V9)](https://mermaid-js.github.io/docs/mermaid-live-editor-beta/#/edit/eyJjb2RlIjoiZmxvd2NoYXJ0IExSXG4gICAgWihQcmVzcyA3KS0tPiB8U2VhcmNoIEZ1bmN0aW9ufCBZKEVudGVyIE5hbWUgb2YgU291cmNlIFN0YXRpb24pIC0tPiB8RWc6IENER3wgVyhFbnRlciBOYW1lIG9mIERlc3RpbmF0aW9uIFN0YXRpb24pLS0-IHxFZzogTkRMU3wgWChPdXRwdXQgYWxsIHBhdGhzIGZyb20gU291cmNlIHRvIERlc3RpbmF0aW9uKSIsIm1lcm1haWQiOnsidGhlbWUiOiJkZWZhdWx0In0sInVwZGF0ZUVkaXRvciI6ZmFsc2V9)

 - _Add train:_
 
    [![](https://mermaid.ink/img/eyJjb2RlIjoiZ3JhcGggVEJcbiAgICBBKFByZXNzIDEpIC0tPiBCKEVudGVyIFRyYWluczogTWFudWFsbHkgLyB2aWEgRmlsZSlcbiAgICBCIC0tPnxQcmVzcyAxfCBEKEVudGVyIFRyYWluIE51bWJlcilcbiAgICBCIC0tPnxQcmVzcyAyfCBDKFRyYWlucyB3aWxsIGdldCBsb2FkZWQgaW50byBEYXRhYmFzZSlcbiAgICBEIC0tPkUoRW50ZXIgVHJhaW4gTmFtZSkgXG5cbiAgIiwibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQifSwidXBkYXRlRWRpdG9yIjpmYWxzZX0)](https://mermaid-js.github.io/docs/mermaid-live-editor-beta/#/edit/eyJjb2RlIjoiZ3JhcGggVEJcbiAgICBBKFByZXNzIDEpIC0tPiBCKEVudGVyIFRyYWluczogTWFudWFsbHkgLyB2aWEgRmlsZSlcbiAgICBCIC0tPnxQcmVzcyAxfCBEKEVudGVyIFRyYWluIE51bWJlcilcbiAgICBCIC0tPnxQcmVzcyAyfCBDKFRyYWlucyB3aWxsIGdldCBsb2FkZWQgaW50byBEYXRhYmFzZSlcbiAgICBEIC0tPkUoRW50ZXIgVHJhaW4gTmFtZSkgXG5cbiAgIiwibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQifSwidXBkYXRlRWRpdG9yIjpmYWxzZX0)
    
