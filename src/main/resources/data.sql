DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'alerte'
          AND column_name = 'message'
          AND data_type = 'bytea'
    ) THEN
        ALTER TABLE alerte
        ALTER COLUMN message TYPE text
        USING convert_from(message, 'UTF8');
    END IF;
END $$;@@

-- Insertion des categories de dépenses 
/*
INSERT INTO categorie_depense (code, libelle, description, type_categorie)
VALUES 
('ACHAT_ALEVINS', 'Achat d''alevins', 'Dépenses liées à l''acquisition de jeunes poissons pour l''élevage.', 'ACHAT'),

('ACHAT_PROVENDE', 'Achat de provende', 'Dépenses pour l''alimentation et la nourriture du bétail/poissons.', 'ACHAT'),

('ACHAT_MEDICAMENT', 'Achat de médicaments', 'Frais de produits vétérinaires, soins et traitements médicaux.', 'ACHAT'),

('MAIN_OEUVRE', 'Main d''œuvre', 'Rémunérations, salaires et indemnités du personnel opérationnel.', 'DEPENSE'),

('LOGISTIQUE', 'Logistique', 'Frais de transport, livraison, stockage et déplacements.', 'DEPENSE'),

('MAINTENANCE', 'Maintenance', 'Entretien et réparation des infrastructures, outils et équipements.', 'DEPENSE'),

('ENERGIE', 'Énergie', 'Factures d''électricité, carburant, eau et autres sources d''énergie.', 'DEPENSE'),

('AUTRE', 'Autre', 'Toutes autres dépenses ne rentrant pas dans les catégories prédéfinies.', 'MIXTE')

ON CONFLICT (code) DO NOTHING;@@
*/