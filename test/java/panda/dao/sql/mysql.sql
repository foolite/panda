DROP TABLE IF EXISTS TEST;

CREATE TABLE TEST (
	ID INTEGER NOT NULL PRIMARY KEY, 
	NAME VARCHAR(80), 
	KIND CHAR(1), 
	PRICE DECIMAL(10,2), 
	UPDATE_TIME TIMESTAMP NULL
);

--set time_zone = '+00:00';

INSERT INTO TEST VALUES(1001, 'NAME 1001', '1', 1001.01, TIMESTAMP('2009-01-01'));
INSERT INTO TEST VALUES(1002, 'NAME 1002', '2', 1002.02, TIMESTAMP('2009-02-02'));
INSERT INTO TEST VALUES(1003, 'NAME 1003', '3', 1003.03, TIMESTAMP('2009-03-03'));
INSERT INTO TEST VALUES(1004, 'NAME 1004', '4', 1004.04, TIMESTAMP('2009-04-04'));
INSERT INTO TEST VALUES(1005, 'NAME 1005', '5', 1005.05, TIMESTAMP('2009-05-05'));

DROP TABLE IF EXISTS IDTEST;
CREATE TABLE IDTEST (
	ID INTEGER NOT NULL AUTO_INCREMENT, 
	NAME VARCHAR(80), 
	KIND CHAR(1), 
	PRICE DECIMAL(10,2), 
	UPDATE_TIME TIMESTAMP NULL,
	PRIMARY KEY(ID)
) AUTO_INCREMENT=101;

