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

-- Données de test : historique des alertes
INSERT INTO alerte (module_source, type_alerte, niveau_criticite, statut, message, date_creation, date_resolution)
VALUES
('ALIMENTATION', 'STOCK_BAS', 'AVERTISSEMENT', 'RESOLUE',
 'Stock de provende en dessous du seuil minimal (100 kg).', '2026-06-01 08:30:00', '2026-06-02 14:00:00'),

('PRODUCTION', 'MORTALITE_ELEVEE', 'CRITIQUE', 'RESOLUE',
 'Taux de mortalité anormalement élevé sur le lot LOT-001.', '2026-06-05 10:15:00', '2026-06-07 09:00:00'),

('SANITAIRE', 'QUALITE_EAU', 'CRITIQUE', 'IGNOREE',
 'pH hors plage acceptable (4.2) dans le bassin BASSIN-A1.', '2026-06-10 06:00:00', '2026-06-10 18:00:00'),

('EQUIPEMENT', 'MAINTENANCE', 'INFO', 'RESOLUE',
 'Maintenance préventive de la pompe P-03 effectuée.', '2026-06-15 11:00:00', '2026-06-15 16:30:00'),

('PRODUCTION', 'RECOLTE_PROCHE', 'AVERTISSEMENT', 'RESOLUE',
 'Le lot LOT-002 atteint 92% du poids cible de récolte.', '2026-06-20 07:45:00', '2026-06-25 10:00:00'),

('ALIMENTATION', 'PEREMPTION', 'CRITIQUE', 'IGNOREE',
 'Lot de provende PRV-2026-03 périmé depuis le 18/06.', '2026-06-18 09:00:00', '2026-06-19 08:00:00'),

('SANITAIRE', 'SEUIL_DEPASSE', 'AVERTISSEMENT', 'RESOLUE',
 'Température eau à 32°C, seuil max dépassé (30°C).', '2026-06-22 14:20:00', '2026-06-22 17:00:00'),

('PRODUCTION', 'AUTRE', 'INFO', 'RESOLUE',
 'Transfert du lot LOT-003 vers le bassin B-05 effectué.', '2026-06-28 13:00:00', '2026-06-28 13:30:00')

ON CONFLICT DO NOTHING;