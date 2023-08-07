# Client API (/client/)

## `GET` /renter/

Gets the renter user.

### Request Params

```JSON
{
    "username": string
}
```

### Response

```JSON
200 SUCCESS
{
    "firstName": string,
    "lastName": string,
    "dateOfBirth": date,
    "occupation": string,
    "address": {
      "streetName": string,
      "postalCode": string,
      "city": string,
      "subcountry": string,
      "country": string,
    }
}

404 NOT FOUND

500 SERVER ERROR
```

### Sample Response

```JSON
200 SUCCESS
{
    "firstName": "John",
    "lastName": "Wick",
    "dateOfBirth": "30-06-1995",
    "occupation": "assassin",
    "address": {
      "streetName": "Continental Street",
      "postalCode": "A1B 2C3",
      "city": "New York City",
      "subcountry": "New York",
      "country": "USA",
    }
}
```

## `POST` /renter/

Creates a renter user.

### Body Params

```JSON
{
    "username": string,
    "password": string,
    "insuranceNumber": number,
    "firstName": string,
    "lastName": string,
    "dateOfBirth": date,
    "occupation": string,
    "address": {
      "streetName": string,
      "postalCode": string,
      "city": string,
      "subcountry": string,
      "country": string,
    },
    "creditCardNumber": number
}
```

### Response

```JSON
201 CREATED

500 SERVER ERROR

400 BAD REQUEST
{
    "message": `A message indicating a problem in one of the inputs`
}
```

### Sample Response

```JSON
400 BAD REQUEST
{
    "message": "Invalid Postal Code"
}
```

## `DELETE` /renter/

Deletes the renter user.

### Request Params

```JSON
{
    "username": string
}
```

### Response

```JSON
200 SUCCESS

404 NOT FOUND

500 SERVER ERROR
```

## `PUT` /renter/

Updates the renter.

### Body Params

```JSON
{
    "username": string,
    "password"?: string,
    "firstName"?: string,
    "lastName"?: string,
    "dateOfBirth"?: date,
    "occupation"?: string,
    "address"?: {
      "streetName": string,
      "postalCode": string,
      "city": string,
      "subcountry": string,
      "country": string,
    },
    "creditCardNumber"?: number,
}
```

### Response

```JSON
200 SUCCESS

500 SERVER ERROR

400 BAD REQUEST
{
    "message": `A message indicating a problem in one of the inputs`
}
```

### Sample Response

```JSON
400 BAD REQUEST
{
    "message": "Invalid Postal Code"
}
```

## `GET` /host/

Gets the renter user.

### Request Params

```JSON
{
    "idNumber": number
}
```

### Response

```JSON
200 SUCCESS
{
    "idNumber": number,
    "firstName": string,
    "lastName": string,
    "dateOfBirth": date,
    "occupation": string,
    "streetName": string,
    "postalCode": string,
    "city": string,
    "country": string,
}

404 NOT FOUND

500 SERVER ERROR
```

### Sample Response

```JSON
200 SUCCESS
{
    "idNumber": 123456789,
    "firstName": "John",
    "lastName": "Wick",
    "dateOfBirth": "30-06-1995",
    "occupation": "assassin",
    "streetName": "Continental Street",
    "postalCode": "A1B 2C3",
    "city": "New York",
    "country": "USA",
}
```

## `POST` /host/

Creates a host user.

### Query Params

```JSON
{
    "idNumber": number,
    "name": string,
    "dateOfBirth": date,
    "occupation": string,
    "streetName": string,
    "postalCode": string,
    "city": string,
    "country": string,
}
```

### Response

```JSON
201 Created

500 Server Error

400 Bad Request
{
    "message": `A message indicating a problem in one of the inputs`
}
```

### Sample Response

```JSON
400 Bad Request
{
    "message": "postalCode is invalid"
}
```

## `DELETE` /host/

Deletes the renter user.

### Request Params

```JSON
{
    "idNumber": string
}
```

### Response

```JSON
200 SUCCESS

404 NOT FOUND

500 SERVER ERROR
```

## `PUT` /host/

Updates a renter user.

### Body Params

```JSON
{
    "idNumber": number,
    "firstName": string,
    "lastName": string,
    "dateOfBirth": date,
    "occupation": string,
    "streetName": string,
    "postalCode": string,
    "city": string,
    "country": string,
    "creditCardNumber": number,
}
```

### Response

```JSON
200 SUCCESS

500 SERVER ERROR

400 BAD REQUEST
{
    "message": `A message indicating a problem in one of the inputs`
}
```

### Sample Response

```JSON
400 BAD REQUEST
{
    "message": "Invalid Postal Code"
}
```

## `GET` /listings/

Gets all listings satisfying the filter query.

Body Params
```JSON
{
    "minPricePerNight"?: number,
    "maxPricePerNight"?: number,
    "location"?: {
        "streetName"?: string,
        "postalCode"?: string,
        "city"?: string,
        "country"?: string,
    },
    "region"?: {
        "centerLongitude": number,
        "centerLatitude": number,
        "radius"?: number,
    },
    "dateRange"?: {
        "startDate": string,
        "endDate": string,
    },
    "amenities"?: string[]
}
```

## `GET` /listing/

## `POST` /listing/

## `DELETE` /listing/

## `PUT` /listing/

## `POST` /booking/

## `DELETE` /booking/

