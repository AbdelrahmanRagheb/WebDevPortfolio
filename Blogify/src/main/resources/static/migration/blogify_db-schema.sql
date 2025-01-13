-- Drop schema if it already exists
DROP SCHEMA IF EXISTS blogify_db;
CREATE SCHEMA blogify_db;
USE blogify_db;

-- Drop tables if they exist
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `blog_post`;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `tag`;
DROP TABLE IF EXISTS `post_like`;
DROP TABLE IF EXISTS `post_tag`;
DROP TABLE IF EXISTS `post_category`;

-- User Table
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) UNIQUE NOT NULL,
    `email` VARCHAR(255) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) NOT NULL,
    `bio` VARCHAR(255) DEFAULT NULL,
    `created_at` DATETIME NOT NULL
);

-- Blog Post Table
CREATE TABLE `blog_post` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(80) NOT NULL,
    `content` TEXT NOT NULL,
    `author_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL,
    FOREIGN KEY (`author_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- Comment Table
CREATE TABLE `comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `content` TEXT NOT NULL,
    `commenter_id` BIGINT NOT NULL,
    `blog_post_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL,
    FOREIGN KEY (`commenter_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`blog_post_id`) REFERENCES `blog_post`(`id`) ON DELETE CASCADE
);

-- Category Table
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) UNIQUE NOT NULL,
    `description` VARCHAR(255) NOT NULL
);

-- Post-Category Join Table
CREATE TABLE `post_category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `post_id` BIGINT NOT NULL,
    `category_id` BIGINT NOT NULL,
    FOREIGN KEY (`post_id`) REFERENCES `blog_post`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE CASCADE
);

-- Tag Table
CREATE TABLE `tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) UNIQUE NOT NULL
);

-- Post-Tag Join Table
CREATE TABLE `post_tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `post_id` BIGINT NOT NULL,
    `tag_id` BIGINT NOT NULL,
    FOREIGN KEY (`post_id`) REFERENCES `blog_post`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tag_id`) REFERENCES `tag`(`id`) ON DELETE CASCADE
);

-- Post-Like Table
CREATE TABLE `post_like` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `blog_post_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`blog_post_id`) REFERENCES `blog_post`(`id`) ON DELETE CASCADE,
    UNIQUE (`user_id`, `blog_post_id`) -- Ensures a user can like a post only once
);
