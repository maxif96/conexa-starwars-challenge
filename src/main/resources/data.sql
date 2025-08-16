-- Script de inicialización de datos para la base de datos H2
-- Los usuarios se crean con contraseñas encriptadas usando BCrypt

-- Usuario de prueba: admin/admin123
INSERT INTO app_users (username, password) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi');

-- Usuario de prueba: user/user123  
INSERT INTO app_users (username, password) VALUES 
('user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a');

-- Usuario de prueba: test/test123
INSERT INTO app_users (username, password) VALUES 
('test', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');
