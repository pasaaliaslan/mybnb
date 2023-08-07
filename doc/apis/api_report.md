# Report API (/report/)

## `GET` /bookings_by_location/

Gets all bookings satisfying the location query.

### Body Params

```JSON
{
    "dateRange": {
        "startDate": string,
        "endDate": string,
    },
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
    "dateRange": {
        "startDate": "2023-09-01",
        "endDate": "2023-09-31",
    },
    "city": "Toronto",
}
```

### Sample Response

```JSON
200 SUCCESS
{
    "bookings": [
        "123456789",
        "987654321",
        "192837465",
        "918273645"
    ],
    "count": 4
}
```

## `GET` /host_rankings_by_posting/

Get the rankings of the hosts by the number of postings in a specific location.

### Body Params

```JSON
{
    "country"?: string,
    "city"?: string,
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

### Body Params

```JSON
{
    "dateRange": {
        "startDate": string,
        "endDate": string,
    },
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
    "dateRange": {
        "startDate": "2022-09-01",
        "endDate": "2022-09-31",
    },
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

### Body Params

```JSON
{
    "dateRange"?: {
        "startDate": string,
        "endDate": string,
    },
    "minCancellationCount"?: number,
    "maxCancellationCount"?: number,
}
```

### Response

```JSON
200 SUCCESS
{
    "users": {
        "idNumber": string,
        "cancellationCount": number,
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
    "dateRange": {
        "startDate": "2022-09-01",
        "endDate": "2022-09-31",
    },
    "minCancellationCount": 10,
    "maxCancellationCount": 22,
}
```

### Sample Response

```JSON
{
    "users": [
        {
            "idNumber": "123456789",
            "bookingCount": 22,
        },
        {
            "idNumber": "987654321",
            "bookingCount": 13,
        },
        {
            "idNumber": "192837465",
            "bookingCount": 12,
        },
        {
            "idNumber": "918273645",
            "bookingCount": 12,
        },
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
