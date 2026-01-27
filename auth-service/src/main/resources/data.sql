-- Insert default roles
INSERT IGNORE INTO roles (id, name, description) VALUES (1, 'ADMIN', 'Administrator with full access');
INSERT IGNORE INTO roles (id, name, description) VALUES (2, 'SELLER', 'Seller who can manage products');
INSERT IGNORE INTO roles (id, name, description) VALUES (3, 'BUYER', 'Buyer who can purchase products');

-- Insert default admin user (password: admin123)
INSERT IGNORE INTO users (id, email, password, full_name, enabled, created_at, updated_at)
VALUES (1, 'admin@marketplace.com', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', 'Admin User', true, NOW(), NOW());

-- Assign admin role to admin user
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (1, 1);