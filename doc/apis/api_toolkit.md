# Toolkit API (/toolkit/)

## `GET` /price_suggestion/

Suggest a pricing based on granular location and amenities of the listing.

### Request Params

```JSON
{
    "startDate": string,
    "endDate": string,
    "postal_code": string,
    "provided_amenities": string[],
}
```

### Response

```JSON
200 OK
{
    "suggested_price_per_night": number,

    // Amenity name to price margin mapping.
    "suggested_amenities": {string: number},
}
```

## Price Suggestion Algorithm

### `PriceFactor` Table

Consider following table, namely `PriceFactor`.

| avgPricePerNight | numberOfBookings | bookingDate | amenityName | postalCode | ... |
|:-:|:-:|:-:|:-:|:-:|:-:|
$s_0$ | $n_0$ | $d_0$ | NULL | $p_1$ | ...
$s_1$ | $n_1$ | $d_1$ | $a_1$ | $p_1$ | ...
$s_2$ | $n_2$ | $d_2$ | $a_2$ | $p_1$ | ...
$s_3$ | $n_3$ | $d_3$ | $a_3$ | $p_2$ | ...
... | ... | ... | ... | ...

where `amenityName` is a foreign key referencing the key of `Amenity` table and `postalCode ...` is a foreign key referencing the key of `PostalCode` table. `bookingDate`, `amenityName` and `postalCode ...` form the primary key of `PriceFactor` table.

Consider a tuple ($s, n, d, a, p$) in `PriceFactor`.

This tuple represent 'the average price $s$ of listings with amenity $a$ and number of such bookings $n$ in the postal code area $p$ for the night of $d$'.

If $a = NULL$, the tuple represents 'the average listing price $s$ and number of such bookings $n$ in the postal code area $p$ for the night of $d$' (i.e. average case with no consideration of an amenity).

### Amenity Margins

Each amenity $a$ has a margin value $m$ which represents how much a Residence with $a$ in the postal code area $p$ costs more than the average listing price in the same area. For instance, assume the average listing price in the postal code area is $100$ per night, and the average listing price of the residences with `Wifi` is $120$, then $m_{wifi} = 120-100 = 20$.

However, timewise trends has great contribution to margin values of the amenities. For instance, `Heater` amenity would mean a lot to the renters in the winter month while mean nothing in the summer months. Ideally, the impact of the average price of a past date on today's suggested price should decrease naturally and become zero at some point.

Following function $c(\Delta d)$ yields this impact, where $\Delta d$ is the number of days passed since the bookingDate.
$$
c(\Delta d) = MAX(0, \ln{\frac{45.877}{\Delta d + 16.877}})
$$
By definition, $c(1)=1$ and $c(x)=0, \forall x >= 30$, which mimics the ideal behavior. The impact of the closest possible date (one day ago) is the greatest and $1$ (i.e. average price of the listings are reflected fully). Meanwhile, the impact of the average prices of the bookings that are older than 30 days is none.

For $a$, $m$ is calculated as follows.

```SQL
SELECT AmenityAvg.avg - GeneralAvg.avg
FROM
((SELECT SUM(avgPricePerNight * numberOfBookings * POW(LOG(45.877/(DATEDIFF(CURRENT_TIMESTAMP, bookingDate)+15.877)), 2)) / SUM(numberOfBookings * POW(LOG(45.877/(DATEDIFF(CURRENT_TIMESTAMP, bookingDate)+15.877)), 2)) as avg
FROM PriceFactor
WHERE (
  postalCode=p AND
  amenityName=NULL
)) AS GeneralAvg),
((SELECT SUM(avgPricePerNight * numberOfBookings * POW(LOG(45.877/(DATEDIFF(CURRENT_TIMESTAMP, bookingDate)+15.877)), 2)) / SUM(numberOfBookings * POW(LOG(45.877/(DATEDIFF(CURRENT_TIMESTAMP, bookingDate)+15.877)), 2)) as avg
FROM PriceFactor
WHERE (
  postalCode=p AND
  amenityName=a
)) AS AmenityAvg)
```

### Suggesting Amenities

Let $a_1, ..., a_n$ be all amenities statically defined in the `Amenity` table. Consider a residence $r$ in the postal code area $p$.

Theoretically, the probability of an amenity $a$ to be in the list of amenities of residence $r$ is $0.5$. Of course, the reality does not exactly follow theory, especially if the dataset is small. For simplicity, <u>it is assumed that the dataset is large and somehow follows the theory</u>, which would make sense in the large scale.



## Limitations

- This algorithm does not consider joint amenity factors practically.
  - For instance, consider amenities `Wifi` and `Dedicated Workspace` amenities. In the postal code area $p$, let `Wifi` has a price factor of $a$ and `Dedicated Workspace` has a price factor of $b$. The algorithm would suggest that if the host includes `Wifi` and `Dedicated Workspace` amenities in his residence, the price per night can be increased by $a+b$. However, `Wifi` and `Dedicated Workspace` is a great combo and might contribute more than $a+b$.

  - Similarly, consider amenities `Outdoor Furniture` and
  `Outdoor Dining Area` amenities. In the postal code area $p$, let `Outdoor Furniture` has a price factor of $x$ and `Outdoor Dining Area` has a price factor of $y$. The algorithm would suggest that if the host includes `Outdoor Furniture` and
  `Outdoor Dining Area` amenities in his residence, the price per night can be increased by $x+y$. However, if the renter only looks for an `Outdoor Dining Area` and nothing else in the outdoors, having `Outdoor Dining Area` amenities in the listing will decrease the value of `Outdoor Furniture` for the renter. Thus, `Outdoor Furniture` and `Outdoor Dining Area` amenities combined might contribute less than $x+y$.

- As the algorithm relies on elementary statistics that only cares average values, outlier prices can corrupt the suggestions. However, it is reasonable to assume that both owners and renters want to maximize their profits. Hosts wouldn't post listings that have significantly less price per night than the average price to maximize their profits. Likewise, renters wouldn't book listings that have significantly more price per night to not spend too much.

- This algorithm only considers past amenity data while suggesting a price/amenity (purely due to project description). However, in reality, the price of a listing depends on other variables too, such as residence type, number of bedrooms, etc.
