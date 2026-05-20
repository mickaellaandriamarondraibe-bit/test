DROP TABLE IF EXISTS mouvement_source CASCADE;
DROP TABLE IF EXISTS mouvement CASCADE;
DROP TABLE IF EXISTS article CASCADE;
DROP TABLE IF EXISTS methode CASCADE;
DROP TYPE IF EXISTS type_mouvement CASCADE;

CREATE TYPE type_mouvement AS ENUM ('ENTREE', 'SORTIE');

CREATE TABLE methode (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE article (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    methode_id BIGINT NOT NULL REFERENCES methode(id)
);

CREATE TABLE mouvement (
    id BIGSERIAL PRIMARY KEY,

    article_id BIGINT NOT NULL REFERENCES article(id),
    methode_id BIGINT NOT NULL REFERENCES methode(id),

    date_mouvement DATE NOT NULL,
    type_mouvement type_mouvement NOT NULL,

    qte NUMERIC(15,2) NOT NULL,
    pu NUMERIC(15,2) NOT NULL,
    valeur NUMERIC(15,2) NOT NULL,

    stock_apres NUMERIC(15,2) NOT NULL,
    valeur_stock_apres NUMERIC(15,2) NOT NULL,
    cump_apres NUMERIC(15,2)
);

CREATE TABLE mouvement_source (
    id BIGSERIAL PRIMARY KEY,

    mouvement_sortie_id BIGINT NOT NULL REFERENCES mouvement(id),
    mouvement_entree_id BIGINT NOT NULL REFERENCES mouvement(id),

    qte_utilisee NUMERIC(15,2) NOT NULL,
    pu_utilise NUMERIC(15,2) NOT NULL,
    valeur_utilisee NUMERIC(15,2) NOT NULL
);

INSERT INTO methode(libelle) VALUES
('FIFO'),
('LIFO'),
('CUMP');

INSERT INTO article(libelle, methode_id) VALUES
('yaourt', 1);

-- Exemple avec methode fixe par article (FIFO ici)
-- Entrée 1 : 5 à 100
INSERT INTO mouvement (
    article_id, methode_id, date_mouvement, type_mouvement,
    qte, pu, valeur, stock_apres, valeur_stock_apres, cump_apres
) VALUES (
    1, 1, '2026-05-19', 'ENTREE',
    5, 100, 500, 5, 500, 100
);

-- Entrée 2 : 10 à 110
INSERT INTO mouvement (
    article_id, methode_id, date_mouvement, type_mouvement,
    qte, pu, valeur, stock_apres, valeur_stock_apres, cump_apres
) VALUES (
    1, 1, '2026-05-20', 'ENTREE',
    10, 110, 1100, 15, 1600, 106.67
);

-- Entrée 3 : 5 à 95
INSERT INTO mouvement (
    article_id, methode_id, date_mouvement, type_mouvement,
    qte, pu, valeur, stock_apres, valeur_stock_apres, cump_apres
) VALUES (
    1, 1, '2026-05-21', 'ENTREE',
    5, 95, 475, 20, 2075, 103.75
);

-- Sortie FIFO : 13
-- 5 à 95 + 8 à 110 = 1355
INSERT INTO mouvement (
    article_id, methode_id, date_mouvement, type_mouvement,
    qte, pu, valeur, stock_apres, valeur_stock_apres, cump_apres
) VALUES (
    1, 1, '2026-05-22', 'SORTIE',
    13, 104.23, 1355, 7, 720, 102.86
);

-- Détail source de la sortie LIFO
INSERT INTO mouvement_source (
    mouvement_sortie_id,
    mouvement_entree_id,
    qte_utilisee,
    pu_utilise,
    valeur_utilisee
) VALUES
(4, 3, 5, 95, 475),
(4, 2, 8, 110, 880);



ALTER TABLE mouvement_source DROP CONSTRAINT mouvement_source_mouvement_sortie_id_fkey;
ALTER TABLE mouvement_source DROP CONSTRAINT mouvement_source_mouvement_entree_id_fkey;

ALTER TABLE mouvement_source
  ADD CONSTRAINT mouvement_source_mouvement_sortie_id_fkey
  FOREIGN KEY (mouvement_sortie_id) REFERENCES mouvement(id) ON DELETE CASCADE;

ALTER TABLE mouvement_source
  ADD CONSTRAINT mouvement_source_mouvement_entree_id_fkey
  FOREIGN KEY (mouvement_entree_id) REFERENCES mouvement(id) ON DELETE CASCADE;