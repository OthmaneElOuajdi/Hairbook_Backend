-- =============================================================================
-- INSERT REALISTIC TEST DATA
-- =============================================================================

-- 1. ROLES
INSERT INTO roles (name) VALUES ('ROLE_MEMBER'), ('ROLE_ADMIN');

-- 2. USERS
-- Generate 100 realistic users
INSERT INTO users (full_name, email, password, phone, loyalty_points)
SELECT
    first_name || ' ' || last_name,
    lower(first_name) || '.' || lower(last_name) || i || '@example.com',
    'password123', -- Placeholder, should be hashed
    '04' || (70000000 + i),
    floor(random() * 500)
FROM
    generate_series(1, 100) AS i
CROSS JOIN (
    SELECT (ARRAY['Jean', 'Pierre', 'Marie', 'Sophie', 'Lucas', 'Emma', 'Louis', 'Chloé', 'Gabriel', 'Léa', 'Thomas', 'Manon', 'Hugo', 'Camille', 'Arthur', 'Inès'])[floor(random() * 16) + 1] AS first_name
) AS fn
CROSS JOIN (
    SELECT (ARRAY['Dupont', 'Martin', 'Bernard', 'Dubois', 'Thomas', 'Robert', 'Richard', 'Petit', 'Durand', 'Leroy', 'Moreau', 'Simon', 'Laurent', 'Lefebvre', 'Michel', 'Garcia'])[floor(random() * 16) + 1] AS last_name
) AS ln;

-- Add a specific admin user
INSERT INTO users (full_name, email, password, phone, loyalty_points) VALUES ('Admin Hairbook', 'admin@hairbook.com', 'adminpass', '0412345678', 1000);

-- 3. USER_ROLES
-- Assign ROLE_MEMBER to all 100 users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email LIKE '%@example.com' AND r.name = 'ROLE_MEMBER';

-- Assign ROLE_ADMIN to the admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@hairbook.com' AND r.name = 'ROLE_ADMIN';

-- 4. SERVICE_ITEMS
INSERT INTO service_items (name, price, loyalty_points_reward, description)
VALUES
    ('Coupe Homme Classique', 25.00, 10, 'Shampooing, coupe et coiffage rapide.'),
    ('Coupe Femme & Brushing', 45.00, 20, 'Shampooing, soin, coupe et brushing professionnel.'),
    ('Coloration Racine', 60.00, 30, 'Application de couleur sur les racines, shampooing et soin.'),
    ('Balayage / Mèches', 85.00, 45, 'Technique de balayage ou mèches, patine, shampooing et soin.'),
    ('Brushing Simple', 20.00, 5, 'Mise en forme des cheveux sur cheveux propres.'),
    ('Soin Capillaire Profond', 35.00, 15, 'Traitement Kératine ou Olaplex pour nourrir intensément les cheveux.');

-- 5. RESERVATIONS
-- Generate 150 reservations for random users and services
INSERT INTO reservations (user_id, service_item_id, start_time, status, notes)
SELECT
    (floor(random() * 100) + 1), -- Random user_id between 1 and 100
    (floor(random() * 6) + 1),   -- Random service_item_id between 1 and 6
    NOW() - (i * interval '20 hour'), -- Reservations spread over the last ~125 days
    (ARRAY['CONFIRMED', 'COMPLETED', 'CANCELLED', 'PENDING'])[floor(random() * 4) + 1]::reservation_status,
    (ARRAY['Client fidèle, demande un café.', 'Première visite, être particulièrement attentif.', 'Allergie au produit X, utiliser le produit Y.', 'Souhaite un style très moderne pour un mariage.', 'Vient pour un événement spécial, demande un chignon.', 'Aucune note particulière.'])[floor(random() * 6) + 1]
FROM generate_series(1, 150) AS i;

-- 6. PAYMENTS
-- Generate payments for 100 completed reservations
INSERT INTO payments (user_id, reservation_id, amount, status, payment_method, transaction_id)
SELECT
    r.user_id,
    r.id,
    s.price,
    'COMPLETED',
    (ARRAY['Carte de crédit', 'PayPal', 'Bancontact', 'Espèces'])[floor(random() * 4) + 1],
    'txn_' || substr(md5(random()::text), 0, 25)
FROM reservations r
JOIN service_items s ON r.service_item_id = s.id
WHERE r.status = 'COMPLETED'
LIMIT 100;

-- 7. WORKING_HOURS
INSERT INTO working_hours (day_of_week, start_time, end_time, closed)
VALUES
    ('MONDAY', '09:00', '18:00', true),
    ('TUESDAY', '09:00', '18:00', false),
    ('WEDNESDAY', '09:00', '18:00', false),
    ('THURSDAY', '09:00', '20:00', false),
    ('FRIDAY', '09:00', '20:00', false),
    ('SATURDAY', '08:00', '16:00', false),
    ('SUNDAY', '09:00', '18:00', true);

-- 8. BLOCKED_SLOTS
-- Block a few slots for demonstration
INSERT INTO blocked_slots (start_at, end_at, reason, admin_id)
VALUES
    (NOW() + interval '2 day', NOW() + interval '2 day' + interval '4 hour', 'Maintenance équipement', (SELECT id FROM users WHERE email = 'admin@hairbook.com')),
    (NOW() + interval '10 day', NOW() + interval '12 day', 'Congés annuels', (SELECT id FROM users WHERE email = 'admin@hairbook.com'));
