WITH TOTAL as (
    SELECT DISTINCT F.day_of_week_id as day_of_week, F.dest_city as dest_city, COUNT(F.fid) as avg_number
    FROM FLIGHTS as F
    GROUP BY F.dest_city, F.day_of_week_id
),
number_of_days as (
    SELECT F.day_of_week_id, count(DISTINCT F.day_of_month) as number
    FROM FLIGHTS as F
    GROUP BY F.day_of_week_id
),
AVG as (
    SELECT W.day_of_week as days, T.dest_city, T.avg_number / n.number as avg_flights, T.day_of_week as daynum,
    ROW_NUMBER() over (PARTITION BY W.day_of_week ORDER By T.avg_number / n.number desc) as rn
    FROM WEEKDAYS as W,
    TOTAL as T,
    number_of_days as n
    WHERE T.day_of_week = W.did
    AND n.day_of_week_id = T.day_of_week
)
SELECT A.days as days_of_week, A.dest_city, A.avg_flights
from AVG as A
WHERE rn = 1 OR rn = 2
ORDER BY daynum asc
;
-- 14 rows
-- 17 s 57 ms
-- Monday	Chicago IL	2171
-- Monday	Atlanta GA	2132
-- Tuesday	Chicago IL	2400
-- Tuesday	Atlanta GA	2334
-- Wednesday	Chicago IL	2450
-- Wednesday	Atlanta GA	2372
-- Thursday	Chicago IL	2452
-- Thursday	Atlanta GA	2348
-- Friday	Chicago IL	2447
-- Friday	Atlanta GA	2350
-- Saturday	Chicago IL	2308
-- Saturday	Atlanta GA	2286
-- Sunday	Chicago IL	2320
-- Sunday	Atlanta GA	2276