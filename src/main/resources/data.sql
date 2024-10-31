
-- Note: passwords should be BCrypt encoded
insert into sys_user (username, password, role) values ('townsq_super', '$2a$12$3hsBwgVK20RlwBWIty3Ap.24JJZvhrNDF/yUtH45HzElTy7H9sX2m', 'ADMIN');
insert into sys_user (username, password, role) values ('townsq_manager', '$2a$12$3hsBwgVK20RlwBWIty3Ap.24JJZvhrNDF/yUtH45HzElTy7H9sX2m', 'ACCOUNT_MANAGER');