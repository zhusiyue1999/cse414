SELECT f.origin_city AS city
FROM flights AS f
GROUP BY f.origin_city
HAVING MAX(f.actual_time) < 3 * 60
ORDER BY f.origin_city ASC;

-- 109 rows
-- 9s
-- Aberdeen SD
-- Abilene TX
-- Alpena MI
-- Ashland WV
-- Augusta GA
-- Barrow AK
-- Beaumont/Port Arthur TX
-- Bemidji MN
-- Bethel AK
-- Binghamton NY
-- Brainerd MN
-- Bristol/Johnson City/Kingsport TN
-- Butte MT
-- Carlsbad CA
-- Casper WY
-- Cedar City UT
-- Chico CA
-- College Station/Bryan TX
-- Columbia MO
-- Columbus GA