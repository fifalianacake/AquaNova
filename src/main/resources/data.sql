-- Insertion des categories de dépenses 
INSERT INTO categorie_depense (code, libelle, description, type_categorie)
VALUES 
('ACHAT_ALEVINS', 'Achat d''alevins', 'Dépenses liées à l''acquisition de jeunes poissons pour l''élevage.', 'ACHAT'),

('ACHAT_PROVENDE', 'Achat de provende', 'Dépenses pour l''alimentation et la nourriture du bétail/poissons.', 'ACHAT'),

('ACHAT_MEDICAMENT', 'Achat de médicaments', 'Frais de produits vétérinaires, soins et traitements médicaux.', 'DEPENSE'),

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
 'Nombre minimal de pesées nécessaires pour estimer une date de récolte.'),

('TAUX_MORTALITE_MAXIMUM', 'Taux de mortalité maximal', '10', 'DECIMAL',
 'Pourcentage de mortalité cumulée d''un lot au-delà duquel une alerte est déclenchée.'),

('JOURS_AVANT_RUPTURE_STOCK', 'Jours avant rupture de stock', '7', 'INTEGER',
 'Nombre de jours restants (selon la consommation moyenne) en deçà duquel une alerte de rupture de stock est déclenchée.'),

('MARGE_MINIMUM_ACCEPTABLE', 'Marge brute minimale acceptable', '20', 'DECIMAL',
 'Taux de marge brute (%) en deçà duquel un lot vendu déclenche une alerte de marge insuffisante.')
ON CONFLICT (code) DO NOTHING;

-- Types d'équipement (pas de contrainte d'unicité sur libelle : garde par NOT EXISTS)
INSERT INTO type_equipement (libelle, description)
SELECT v.libelle, v.description
FROM (VALUES
    ('Pompe à eau', 'Pompe assurant la circulation ou le renouvellement de l''eau des bassins.'),
    ('Aérateur', 'Aérateur de surface ou diffuseur d''air maintenant le taux d''oxygène dissous.'),
    ('Système de filtration', 'Filtre mécanique ou biologique de traitement de l''eau.'),
    ('Sonde de mesure', 'Sonde de température, pH ou oxygène dissous utilisée pour les relevés de qualité d''eau.'),
    ('Distributeur d''aliment', 'Distributeur automatique ou semi-automatique de provende.'),
    ('Groupe électrogène', 'Source d''alimentation électrique de secours de l''exploitation.'),
    ('Filet et épuisette', 'Matériel de capture utilisé lors des pesées, transferts et récoltes.'),
    ('Balance', 'Balance de pesée des poissons et des sacs de provende.'),
    ('Bâche de bassin', 'Bâche ou géomembrane assurant l''étanchéité d''un bassin.'),
    ('Chambre froide', 'Équipement de conservation du poisson récolté avant la vente.')
) AS v(libelle, description)
WHERE NOT EXISTS (
    SELECT 1 FROM type_equipement t WHERE t.libelle = v.libelle
);
