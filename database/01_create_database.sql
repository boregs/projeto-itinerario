-- PostgreSQL 17. Execute conectado ao banco postgres como administrador.
-- Execute cada comando separadamente, com autocommit (CREATE DATABASE não aceita transação).
-- Execute somente uma vez. Não remove nem sobrescreve bancos ou usuários existentes.
CREATE ROLE itinerario LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE;

CREATE DATABASE itinerario OWNER itinerario ENCODING 'UTF8' TEMPLATE template0;

-- Defina a senha fora deste arquivo:
-- psql: \password itinerario
-- pgAdmin: Login/Group Roles > itinerario > Properties > Definition > Password.
-- Conecte a aplicação com este usuário. O Flyway criará e versionará as tabelas.
