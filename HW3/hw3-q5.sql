SELECT DISTINCT f2.dest_city AS city
FROM flights AS f1, flights AS f2
WHERE f1.dest_city = f2.origin_city and f2.dest_city != 'Seattle WA'
    and f1.origin_city = 'Seattle WA' and f2.dest_city NOT IN
    (SELECT f3.dest_city
        FROM flights AS f3
        WHERE f3.origin_city = 'Seattle WA'
        GROUP BY f3.dest_city)
ORDER BY f2.dest_city ASC;

-- 256 rows
-- 26s
-- Aberdeen SD
-- Abilene TX
-- Adak Island AK
-- Aguadilla PR
-- Akron OH
-- Albany GA
-- Albany NY
-- Alexandria LA
-- Allentown/Bethlehem/Easton PA
-- Alpena MI
-- Amarillo TX
-- Appleton WI
-- Arcata/Eureka CA
-- Asheville NC
-- Ashland WV
-- Aspen CO
-- Atlantic City NJ
-- Augusta GA
-- Bakersfield CA
-- Bangor ME