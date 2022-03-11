-- question 1
.mode csv
CREATE TABLE Sales (name TEXT, discount TEXT, month varchar(3), price int);
.import mrFrumbleData.csv Sales


-- question 2
SELECT *
FROM Sales AS s
GROUP BY s.name
HAVING count( DISTINCT s.price) > 1;
-- name -> price

SELECT *
FROM Sales AS s
GROUP BY s.month
HAVING count( DISTINCT s.discount) > 1;
-- month -> discount

SELECT *
FROM Sales AS s
GROUP BY s.name, s.discount
HAVING count( DISTINCT s.price) > 1;
-- name, discount -> price

SELECT *
FROM Sales AS s
GROUP BY s.month, s.price
HAVING count( DISTINCT s.discount) > 1;
-- month, price -> discount

SELECT *
FROM Sales AS s
GROUP BY s.name, s.discount, s.month
HAVING count( DISTINCT s.price) > 1;
-- name, discount, month -> price

SELECT *
FROM Sales AS s
GROUP BY s.month, s.price, s.name
HAVING count( DISTINCT s.discount) > 1;
-- month, price, name -> discount
