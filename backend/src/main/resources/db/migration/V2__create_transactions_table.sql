CREATE TABLE transactions (
    id                UUID          PRIMARY KEY,
    user_id           UUID          NOT NULL,
    amount            NUMERIC(19,4) NOT NULL,
    currency          VARCHAR(3)    NOT NULL,
    description       VARCHAR(255)  NOT NULL,
    counterparty_iban VARCHAR(34)   NOT NULL,
    booked_at         TIMESTAMPTZ   NOT NULL,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    version           BIGINT        NOT NULL DEFAULT 0,

    CONSTRAINT fk_transactions_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,

    CONSTRAINT ck_transactions_amount_positive CHECK (amount > 0)
);

CREATE INDEX idx_transactions_user_booked_at
    ON transactions (user_id, booked_at DESC, id DESC);