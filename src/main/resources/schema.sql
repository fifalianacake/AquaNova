-- Adjust statut_lot libelle check to include ANNULE
ALTER TABLE IF EXISTS statut_lot DROP CONSTRAINT IF EXISTS statut_lot_libelle_check;
ALTER TABLE IF EXISTS statut_lot ADD CONSTRAINT statut_lot_libelle_check CHECK (libelle IN ('EN_CROISSANCE','RECOLTE_PARTIELLE','CLOTURE','ANNULE'));
