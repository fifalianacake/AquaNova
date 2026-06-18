CREATE TABLE aliment (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('Alevin', 'Croissance', 'Finition')),
    age_min INT NOT NULL,
    age_max INT NOT NULL,
    taille_granule DECIMAL(5,2),
    prix_unitaire DECIMAL(10,2) NOT NULL,
    seuil_alerte_kg DECIMAL(10,2) NOT NULL,

    CONSTRAINT chk_age CHECK (age_min <= age_max),
    CONSTRAINT chk_prix CHECK (prix_unitaire >= 0),
    CONSTRAINT chk_seuil CHECK (seuil_alerte_kg >= 0)
);

CREATE TABLE lot (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    biomasse DECIMAL(10,2) DEFAULT 0,
    age_actuel INT NOT NULL,

    CONSTRAINT chk_biomasse CHECK (biomasse >= 0),
    CONSTRAINT chk_age_lot CHECK (age_actuel >= 0)
);

CREATE TABLE mouvement_stock (
    id SERIAL PRIMARY KEY,
    date_mouvement DATE NOT NULL,
    aliment_id INT NOT NULL,
    type_mouvement VARCHAR(20) NOT NULL CHECK (type_mouvement IN ('ENTREE', 'SORTIE', 'PERTE')),
    quantite_kg DECIMAL(10,2) NOT NULL,
    commentaire TEXT,

    CONSTRAINT fk_aliment
        FOREIGN KEY (aliment_id)
        REFERENCES aliment(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_quantite CHECK (quantite_kg > 0)
);

CREATE INDEX idx_mouvement_aliment ON mouvement_stock(aliment_id);
CREATE INDEX idx_mouvement_date ON mouvement_stock(date_mouvement);


-- Script de reset + données de test pour aquanova
-- A lancer directement dans psql ou ton client SQL: psql -U postgres -d aquanova -f reset_et_donnees_test.sql

-- 1. Vide les tables et remet les séquences SERIAL à zéro (corrige le conflit de clé primaire)
TRUNCATE TABLE mouvement_stock, aliment RESTART IDENTITY CASCADE;

-- 2. ALIMENTS (pas d'id explicite, Postgres génère via la séquence remise à zéro)
-- Granulé Alevin: seuil 50kg, stock final = 75 (ENTREE 100 - SORTIE 20 - PERTE 5) -> PAS en alerte
-- Granulé Croissance: seuil 100kg, stock final = 40 (ENTREE 60 - SORTIE 20) -> EN ALERTE (40 < 100)
-- Granulé Finition: seuil 30kg, stock final = 0 (ENTREE 50 - SORTIE 50) -> EN ALERTE (0 < 30)

INSERT INTO aliment (nom, type, age_min, age_max, taille_granule, prix_unitaire, seuil_alerte_kg) VALUES
('Granulé Alevin 1mm', 'Alevin', 0, 30, 1.00, 1200.00, 50.00),
('Granulé Croissance 3mm', 'Croissance', 31, 90, 3.00, 950.00, 100.00),
('Granulé Finition 5mm', 'Finition', 91, 180, 5.00, 800.00, 30.00);

-- 3. MOUVEMENTS (aliment_id résolu par sous-requête sur le nom)

-- Aliment Alevin: stock au 2026-06-18 = 100 - 20 - 5 = 75 kg
INSERT INTO mouvement_stock (date_mouvement, aliment_id, type_mouvement, quantite_kg, commentaire) VALUES
('2026-06-01', (SELECT id FROM aliment WHERE nom = 'Granulé Alevin 1mm'), 'ENTREE', 100.00, 'Livraison initiale alevin'),
('2026-06-10', (SELECT id FROM aliment WHERE nom = 'Granulé Alevin 1mm'), 'SORTIE', 20.00, 'Distribution bassin A'),
('2026-06-17', (SELECT id FROM aliment WHERE nom = 'Granulé Alevin 1mm'), 'PERTE', 5.00, 'Sac endommagé');

-- Aliment Croissance: stock au 2026-06-18 = 60 - 20 = 40 kg
INSERT INTO mouvement_stock (date_mouvement, aliment_id, type_mouvement, quantite_kg, commentaire) VALUES
('2026-06-03', (SELECT id FROM aliment WHERE nom = 'Granulé Croissance 3mm'), 'ENTREE', 60.00, 'Livraison croissance'),
('2026-06-12', (SELECT id FROM aliment WHERE nom = 'Granulé Croissance 3mm'), 'SORTIE', 20.00, 'Distribution bassin B');

-- Aliment Finition: stock au 2026-06-18 = 50 - 50 = 0 kg
INSERT INTO mouvement_stock (date_mouvement, aliment_id, type_mouvement, quantite_kg, commentaire) VALUES
('2026-06-05', (SELECT id FROM aliment WHERE nom = 'Granulé Finition 5mm'), 'ENTREE', 50.00, 'Livraison finition'),
('2026-06-15', (SELECT id FROM aliment WHERE nom = 'Granulé Finition 5mm'), 'SORTIE', 50.00, 'Distribution bassin C - stock épuisé');

-- 4. Vérification rapide : affiche le stock calculé par aliment après insertion
SELECT
    a.id,
    a.nom,
    a.seuil_alerte_kg,
    COALESCE(SUM(CASE WHEN m.type_mouvement = 'ENTREE' THEN m.quantite_kg ELSE -m.quantite_kg END), 0) AS stock_calcule
FROM aliment a
LEFT JOIN mouvement_stock m ON m.aliment_id = a.id
GROUP BY a.id, a.nom, a.seuil_alerte_kg
ORDER BY a.id;