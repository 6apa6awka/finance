SET GLOBAL local_infile = TRUE;
DELETE FROM geography;
INSERT INTO geography(name, parent_geography) VALUES ('QC',NULL);
INSERT INTO geography(name, parent_geography) VALUES ('ON',NULL);

LOAD DATA LOCAL INFILE 'D:/Programming/finance/src/main/resources/db/dataload/geography/qc.txt'
    INTO TABLE geography
    FIELDS TERMINATED BY ', '
    LINES TERMINATED BY '\n'
    (name, parent_geography);

LOAD DATA LOCAL INFILE 'D:/Programming/finance/src/main/resources/db/dataload/geography/on.txt'
    INTO TABLE geography
    FIELDS TERMINATED BY ', '
    LINES TERMINATED BY '\n'
    (name, parent_geography);

LOAD DATA LOCAL INFILE 'D:/Programming/finance/src/main/resources/db/dataload/geography/montreal.txt'
    INTO TABLE geography
    FIELDS TERMINATED BY ', '
    LINES TERMINATED BY '\n'
    (name, parent_geography);

DELETE FROM category;

LOAD DATA LOCAL INFILE 'D:/Programming/finance/src/main/resources/db/dataload/category/categories.txt'
    INTO TABLE category
    FIELDS TERMINATED BY ', '
    LINES TERMINATED BY '\n'
    (name, parent_category);



