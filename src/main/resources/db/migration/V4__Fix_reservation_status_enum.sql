-- Migration pour corriger la gestion des ENUMs PostgreSQL avec Hibernate
-- Hibernate avec @Enumerated(EnumType.STRING) fonctionne mieux avec des colonnes VARCHAR
-- qui utilisent des contraintes CHECK pour valider les valeurs

-- 1. Modifier la colonne status pour utiliser VARCHAR avec contrainte CHECK
ALTER TABLE reservations 
ALTER COLUMN status TYPE VARCHAR(20);

-- 2. Ajouter une contrainte CHECK pour valider les valeurs
ALTER TABLE reservations 
ADD CONSTRAINT chk_reservation_status 
CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED'));

-- 3. Mettre à jour les valeurs existantes si nécessaire (au cas où il y aurait des données)
UPDATE reservations SET status = 'PENDING' WHERE status IS NULL;
