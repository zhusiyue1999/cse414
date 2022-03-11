SELECT c.name AS name, min(f.price) AS min_price
FROM CARRIERS AS c, FLIGHTS AS f
WHERE c.cid = f.carrier_id 
	AND ((f.origin_city = 'Seattle WA' AND f.dest_city = 'New York NY') 
	OR (f.origin_city = 'New York NY' AND f.dest_city = 'Seattle WA'))
GROUP BY c.cid;
