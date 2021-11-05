DROP TABLE IF EXISTS ARTICLE_ENTITY;

CREATE TABLE ARTICLE_ENTITY(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    text VARCHAR(10000) NOT NULL,
    creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);