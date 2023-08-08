# Report API (/report/)

NOTE: Country, Subcountry, City, and PostalCode are weak entity sets, meaning
the granular one requires others to work. For instance, only providing `city=Toronto` wouldn't be enough as the system would think about `city=Toronto` in different subcountries in different countries. Similarly, a subcountry requires country. Country could exist by itself in a query.

## `GET` /bookings_by_location/

Gets all bookings satisfying the location query.

### Query Params

```JSON
{
    "startDate"?: string,
    "endDate"?: string,
    "country"?: string,
    "subcountry"?: string,
    "city"?: string,
    "postalCode"?: string,
}
```

### Response

```JSON
200 SUCCESS
{
    "bookings": string[],   // List of booking ids.
    "count": number,        // Number of bookings satisfying the query.

}

400 BAD_QUERY
{
    "message": `A message indicating what was invalid about the query`
}

500 SERVER_ERROR
```

### Sample Query

```JSON
{
    "startDate": "2023-09-01",
    "endDate": "2023-09-31",
    "country": "Canada",
    "subcountry": "Ontario",
    "city": "Toronto",
}
```

### Sample Response

```JSON
200 SUCCESS
{
    "ids": [
        "123456789",
        "987654321",
        "192837465",
        "918273645"
    ],
    "count": 4
}
```

## `GET` /host_rankings_by_listing/

Get the rankings of the hosts by the number of listing in a specific location.

### Query Params

```JSON
{
    "country"?: string,
    "subcountry"?: string,
    "city"?: string,
    "postalCode"?: string
}
```

### Response

```JSON
200 SUCCESS
{
    "hosts": {
        "idNumber": number,
        "marketShare": number,
    }[]
}

400 BAD_QUERY
{
    "message": `A message indicating what was invalid about the query`
}

500 SERVER_ERROR
```

### Sample Query

```JSON
{
    "country": "Canada",
    "subcountry": "Ontario",
    "city": "Toronto",
}
```

### Sample Response

```JSON
200 SUCCESS
{
    "hosts": [
        {
            "idNumber": "123456789",
            "marketShare": 97,
        },
        {
            "idNumber": "987654321",
            "marketShare": 1,
        },
        {
            "idNumber": "192837465",
            "marketShare": 1,
        },
        {
            "idNumber": "918273645",
            "marketShare": 1,
        },
    ]
}
```

## `GET` /renter_rankings_by_bookings/

Get the ranking of the renters by the number of bookings they made.

### Query Params

```JSON
{

    "startDate": string,
    "endDate": string,
    "city"?: string,
    "country"?: string,
    "minBookingCount"?: number,
    "maxBookingCount"?: number,
}
```

### Response

```JSON
200 SUCCESS
{
    "renters": {
        "idNumber": string,
        "bookingCount": number,
    }[]
}
400 BAD_QUERY
{
    "message": `A message indicating what was invalid about the query`
}

500 SERVER_ERROR
```

### Sample Query

```JSON
{
    "startDate": "2022-09-01",
    "endDate": "2022-09-31",
    "city": "Toronto",
    "minBookingCount": 2,
}
```

### Sample Response

```JSON
{
    "renters": [
        {
            "idNumber": "123456789",
            "bookingCount": 23,
        },
        {
            "idNumber": "987654321",
            "bookingCount": 13,
        },
        {
            "idNumber": "192837465",
            "bookingCount": 4,
        },
        {
            "idNumber": "918273645",
            "bookingCount": 2,
        },
    ]
}
```


## `GET` /cancellation_ranking/

Get the ranking of the hosts and renters by the number of cancellations they made.

### Query Params

```JSON
{
}
```

### Response

```JSON
200 SUCCESS
{
    "renterRankings": {
        "username": string,
        "numberOfCancelledBookings": number,
    }[],
    "hostRankings": {
        "username": string,
        "numberOfCancelledBookings": number,
    }[]
}
400 BAD_QUERY
{
    "message": `A message indicating what was invalid about the query`
}

500 SERVER_ERROR
```

### Sample Response

```JSON
{
  "renterRankings": [
    {
        "numberOfCancelledBookings": 7,
        "username": "leomessi"
    },
    {
        "numberOfCancelledBookings": 4,
        "username": "therock"
    },
    {
        "numberOfCancelledBookings": 2,
        "username": "cristiano"
    }
  ],
  "hostRankings": [
    {
        "numberOfCancelledBookings": 7,
        "username": "lebron10"
    },
    {
        "numberOfCancelledBookings": 3,
        "username": "person3"
    },
    {
        "numberOfCancelledBookings": 2,
        "username": "person4"
    },
    {
        "numberOfCancelledBookings": 2,
        "username": "person5"
    }
  ]
}
```

## `GET` /most_popular_noun_phrases/

Get the ranking of the noun phrases by the number of times they are used in the comments of the specified listing.

### Query Params

```JSON
{
    "listingId": string,
}
```

### Response

```JSON
200 SUCCESS
{
    "phrases": {
        "phrase": string,
        "count": number,
    }[]
}

404 NOT_FOUND

500 SERVER_ERROR
```

### Sample Query

```JSON
{
    "listingId": "123456789"
}
```

### Sample Response

```JSON
{
    "phrases": [
        {
            "phrase": "Awesome",
            "count": 22,
        },
        {
            "phrase": "Terrible",
            "count": 13,
        },
        {
            "phrase": "Good",
            "count": 12,
        },
        {
            "phrase": "Bad",
            "count": 12,
        },
    ]
}
```
