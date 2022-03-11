SELECT DISTINCT f.city
FROM (SELECT DISTINCT f3.origin_city AS city
        FROM flights AS f3
        UNION
        SELECT DISTINCT f4.dest_city AS city
        FROM flights AS f4) AS f
WHERE f.city NOT IN (
        SELECT f.dest_city
        FROM flights AS f
        WHERE f.origin_city = 'Seattle WA'
        UNION
        SELECT f2.dest_city
        FROM flights AS f1, flights AS f2
        WHERE f1.dest_city = f2.origin_city and f1.origin_city = 'Seattle WA')
ORDER BY f.city ASC;

-- 4 rows
-- 7m36s
-- Devils Lake ND
-- Hattiesburg/Laurel MS
-- St. Augustine FL
-- Victoria TX