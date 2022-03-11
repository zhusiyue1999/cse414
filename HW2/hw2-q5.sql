SELECT c.name AS name, CAST(sum(f.canceled) AS DOUBLE) / COUNT(f.fid) AS percent
FROM CARRIERS AS c, FLIGHTS AS f
WHERE c.cid = f.carrier_id and f.origin_city = 'Seattle WA'
GROUP BY c.name
HAVING percent > 0.006
ORDER BY percent DESC;
