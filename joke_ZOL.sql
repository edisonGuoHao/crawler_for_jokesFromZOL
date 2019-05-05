-- 创建jokes数据库
CREATE DATABASE jokes_ZOL;

-- 使用jokes数据库
USE jokes_ZOL;

-- 创建笑话分类表
CREATE TABLE jokes_type(
	id  INT PRIMARY KEY AUTO_INCREMENT,
	parent_id INT,
	NAME VARCHAR(32)
);

-- 创建笑话表
CREATE TABLE jokes(
	id VARCHAR(50) PRIMARY KEY,
	title VARCHAR(200),
	source VARCHAR(32),
	TEXT TEXT,
	likes INT,
	hates INT,
	createTime DATE,
	updateTime DATE,
	type_id INT
);