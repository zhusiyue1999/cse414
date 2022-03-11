SELECT C.name AS name, sum(departure_delay) AS delay
FROM CARRIERS AS c, FLIGHTS AS f
WHERE c.cid = f.carrier_id AND f.canceled = 0
GROUP BY c.name;
