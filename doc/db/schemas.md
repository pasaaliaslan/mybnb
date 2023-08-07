# Schemas

## Geography

Country (<u>name</u>)

Subcountry (<u>name</u>, <u>countryName</u>)

City (<u>name</u>, <u>subcountryName</u>)

PostalCode (<u>code</u>, <u>cityName</u>, <u>subcountryName</u>, <u>countryName</u>)

Address (<u>streetName</u>, <u>postalCode</u>, <u>cityName</u>, <u>subcountryName</u>, <u>countryName</u>)

Location (<u>longitude</u>, <u>latitude</u>, streetName, postalCode, cityName, <u>subcountryName</u>, countryName)

## User Related

User (<u>username</u>, password, insuranceNumber, firstName, lastName, dateOfBirth, occupation)

livesIn(<u>username</u>, streetName, postalCode, cityName, subcountryName, countryName)

Renter (<u>username</u>, creditCardNumber)

Host (<u>username</u>)

## Listing

ListingType(<u>type</u>)

Listing (<u>id</u>, description, pricePerNight, availabilityStart, availabilityEnd, listingType, hostUsername, numberOfBedrooms, numberOfBeds, numberOfBaths, longitude, latitude)

AmenityCategory (<u>name</u>)

Amenity (<u>name</u>, amenityCategoryName)

includes (<u>listingId</u>, <u>amenityName</u>)

## Booking Related

Booking (<u>id</u>, startDate, endDate, renterUsername, listingId)

reviewHost (<u>commentedAt</u>, <u>renterUsername</u>, <u>bookingId</u>, comment, rating)

reviewRenter (<u>commentedAt</u>, <u>hostUsername</u>, <u>bookingId</u>, comment, rating)

reviewVisit (<u>commentedAt</u>, <u>renterUsername</u>, <u>bookingId</u>, comment, rating)
