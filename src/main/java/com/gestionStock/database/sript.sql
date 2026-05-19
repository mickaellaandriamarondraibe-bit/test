
CREATE TABLE article (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL
);

CREATE TYPE type_mouvement AS ENUM ('ENTREE', 'SORTIE');

CREATE TABLE methode (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE mouvement (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL,
    methode_id BIGINT NOT NULL,
    date_mouvement DATE NOT NULL,
    type_mouvement type_mouvement NOT NULL,
    qte NUMERIC(15, 2) NOT NULL,
    pu NUMERIC(15, 2) NOT NULL,
    valeur NUMERIC(15, 2) NOT NULL,
    stock_apres NUMERIC(15, 2) NOT NULL,
    valeur_stock_apres NUMERIC(15, 2) NOT NULL,
    cump_apres NUMERIC(15, 2),

    CONSTRAINT fk_mouvement_article FOREIGN KEY (article_id) REFERENCES article(id),
    CONSTRAINT fk_mouvement_methode FOREIGN KEY (methode_id) REFERENCES methode(id),
    CONSTRAINT check_qte_positive CHECK (qte > 0),
    CONSTRAINT check_pu_positive CHECK (pu >= 0),
    CONSTRAINT check_valeur_positive CHECK (valeur >= 0),
    CONSTRAINT check_stock_positive CHECK (stock_apres >= 0),
    CONSTRAINT check_valeur_stock_positive CHECK (valeur_stock_apres >= 0)
);


INSERT INTO methode (libelle) VALUES
('FIFO'),
('LIFO'),
('CUMP');
