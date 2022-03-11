SELECT sum(f.capacity) AS capacity
FROM FLIGHTS AS f, MONTHS AS m
WHERE f.month_id = m.mid and m.month = 'July' and f.day_of_month = 11
	AND ((f.origin_city = 'Seattle WA' and f.dest_city = 'San Francisco CA')
	or (f.origin_city = 'San Francisco CA' and f.dest_city = 'Seattle WA'));

-- This query has 1 row of output
