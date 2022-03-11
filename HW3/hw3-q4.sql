WITH NONCANCELEDTOTAL AS
    (SELECT COUNT(f1.fid) AS total, f1.origin_city AS origin_city
        FROM flights AS f1
        WHERE f1.actual_time > 0
        GROUP BY f1.origin_city),
NONCANCELEDSELECTED AS
    (SELECT COUNT(f2.fid) AS total, f2.origin_city AS origin_city
        FROM flights AS f2
        WHERE f2.actual_time > 0 and f2.actual_time < 3 * 60
        GROUP BY f2.origin_city)
SELECT NC.origin_city,
    (CASE
        WHEN NCS.total IS NULL THEN 0
        Else NCS.total * 100.0 / NC.total
    END) AS percentage
FROM NONCANCELEDTOTAL AS NC left outer join NONCANCELEDSELECTED AS NCS ON NCS.origin_city = NC.origin_city
ORDER BY NCS.total * 100.0/ NC.total ASC;

-- 327 rows
-- 18s
-- Guam TT	0.000000000000
-- Pago Pago TT	0.000000000000
-- Aguadilla PR	28.897338403041
-- Anchorage AK	31.812080536912
-- San Juan PR	33.660531697341
-- Charlotte Amalie VI	39.558823529411
-- Ponce PR	40.983606557377
-- Fairbanks AK	50.116550116550
-- Kahului HI	53.514471352628
-- Honolulu HI	54.739028823682
-- San Francisco CA	55.828864537188
-- Los Angeles CA	56.080890822987
-- Seattle WA	57.609387792231
-- Long Beach CA	62.176439513998
-- New York NY	62.371834136728
-- Kona HI	63.160792951541
-- Las Vegas NV	64.920256372037
-- Christiansted VI	65.100671140939
-- Newark NJ	65.849971096980
-- Plattsburgh NY	66.666666666666