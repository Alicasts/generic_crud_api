CREATE TABLE IF NOT EXISTS users (
  id           BIGSERIAL PRIMARY KEY,
  name         VARCHAR(120)  NOT NULL,
  email        VARCHAR(254)  NOT NULL,
  age          INTEGER       NOT NULL CHECK (age BETWEEN 0 AND 130),
  cpf          VARCHAR(11)   NOT NULL CHECK (cpf ~ '^[0-9]{11}$'),
  cep          VARCHAR(8)    NOT NULL CHECK (cep ~ '^[0-9]{8}$'),
  address      VARCHAR(200)  NOT NULL,
  sex          VARCHAR(16)   NOT NULL CHECK (sex IN ('MALE','FEMALE','OTHER','UNSPECIFIED')),
  created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  CONSTRAINT uk_users_email UNIQUE (email),
  CONSTRAINT uk_users_cpf   UNIQUE (cpf)
);