SELECT DISTINCT c.name AS name
FROM CARRIERS AS c, FLIGHTS AS f
WHERE c.cid = f.carrier_id
GROUP BY c.cid, f.month_id, f.day_of_month
HAVING COUNT(*) > 1000;
