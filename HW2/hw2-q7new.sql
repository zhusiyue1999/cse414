SELECT SUM(F.capacity) as capacity
FROM FLIGHTS as F,
MONTHS as M
WHERE F.month_id = M.mid AND M.month = 'July' AND F.day_of_month = 11 AND ((F.origin_city = 'Seattle WA' AND F.dest_city = 'San Francisco NY')
OR (F.dest_city = 'Seattle WA' AND F.origin_city = 'San Francisco CA'));
