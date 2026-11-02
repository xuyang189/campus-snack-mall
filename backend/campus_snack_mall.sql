CREATE DATABASE IF NOT EXISTS campus_snack_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_snack_mall;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NULL,
    phone VARCHAR(20) NULL,
    avatar VARCHAR(500) NULL,
    role VARCHAR(20) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_user_address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    is_default TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS snack_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    sort INT DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS snack_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    cover_image VARCHAR(500) NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    sales INT NOT NULL DEFAULT 0,
    description TEXT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    audit_status TINYINT NOT NULL DEFAULT 0,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_category_id (category_id),
    KEY idx_merchant_id (merchant_id),
    KEY idx_audit_status (audit_status)
);

CREATE TABLE IF NOT EXISTS snack_cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    checked TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id)
);

CREATE TABLE IF NOT EXISTS snack_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status TINYINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    remark VARCHAR(255) NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_order_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS snack_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_image VARCHAR(500) NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id),
    KEY idx_product_id (product_id)
);

CREATE TABLE IF NOT EXISTS sys_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO sys_user (username, password, nickname, role, status)
SELECT 'admin',
       '$2a$10$YrShwQU4TtKdac2zNQ8HyeeeuTulI4aXTVho7VtsUuB4R7Q6xj0Sy',
       '系统管理员',
       'ADMIN',
       1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

INSERT INTO snack_category (name, sort, status)
SELECT '饼干膨化', 1, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM snack_category WHERE name = '饼干膨化');

INSERT INTO snack_category (name, sort, status)
SELECT '糖果巧克力', 2, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM snack_category WHERE name = '糖果巧克力');

INSERT INTO snack_category (name, sort, status)
SELECT '饮料冲调', 3, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM snack_category WHERE name = '饮料冲调');

INSERT INTO snack_category (name, sort, status)
SELECT '方便速食', 4, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM snack_category WHERE name = '方便速食');

INSERT INTO snack_category (name, sort, status)
SELECT '坚果卤味', 5, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM snack_category WHERE name = '坚果卤味');

INSERT INTO sys_notice (title, content, status)
SELECT '新用户欢迎', '欢迎来到校园零食商城，商品上新和活动公告会在这里展示。', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_notice WHERE title = '新用户欢迎');

INSERT INTO sys_notice (title, content, status)
SELECT '商家入驻说明', '入驻商家提交商品后需要管理员审核，审核通过后方可上架销售。', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_notice WHERE title = '商家入驻说明');