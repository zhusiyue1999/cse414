SELECT c.name
FROM CARRIERS AS c, flights AS f
WHERE f.origin_city = 'Seattle WA' and f.dest_city = 'San Francisco CA'
    and c.cid = f.carrier_id
GROUP BY c.name
ORDER BY c.name ASC;

-- 4 rows
-- 4s
-- Alaska Airlines Inc.
-- SkyWest Airlines Inc.
-- United Air Lines Inc.
-- Virgin America