--DROP DATABASE IF EXISTS pandatest;
--DROP USER IF EXISTS panda;
--CREATE USER panda PASSWORD 'panda';
--CREATE DATABASE pandastest WITH OWNER = panda ENCODING = 'UTF-8';
--GRANT ALL ON DATABASE pandatest TO panda;


DROP TABLE IF EXISTS TEST;

CREATE TABLE TEST (
	ID INTEGER NOT NULL PRIMARY KEY, 
	NAME VARCHAR(80), 
	KIND CHAR(1), 
	PRICE DECIMAL(10,2), 
	UPDATE_TIME TIMESTAMP
);

INSERT INTO TEST VALUES(1001, 'NAME 1001', '1', 1001.01, TO_TIMESTAMP('2009-01-01', 'YYYY-MM-DD'));
INSERT INTO TEST VALUES(1002, 'NAME 1002', '2', 1002.02, TO_TIMESTAMP('2009-02-02', 'YYYY-MM-DD'));
INSERT INTO TEST VALUES(1003, 'NAME 1003', '3', 1003.03, TO_TIMESTAMP('2009-03-03', 'YYYY-MM-DD'));
INSERT INTO TEST VALUES(1004, 'NAME 1004', '4', 1004.04, TO_TIMESTAMP('2009-04-04', 'YYYY-MM-DD'));
INSERT INTO TEST VALUES(1005, 'NAME 1005', '5', 1005.05, TO_TIMESTAMP('2009-05-05', 'YYYY-MM-DD'));

DROP TABLE IF EXISTS IDTEST;
CREATE TABLE IDTEST (
	ID serial NOT NULL, 
	NAME VARCHAR(80), 
	KIND CHAR(1), 
	PRICE DECIMAL(10,2), 
	UPDATE_TIME TIMESTAMP
);

