CREATE TABLE InsuranceCo (name TEXT PRIMARY KEY, phone INTEGER);
CREATE TABLE Person (ssn INTEGER PRIMARY KEY, name TEXT);
CREATE TABLE Driver (DriverID INTEGER, driverssn INTEGER NOT NULL REFERENCES Person PRIMARY KEY);
CREATE TABLE Vehicle (licensePlate TEXT PRIMARY KEY, year INTEGER, personssn INTEGER UNIQUE REFERENCES Person);
CREATE TABLE Car (make TEXT, carLicensePlate TEXT NOT NULL REFERENCES Vehicle PRIMARY KEY);
CREATE TABLE NonProfessionalDriver (nonpssn INTEGER NOT NULL REFERENCES Driver PRIMARY KEY);
CREATE TABLE ProfessionalDriver (MedicalHistory TEXT, pssn INTEGER NOT NULL REFERENCES Driver PRIMARY KEY);
CREATE TABLE Truck (capacity INTEGER, truckLicensePlate TEXT NOT NULL REFERENCES Vehicle PRIMARY KEY, pssn INTEGER UNIQUE REFERENCES ProfessionalDriver);
CREATE TABLE Insures (maxLiability REAL, name TEXT REFERENCES InsuranceCo, licensePlate TEXT UNIQUE REFERENCES Vehicle, PRIMARY KEY (name, licensePlate));
CREATE TABLE Drives (carLicensePlate TEXT REFERENCES Car, npssn INTEGER REFERENCES NonProfessionalDriver, PRIMARY KEY(carLicensePlate, npssn));


-- "Insures" represents a many-to-one relationship from Vihecle to InsuranceCo so that we don't need to create a seperate table for "insures".
-- Instead, we can reference InsuranceCo in Vehicle by using foreign key name.

-- "Drivers" is a many-to-many relationship so we need to create a table to represent the relationship from Car to NonProfessionalDriver.
-- "Operates" is a many-to-one relationship so we don't need to create a seeperate table for "Operates". We can reference ProfessionalDriver in
-- Truck by using ProfessionalDriver's ssn.