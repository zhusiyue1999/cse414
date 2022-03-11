SELECT C.name as name, CAST(sum(F.canceled) as DOUBLE)/COUNT(F.fid) as percent
FROM CARRIERS as C, FLIGHTS as F
WHERE F.origin_city = 'Seattle WA'
AND F.carrier_id = C.cid
GROUP BY C.name
HAVING  percent > 0.006
ORDER BY percent ASC;
