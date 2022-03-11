SELECT C.name as carrier, MIN(F.price) as min_price
from FLIGHTS as F, CARRIERS as C
WHERE F.carrier_id = C.cid AND ((F.origin_city = 'Seattle WA' AND F.dest_city = 'New York NY')
OR (F.dest_city = 'Seattle WA' AND F.origin_city = 'New York NY'))
GROUP BY C.name;
