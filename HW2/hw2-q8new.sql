SELECT C.name as name, SUM(departure_delay) as delay
from FLIGHTS as F, CARRIERS as C
WHERE F.carrier_id = C.cid AND F.canceled = 0
GROUP BY C.name;
