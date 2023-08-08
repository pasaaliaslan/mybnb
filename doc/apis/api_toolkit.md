# Toolkit API (/toolkit/)

## `GET` /price_suggestion/

Suggest a pricing based on granular location and amenities of the listing.

### Request Params

```JSON
{
    "country": string,
    "subcountry": string,
    "city": string,
    "postalCode": string,
    "providedAmenities": {
      "name": string,
      "frequency": int,
    },
}
```

### Response

```JSON
200 OK
{
    "suggestedPricePerNight": number,
    "suggestedAmenities": string[]
}
```

## Algorithm

Suggested price per night is calculated considering the listings within the same postal code area. Simply, algorithm takes the average price per night of the listings in the postal code area and provides it as a suggested price for the potential listing.

On the other hand, suggested amenities is the most used amenities in the listings in the postal code area that does not already appear in the potential listing.
