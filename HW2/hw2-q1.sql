SELECT DISTINCT f.flight_num AS flight_num
FROM FLIGHTS AS f, CARRIERS AS c
WHERE f.carrier_id = c.cid
	and f.day_of_week_id = 5 
	and f.origin_city = 'Seattle WA' 
	and f.dest_city = 'Boise ID'
	and c.name = 'Alaska Airlines Inc.';
