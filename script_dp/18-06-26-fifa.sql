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