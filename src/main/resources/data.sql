-- Insertion des categories de dépenses 
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

ON CONFLICT (code) DO NOTHING;

INSERT INTO parametre_systeme (code, libelle, valeur, type_valeur, description)
VALUES
('ICA_SYSTEME', 'Indice de conversion alimentaire système', '1.3', 'DECIMAL',
 'Valeur utilisée pour calculer la ration théorique cible.'),

('STOCK_ALIMENT_MINIMUM_KG', 'Seuil minimal de stock aliment', '100', 'DECIMAL',
 'Seuil global minimal de stock de provende en kilogrammes.'),

('PERIODE_ANALYSE_CONSO_JOURS', 'Période d’analyse de consommation', '30', 'INTEGER',
 'Nombre de jours utilisés pour calculer la consommation moyenne.'),

('HORIZON_PREVISION_STOCK_JOURS', 'Horizon de prévision stock', '30', 'INTEGER',
 'Nombre de jours utilisés pour estimer les besoins d’achat.'),

('TEMP_EAU_MIN', 'Température minimale eau', '18', 'DECIMAL',
 'Température minimale acceptable pour l’eau des bassins.'),

('TEMP_EAU_MAX', 'Température maximale eau', '30', 'DECIMAL',
 'Température maximale acceptable pour l’eau des bassins.'),

('PH_MIN', 'pH minimal', '6.5', 'DECIMAL',
 'pH minimal acceptable pour l’eau des bassins.'),

('PH_MAX', 'pH maximal', '8.5', 'DECIMAL',
 'pH maximal acceptable pour l’eau des bassins.'),

('OXYGENE_MIN_MG_L', 'Oxygène dissous minimal', '5', 'DECIMAL',
 'Seuil minimal d’oxygène dissous en mg/L.'),

('SEUIL_PROCHE_RECOLTE_RATIO', 'Seuil proche récolte', '0.90', 'DECIMAL',
 'Ratio du poids cible à partir duquel un lot est considéré proche de la récolte.'),

('NB_MIN_PESEES_PREVISION_RECOLTE', 'Nombre minimal de pesées pour prévision', '2', 'INTEGER',
 'Nombre minimal de pesées nécessaires pour estimer une date de récolte.')
ON CONFLICT (code) DO NOTHING;