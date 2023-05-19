INSERT INTO geography(name, parent_geography_id) VALUES ('QC',NULL);
INSERT INTO geography(name, parent_geography_id) VALUES ('ON',NULL);

LOAD DATA LOCAL INFILE
'D:/Programming/finance/src/main/resources/db/dataload/geography/qc.csv'
INTO TABLE geography
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
(name, parent_geography_id);

