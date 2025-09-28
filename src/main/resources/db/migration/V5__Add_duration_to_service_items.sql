-- Ajouter la colonne duration à la table service_items
ALTER TABLE service_items ADD COLUMN duration INTEGER NOT NULL DEFAULT 60;

-- Mettre à jour les durées pour chaque service selon les standards de coiffure
UPDATE service_items SET duration = 30 WHERE name LIKE '%Coupe Homme%';
UPDATE service_items SET duration = 60 WHERE name LIKE '%Coupe Femme%';
UPDATE service_items SET duration = 45 WHERE name LIKE '%Coloration Racine%';
UPDATE service_items SET duration = 90 WHERE name LIKE '%Balayage%' OR name LIKE '%Mèches%';
UPDATE service_items SET duration = 20 WHERE name LIKE '%Brushing Simple%';
UPDATE service_items SET duration = 45 WHERE name LIKE '%Soin Capillaire%';
