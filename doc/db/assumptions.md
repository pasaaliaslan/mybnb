# Assumptions and Implementation Considerations

## Geography

- Country, Subcountry, and City tables are prepopulated as they are static (unless new countries, subcountries, and cities are established).
User will choose one of the country, subcountry, city from the rows defined in these tables (i.e. no chance for user to input country/subcountry/city, eliminating the possibility of having inaccurate data).

- Ideally, postal code should have been static too. However, due to the small extent of this project, millions of postal codes are not put into a table and <u>it is assumed that the users will enter valid postal codes for their addresses</u>.

- It is assummed that street name and coordinate data matches with country/subcountry/city data inputted by the user.

