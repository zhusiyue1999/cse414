CREATE TABLE EDGES(Source int, Destination int);

INSERT INTO EDGES VALUES(8, 5);
INSERT INTO EDGES VALUES(6, 22);
INSERT INTO EDGES VALUES(1, 3);
INSERT INTO EDGES VALUES(5, 5);

SELECT * FROM EDGES;
SELECT E.Source FROM EDGES AS E;
SELECT * FROM EDGES AS E WHERE Source > Destination;

INSERT INTO EDGES VALUES('-1', '2000');
/* I didn't get any error. Due to the type affinity, the rigid typing SQL database engines will usually convert values to the appropriate datatype.*/
