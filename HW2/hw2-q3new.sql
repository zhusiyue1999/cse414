SELECT AVG(F.arrival_delay) as delay, w.day_of_week
from FLIGHTS as F, WEEKDAYS as W
where F.day_of_week_id = W.did;
