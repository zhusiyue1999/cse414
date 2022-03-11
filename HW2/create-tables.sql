.mode csv
PRAGMA foreign_keys=ON;
CREATE TABLE CARRIERS(cid varchar(7), name varchar(83), PRIMARY KEY (cid));
CREATE TABLE MONTHS(mid int, month varchar(9), PRIMARY KEY (mid));
CREATE TABLE WEEKDAYS(did int, day_of_week varchar(9), PRIMARY KEY (did));
CREATE TABLE FLIGHTS(fid int, month_id int REFERENCES Months, day_of_month int, day_of_week_id int REFERENCES Weekdays, carrier_id varchar(7) REFERENCES Carriers, flight_num int, origin_city varchar(34), origin_state varchar(47), dest_city varchar(34), dest_state varchar(46), departure_delay int, taxi_out int, arrival_delay int, canceled int, actual_time int, distance int, capacity int, price int, PRIMARY KEY (fid));

.import carriers.csv CARRIERS
.import months.csv MONTHS
.import weekdays.csv WEEKDAYS
.import flights-small.csv FLIGHTS
