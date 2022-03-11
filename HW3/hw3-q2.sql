WITH MINTIME AS
    (SELECT min(f.actual_time) AS a, f.origin_city AS b
        FROM flights AS f
        WHERE f.canceled = 0
        GROUP BY f.origin_city)
SELECT f.origin_city,f.dest_city, f.actual_time AS time
FROM flights AS f, MINTIME AS MT
WHERE f.origin_city = MT.b and f.actual_time = MT.a
GROUP BY f.origin_city, f.dest_city, f.actual_time
ORDER BY min(f.actual_time) ASC, f.origin_city ASC;

-- 339 rows
-- 18s
-- Bend/Redmond OR	Los Angeles CA	10
-- Burbank CA	New York NY	10
-- Las Vegas NV	Chicago IL	10
-- New York NY	Nashville TN	10
-- Newark NJ	Detroit MI	10
-- Sacramento CA	Atlanta GA	10
-- Washington DC	Minneapolis MN	10
-- Boise ID	Chicago IL	11
-- Boston MA	Philadelphia PA	11
-- Buffalo NY	Orlando FL	11
-- Cincinnati OH	New Haven CT	11
-- Denver CO	Honolulu HI	11
-- Denver CO	Orlando FL	11
-- Denver CO	Philadelphia PA	11
-- Fort Myers FL	Chicago IL	11
-- Houston TX	Salt Lake City UT	11
-- Minneapolis MN	Newark NJ	11
-- Pittsburgh PA	Dallas/Fort Worth TX	11
-- Indianapolis IN	Houston TX	12
-- Phoenix AZ	Dallas/Fort Worth TX	12