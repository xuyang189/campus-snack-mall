USE campus_snack_mall;

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

USE campus_snack_mall;

UPDATE sys_user
SET password = '$2a$10$YrShwQU4TtKdac2zNQ8HyeeeuTulI4aXTVho7VtsUuB4R7Q6xj0Sy'
WHERE username = 'admin';

SELECT username, role, status
FROM sys_user
WHERE username = 'admin';