SELECT w.day_of_week AS day_of_week, avg(f.arrival_delay) AS delay 
FROM FLIGHTS AS f, WEEKDAYS AS w
WHERE f.day_of_week_id = w.did
GROUP BY w.day_of_week
ORDER BY avg(f.arrival_delay) ASC
LIMIT 1;
