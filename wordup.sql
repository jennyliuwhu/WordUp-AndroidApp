create database if not exists wordup;

CREATE TABLE IF NOT EXISTS `wordup`.`user` (user_id int NOT NULL AUTO_INCREMENT,
	                                        name varchar(255),
                                            password varchar(255),
                                            PRIMARY KEY (user_id));

CREATE TABLE IF NOT EXISTS `wordup`.`words` (word_id int NOT NULL AUTO_INCREMENT,
				                             word varchar(255),
				                             description TEXT,
                                             PRIMARY KEY (word_id));


CREATE TABLE IF NOT EXISTS `wordup`.`rec` (word_id int NOT NULL AUTO_INCREMENT,
				                           word varchar(255),
				                           description TEXT,
                                           PRIMARY KEY (word_id));