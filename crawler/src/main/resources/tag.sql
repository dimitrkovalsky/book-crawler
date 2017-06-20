CREATE TABLE `tags` (
  `tag_id`      INT(11)      NOT NULL AUTO_INCREMENT,
  `tag_name`    VARCHAR(255) NOT NULL,
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `id_UNIQUE` (`tag_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

  CREATE TABLE `tag_selection` (
  `id`            INT(11)      NOT NULL AUTO_INCREMENT,
  `tag_id`        INT(11)      NOT NULL,
  `selection_id`  INT(11)      NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT tag_selection_id UNIQUE KEY (tag_id,selection_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

  CREATE TABLE `tag_book` (
  `id`            INT(11)      NOT NULL AUTO_INCREMENT,
  `tag_id`        INT(11)      NOT NULL,
  `book_id`       INT(11)      NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT tag_selection_id UNIQUE KEY (tag_id,book_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `selection` (
  `selection_id`  INT(11)      NOT NULL,
  `title`         VARCHAR(512) NOT NULL,
  `user_made`     VARCHAR(512) NOT NULL,
  `create_time`   VARCHAR(64)  NOT NULL,
  `description`   MEDIUMTEXT ,
  `likes_count`  INT(11)      ,
  `votes_count`  INT(11)      ,

  PRIMARY KEY (`selection_id`),
  UNIQUE KEY `id_UNIQUE` (`selection_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `selection_books` (
  `id`           INT(11)      NOT NULL AUTO_INCREMENT,
  `selection_id` INT(11)      NOT NULL,
  `livelib_book_id`  INT(11)      NOT NULL,
  `neurolib_book_id` INT(11)   ,
  `votes_count`   INT(11)      ,
  `description`   MEDIUMTEXT NOT NULL,

  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `livelib_books` (
  `book_id`           INT(11)  NOT NULL,
  `title`         VARCHAR(512) NOT NULL,
  `description`   VARCHAR(10240) ,
  `cover_url`    VARCHAR(512) NOT NULL,
  `author_names`  VARCHAR(512) ,
  `author_id`     INT(11)      ,
  `rating`        DECIMAL(9,5) NOT NULL,

  PRIMARY KEY (`book_id`),
  UNIQUE KEY `id_UNIQUE` (`book_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `links` (
  `id`           INT(11)      NOT NULL AUTO_INCREMENT,
  `link`          VARCHAR(10240)     NOT NULL,

  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;