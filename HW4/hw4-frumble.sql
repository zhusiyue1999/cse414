.header OFF
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

/*
SELECT *
FROM Sales AS s
GROUP BY s.discount
HAVING count( DISTINCT s.popularity) > 1;
-- discount -> popularity
*/



-- question 3
CREATE TABLE Sales1 (name TEXT PRIMARY KEY, price int);
CREATE TABLE Sales2 (month varchar(3) PRIMARY KEY, discount TEXT);
CREATE TABLE Sales3 (name TEXT REFERENCES Sales1, month varchar(3) REFERENCES Sales2);


-- question 4
INSERT INTO Sales1 SELECT DISTINCT s.name, s.price FROM Sales AS s;
INSERT INTO Sales2 SELECT DISTINCT s.month, s.discount FROM Sales AS s;
INSERT INTO Sales3 SELECT s.name, s.month FROM Sales AS s;

SELECT COUNT(*) FROM Sales1;
SELECT COUNT(*) FROM Sales2;
SELECT COUNT(*) FROM Sales3;
-- SELECT * FROM Sales1;
-- SELECT * FROM Sales2;
-- SELECT * FROM Sales3;



/*
*/