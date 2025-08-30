-- Alter the name column to use VARCHAR and cast existing values
ALTER TABLE roles ALTER COLUMN name TYPE VARCHAR(255) USING name::text;

-- Drop the now-unused e_role enum type
DROP TYPE e_role;
