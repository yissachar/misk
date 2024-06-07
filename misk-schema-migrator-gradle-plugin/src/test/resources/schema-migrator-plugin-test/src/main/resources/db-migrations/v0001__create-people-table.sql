CREATE TABLE people (
    id                          bigint       NOT NULL AUTO_INCREMENT,
    name                        varchar(191) NOT NULL,
    PRIMARY KEY (id),
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    ROW_FORMAT = DYNAMIC;
