--set time_zone = '+00:00';

DROP TABLE IF EXISTS TEST;

CREATE TABLE TEST (
	ID INTEGER NOT NULL PRIMARY KEY,
	FBIT             BIT,
	FBOOL            BOOLEAN,
	
	FCHAR            CHAR(1),
	FSTR             VARCHAR(80),

	FTINYINT         TINYINT,
	FSMALLINT        SMALLINT,
	FINTEGER         INTEGER,
	FBIGINT          BIGINT,
	
	FREAL            REAL,
	FFLOAT           FLOAT,
	FDOUBLE          DOUBLE,
	FDECIMAL         DECIMAL(10,2),
	FNUMERIC         NUMERIC(10,2),

	FDATE            DATE,
	FTIME            TIME,
	FTIMESTAMP       DATETIME,

	FCLOB            LONGTEXT,
	FLONGVARCHAR     TEXT,

	FBLOB            LONGBLOB,
	FBINARY          BINARY(100),
	FVARBINARY       VARBINARY(100),
	FLONGVARBINARY   BLOB
);


INSERT INTO TEST VALUES(1001, 1, 1, '1', 'NAME 1001', 11, 101, 1001, 10001, 1.01, 11.01, 101.01, 1001.01, 10001.01, '2009-01-01', '00:01:01', TIMESTAMP('2009-01-01'), NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST VALUES(1002, 1, 1, '2', 'NAME 1002', 12, 102, 1002, 10002, 2.02, 12.02, 102.02, 1002.02, 10002.02, '2009-02-02', '00:02:02', TIMESTAMP('2009-02-02'), NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST VALUES(1003, 1, 1, '3', 'NAME 1003', 13, 103, 1003, 10003, 3.03, 13.03, 103.03, 1003.03, 10003.03, '2009-03-03', '00:03:03', TIMESTAMP('2009-03-03'), NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST VALUES(1004, 1, 1, '4', 'NAME 1004', 14, 104, 1004, 10004, 4.04, 14.04, 104.04, 1004.04, 10004.04, '2009-04-04', '00:04:04', TIMESTAMP('2009-04-04'), NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST VALUES(1005, 1, 1, '5', 'NAME 1005', 15, 105, 1005, 10005, 5.05, 15.05, 105.05, 1005.05, 10005.05, '2009-05-05', '00:05:05', TIMESTAMP('2009-05-05'), NULL, NULL, NULL, NULL, NULL, NULL);


DROP TABLE IF EXISTS IDTEST;
CREATE TABLE IDTEST (
	ID INTEGER NOT NULL AUTO_INCREMENT, 
	FSTR VARCHAR(80), 
	PRIMARY KEY(ID)
) AUTO_INCREMENT=101;

