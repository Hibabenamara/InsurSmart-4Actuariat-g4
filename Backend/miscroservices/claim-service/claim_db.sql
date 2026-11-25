-- =====================================================================
--  InsurSmart - claim-service : base MySQL dediee (claim_db)
--  Aucune cle etrangere vers un autre microservice :
--  user_id (identite Keycloak) et contract_id sont de simples identifiants.
--  Les seules FK sont internes au service (documents / historique -> claims).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS claim_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE claim_db;

-- ---------------------------------------------------------------------
--  Nettoyage (re-execution possible)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS claim_status_history;
DROP TABLE IF EXISTS claim_documents;
DROP TABLE IF EXISTS claims;

-- ---------------------------------------------------------------------
--  claims
-- ---------------------------------------------------------------------
CREATE TABLE claims (
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    user_id           VARCHAR(255)   NOT NULL COMMENT 'sub Keycloak',
    contract_id       BIGINT         NOT NULL COMMENT 'id du contrat (contract-service)',
    incident_date     DATE           NOT NULL,
    description       VARCHAR(2000)  NOT NULL,
    incident_location VARCHAR(255)   NULL,
    estimated_damage  DECIMAL(12, 2) NOT NULL,
    status            VARCHAR(20)    NOT NULL DEFAULT 'PENDING'
                      COMMENT 'PENDING | UNDER_REVIEW | APPROVED | REJECTED | PAID',
    decision_reason   VARCHAR(1000)  NULL,
    created_at        DATETIME(6)    NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_claims_status
        CHECK (status IN ('PENDING', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'PAID')),
    INDEX idx_claims_user     (user_id),
    INDEX idx_claims_contract (contract_id),
    INDEX idx_claims_status   (status)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  claim_documents (pieces jointes)
-- ---------------------------------------------------------------------
CREATE TABLE claim_documents (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    file_name   VARCHAR(255) NOT NULL,
    file_url    VARCHAR(500) NOT NULL,
    uploaded_at DATETIME(6)  NULL,
    claim_id    BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_documents_claim FOREIGN KEY (claim_id) REFERENCES claims (id) ON DELETE CASCADE,
    INDEX idx_documents_claim (claim_id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  claim_status_history (historique / timeline des statuts)
-- ---------------------------------------------------------------------
CREATE TABLE claim_status_history (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    old_status VARCHAR(20)   NULL COMMENT 'NULL a la creation',
    new_status VARCHAR(20)   NOT NULL,
    changed_by VARCHAR(255)  NOT NULL,
    changed_at DATETIME(6)   NULL,
    comment    VARCHAR(1000) NULL,
    claim_id   BIGINT        NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_history_claim FOREIGN KEY (claim_id) REFERENCES claims (id) ON DELETE CASCADE,
    INDEX idx_history_claim (claim_id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  Jeu de donnees de test (dossier d'Amira)
-- ---------------------------------------------------------------------
INSERT INTO claims (id, user_id, contract_id, incident_date, description, incident_location,
                    estimated_damage, status, decision_reason, created_at)
VALUES
    (1, 'amira-user-id', 1, '2026-11-10', 'Leger accrochage au rond-point, pare-chocs avant endommage.',
     'Tunis - Lac 2', 2000.00, 'UNDER_REVIEW', NULL, '2026-11-11 09:15:00'),
    (2, 'amira-user-id', 1, '2026-10-02', 'Bris de glace lateral.',
     'Ariana', 450.00, 'REJECTED', 'Sinistre hors garantie.', '2026-10-03 14:00:00'),
    (3, 'user-2-id', 2, '2026-11-20', 'Degat des eaux dans la cuisine.',
     'Sousse', 1200.00, 'PENDING', NULL, '2026-11-21 10:30:00');

INSERT INTO claim_documents (file_name, file_url, uploaded_at, claim_id)
VALUES
    ('constat-amiable.pdf', 'uploads/claims/1/constat-amiable.pdf', '2026-11-11 09:20:00', 1),
    ('photo-pare-chocs.jpg', 'uploads/claims/1/photo-pare-chocs.jpg', '2026-11-11 09:21:00', 1);

INSERT INTO claim_status_history (old_status, new_status, changed_by, changed_at, comment, claim_id)
VALUES
    (NULL,          'PENDING',      'amira-user-id', '2026-11-11 09:15:00', 'Sinistre declare',        1),
    ('PENDING',     'UNDER_REVIEW', 'agent-1-id',    '2026-11-12 11:00:00', 'Dossier pris en charge',  1),
    (NULL,          'PENDING',      'amira-user-id', '2026-10-03 14:00:00', 'Sinistre declare',        2),
    ('PENDING',     'UNDER_REVIEW', 'agent-1-id',    '2026-10-04 09:00:00', 'Dossier pris en charge',  2),
    ('UNDER_REVIEW','REJECTED',     'agent-1-id',    '2026-10-05 16:30:00', 'Sinistre hors garantie.', 2),
    (NULL,          'PENDING',      'user-2-id',     '2026-11-21 10:30:00', 'Sinistre declare',        3);
