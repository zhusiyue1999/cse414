SELECT count(*)
FROM (
    SELECT f1.origin_city, f1.dest_city
    FROM flights AS f1
    UNION
    SELECT f2.dest_city, f2.origin_city
    FROM flights As f2) AS t
WHERE t.origin_city > t.dest_city;

-- 1 row
-- 18s
-- 2351