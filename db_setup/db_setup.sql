-- Geography

CREATE TABLE Country (
  name VARCHAR(50),
  PRIMARY KEY (name)
) ENGINE INNODB;

CREATE TABLE Subcountry (
  name VARCHAR(50),
  countryName VARCHAR(50),
  FOREIGN KEY (countryName) REFERENCES Country(name),
  PRIMARY KEY (name, countryName)
) ENGINE INNODB;

CREATE TABLE City (
  name VARCHAR(50),
  subcountryName VARCHAR(50),
  countryName VARCHAR(50),
  FOREIGN KEY (subcountryName, countryName) REFERENCES Subcountry(name, countryName),
  PRIMARY KEY (name, subcountryName, countryName)
) ENGINE INNODB;

CREATE TABLE PostalCode (
  code VARCHAR(10),
  cityName VARCHAR(50),
  subcountryName VARCHAR(50),
  countryName VARCHAR(50),
  FOREIGN KEY (cityName, subcountryName, countryName) REFERENCES City(name, subcountryName, countryName),
  PRIMARY KEY (code, cityName, subcountryName, countryName)
) ENGINE INNODB;

CREATE TABLE Address (
  streetName VARCHAR(100),
  postalCode VARCHAR(10),
  cityName VARCHAR(50),
  subcountryName VARCHAR(50),
  countryName VARCHAR(50),
  FOREIGN KEY (postalCode, cityName, subcountryName, countryName) REFERENCES PostalCode(code, cityName, subcountryName, countryName),
  PRIMARY KEY (streetName, postalCode, cityName, subcountryName, countryName)
) ENGINE INNODB;

CREATE TABLE Location (
  longitude DECIMAL(10,7),
  latitude DECIMAL(10,7),
  streetName VARCHAR(100) NOT NULL,
  postalCode VARCHAR(10) NOT NULL,
  cityName VARCHAR(50) NOT NULL,
  subcountryName VARCHAR(50),
  countryName VARCHAR(50) NOT NULL,
  FOREIGN KEY (streetName, postalCode, cityName, subcountryName, countryName) REFERENCES Address(streetName, postalCode, cityName, subcountryName, countryName),
  PRIMARY KEY (longitude, latitude)
) ENGINE INNODB;

-- User Related

CREATE TABLE User (
  username VARCHAR(50),
  password VARCHAR(50) NOT NULL,
  insuranceNumber INT(50),
  firstName VARCHAR(50) NOT NULL,
  lastName VARCHAR(50) NOT NULL,
  dateOfBirth DATE NOT NULL,
  occupation VARCHAR(100),
  PRIMARY KEY (username)
) ENGINE INNODB;

delimiter |
CREATE TRIGGER userTrig BEFORE INSERT ON User
FOR EACH ROW BEGIN
DECLARE msg VARCHAR(255);
IF TIMESTAMPDIFF(YEAR, NEW.dateOfBirth, CURRENT_TIMESTAMP) < 18 THEN
SET msg = 'User should be older than 18.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
END
|
delimiter ;

CREATE TABLE livesIn (
  username VARCHAR(50),
  streetName VARCHAR(100) NOT NULL,
  postalCode VARCHAR(10) NOT NULL,
  cityName VARCHAR(50) NOT NULL,
  subcountryName VARCHAR(50) NOT NULL,
  countryName VARCHAR(50) NOT NULL,
  FOREIGN KEY (username) REFERENCES User(username),
  FOREIGN KEY (streetName, postalCode, cityName, subcountryName, countryName) REFERENCES Address(streetName, postalCode, cityName, subcountryName, countryName),
  PRIMARY KEY (username)
) ENGINE INNODB;

CREATE TABLE Renter (
  username VARCHAR(50),
  creditCardNumber INT(16) NOT NULL,
  FOREIGN KEY (username) REFERENCES User(username),
  PRIMARY KEY (username)
) ENGINE INNODB;

CREATE TABLE Host (
  username VARCHAR(50),
  FOREIGN KEY (username) REFERENCES User(username),
  PRIMARY KEY (username)
) ENGINE INNODB;

-- Listing

CREATE TABLE ResidenceType (
  name VARCHAR(20),
  PRIMARY KEY (name)
) ENGINE INNODB;

CREATE TABLE Residence (
  id INT(50) AUTO_INCREMENT,
  residenceTypeName VARCHAR(20) NOT NULL,
  hostUsername VARCHAR(50) NOT NULL,
  numberOfBedrooms INT(5) NOT NULL,
  numberOfBeds INT(5) NOT NULL,
  numberOfBaths INT(5) NOT NULL,
  roomNumber INT(5),
  longitude DECIMAL(10,7) NOT NULL,
  latitude DECIMAL(10,7) NOT NULL,
  FOREIGN KEY (residenceTypeName) REFERENCES ResidenceType(name),
  FOREIGN KEY (hostUsername) REFERENCES Host(username),
  FOREIGN KEY (longitude, latitude) REFERENCES Location(longitude, latitude),
  PRIMARY KEY (id)
) ENGINE INNODB;

CREATE TABLE Listing (
  id INT(50) AUTO_INCREMENT,
  residenceId INT(50) NOT NULL,
  description TEXT NOT NULL,
  pricePerNight DECIMAL(10, 2) NOT NULL,
  availabilityStart DATE NOT NULL,
  availabilityEnd DATE,
  isAvailable BOOLEAN DEFAULT 1, -- A listing is never deleted for the booking history. This flag mimics the logic of being 'deleted' or not.
  FOREIGN KEY (residenceId) REFERENCES Residence(id),
  PRIMARY KEY (id)
) ENGINE INNODB;

delimiter |
CREATE TRIGGER listingTrig BEFORE INSERT ON Listing
FOR EACH ROW BEGIN
DECLARE msg VARCHAR(255);
DECLARE cond BOOLEAN;

SET cond = (NEW.availabilityStart > NEW.availabilityEnd);

IF cond THEN
SET msg = 'availabilityEnd cannot be earlier than availabilityStart.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;

SET cond = (SELECT IF (COUNT(*) > 0, 1, 0) FROM Listing WHERE residenceId=NEW.residenceId) AND (SELECT IF (SUM(pass=1) = COUNT(*), 0, 1) FROM (SELECT availabilityStart >= NEW.availabilityEnd OR availabilityEnd <= NEW.availabilityStart AS pass FROM Listing WHERE residenceId=NEW.residenceId AND isAvailable=1) AS T);

IF cond THEN
SET msg = 'There is a listing within the given availability range with the same residence.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
END
|
delimiter ;

CREATE TABLE AmenityCategory (
  name VARCHAR(100),
  PRIMARY KEY (name)
) ENGINE INNODB;

CREATE TABLE Amenity (
  name VARCHAR(100),
  amenityCategoryName VARCHAR(100) NOT NULL,
  FOREIGN KEY (amenityCategoryName) REFERENCES AmenityCategory(name),
  PRIMARY KEY (name)
) ENGINE INNODB;

CREATE TABLE includes (
  residenceId INT(50),
  amenityName VARCHAR(100) NOT NULL,
  FOREIGN KEY (residenceId) REFERENCES Residence(id),
  FOREIGN KEY (amenityName) REFERENCES Amenity(name),
  PRIMARY KEY (residenceId, amenityName)
) ENGINE INNODB;

-- Booking Related

CREATE TABLE Booking (
  id INT(50) AUTO_INCREMENT,
  startDate DATE NOT NULL,
  endDate DATE NOT NULL,
  renterUsername VARCHAR(50) NOT NULL,
  listingId INT(50) NOT NULL,
  pricePerNight INT(50) NOT NULL DEFAULT -1,
  cancelledBy VARCHAR(10) NOT NULL DEFAULT 'NONE',
  CONSTRAINT checkCancelledBy CHECK (cancelledBy IN ('RENTER', 'HOST', 'NONE')),
  FOREIGN KEY (renterUsername) REFERENCES Renter(username),
  FOREIGN KEY (listingId) REFERENCES Listing(id),
  PRIMARY KEY (id)
) ENGINE INNODB;

delimiter |
CREATE TRIGGER bookingTrig BEFORE INSERT ON Booking
FOR EACH ROW BEGIN
DECLARE msg VARCHAR(255);
IF NEW.pricePerNight = -1 THEN
SET NEW.pricePerNight = (SELECT pricePerNight FROM Listing WHERE Listing.id=NEW.listingId);
END IF;
IF NEW.startDate > NEW.endDate THEN
SET msg = 'End date cannot be earlier than start date.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
IF NEW.startDate < (SELECT availabilityStart FROM Listing WHERE id=NEW.listingId) OR NEW.endDate > (SELECT availabilityEnd FROM Listing WHERE id=NEW.listingId) THEN
SET msg = 'Booking should be within the availability range of the listing.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
IF 1 = (SELECT EXISTS(SELECT * FROM Booking WHERE Booking.listingId=NEW.listingId AND Booking.cancelledBy='NONEØ' AND NEW.endDate > Booking.startDate AND Booking.endDate > NEW.startDate)) THEN
SET msg = 'The listing is booked within the date range.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
END
|
delimiter ;

CREATE TABLE ReviewHost (
  commentedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  bookingId INT(50),
  comment TEXT NOT NULL,
  rating INT(1) NOT NULL,
  CHECK (1<= rating AND  rating <= 5),
  FOREIGN KEY(bookingId) REFERENCES Booking(id),
  PRIMARY KEY (bookingId)
) ENGINE INNODB;

delimiter |
CREATE TRIGGER reviewHostTrig BEFORE INSERT ON ReviewHost
FOR EACH ROW BEGIN
DECLARE msg VARCHAR(255);
IF NEW.rating < 1 OR NEW.rating > 5 THEN
SET msg = 'Rating should be between 1 and 5 (inclusive).';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
IF NEW.commentedAt > CURRENT_TIMESTAMP THEN
SET msg = 'Cannot comment from future.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
IF NEW.commentedAt < (SELECT endDate FROM Booking WHERE NEW.bookingId=id) THEN
SET msg = 'Cannot comment before visit is completed.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
END
|
delimiter ;

CREATE TABLE ReviewRenter (
  commentedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  bookingId INT(50),
  comment TEXT NOT NULL,
  rating INT(1) NOT NULL,
  CHECK (1<= rating AND  rating <= 5),
  FOREIGN KEY(bookingId) REFERENCES Booking(id),
  PRIMARY KEY (bookingId)
) ENGINE INNODB;

delimiter |
CREATE TRIGGER reviewRenterRatingTrig BEFORE INSERT ON ReviewRenter
FOR EACH ROW BEGIN
DECLARE msg VARCHAR(255);
IF NEW.rating < 1 OR NEW.rating > 5 THEN
SET msg = 'Rating should be between 1 and 5 (inclusive).';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
IF NEW.commentedAt > CURRENT_TIMESTAMP THEN
SET msg = 'Cannot comment from future.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
IF NEW.commentedAt < (SELECT endDate FROM Booking WHERE NEW.bookingId=id) THEN
SET msg = 'Cannot comment before visit is completed.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
END
|
delimiter ;

CREATE TABLE ReviewVisit (
  commentedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  bookingId INT(50),
  comment TEXT NOT NULL,
  rating INT(1) NOT NULL,
  CHECK (1<= rating AND rating <= 5),
  FOREIGN KEY(bookingId) REFERENCES Booking(id),
  PRIMARY KEY (bookingId)
) ENGINE INNODB;

delimiter |
CREATE TRIGGER reviewVisitRatingTrig BEFORE INSERT ON ReviewVisit
FOR EACH ROW BEGIN
DECLARE msg VARCHAR(255);
IF NEW.rating < 1 OR NEW.rating > 5 THEN
SET msg = 'Rating should be between 1 and 5 (inclusive).';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
IF NEW.commentedAt > CURRENT_TIMESTAMP THEN
SET msg = 'Cannot comment from future.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
IF NEW.commentedAt < (SELECT endDate FROM Booking WHERE NEW.bookingId=id) THEN
SET msg = 'Cannot comment before visit is completed.';
SIGNAL sqlstate '45000' SET message_text = msg;
END IF;
END
|
delimiter ;

/* CREATE TABLE PriceFactor (
  aggregatePricePerNight INT(50),
  numberOfBookings INT(50),
  bookingDate INT(50) NOT NULL,
  amenityName INT(50),
  postalCode VARCHAR(10),
  cityName VARCHAR(50),
  subcountryName VARCHAR(50),
  countryName VARCHAR(50),
  FOREIGN KEY (postalCode, cityName, subcountryName, countryName) REFERENCES PostalCode(code, cityName, subcountryName, countryName),
  PRIMARY KEY (bookingDate, amenityName, postalCode, cityName, subcountryName, countryName)
) ENGINE INNODB; */
