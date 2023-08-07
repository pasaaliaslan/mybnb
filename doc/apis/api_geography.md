# Geography API (/geography/)

## `GET` /country/

Returns the list of all countries recorded in the system in alphebetical order. This list is completely static (unless new countries are founded).

### Request Params

```JSON
NONE
```

### Response

```JSON
200 SUCCESS
{
  "countries": [
    "Andorra",
    "United Arab Emirates",
    "Afghanistan",
    ...
    "Zambia",
    "Zimbabwe"
  ]
}

500 SERVER ERROR
```

## `GET` /subcountry/

Returns the list of all subcountries (province, state, etc.) recorded in the system for the specified country in alphebetical order. For any country, the list is completely static (unless new subcountries are established)

### Request Params

```JSON
{
  "country": string
}
```

### Response

```JSON
200 OK
{
  "subcountries": string[]
}

400 BAD REQUEST
{
  "message": "No such country"
}

500 SERVER ERROR
```

### Sample Request

```JSON
{
  "country": "Canada",
}
```

### Sample Response

```JSON
{
  "subcountries": [
    "Alberta",
    "British Columbia",
    "Manitoba",
    "New Brunswick",
    "Newfoundland and Labrador",
    "Northwest Territories",
    "Nova Scotia",
    "Ontario",
    "Prince Edward Island",
    "Saskatchewan",
    "Quebec",
    "Yukon",
  ]
}
```


## `GET` /city/

Returns the list of all cities recorded in the system for the specified country and subcountry in alphebetical order. The list is completely static (unless new cities are established)

### Request Params

```JSON
{
  "country": string,
  "subcountry": string,
}
```

### Response

```JSON
200 OK
{
  "cities": string[]
}

400 BAD REQUEST
{
  "message": "No such country" | "No such subcountry in the country"
}

500 SERVER ERROR
```

### Sample Request

```JSON
{
  "country": "Canada",
  "subcountry": "Ontario",
}
```

### Sample Response

```JSON
{
  "cities": [
    "Ajax",
    "Barrie",
    "Belleville",
    ...
    "Toronto",
    "Vaughan",
    "Waterloo",
    "Welland",
    "Windsor",
    "Woodstock",
    "Scarborough"
    "Ancaster",
    "Willowdale",
  ]
}
```
