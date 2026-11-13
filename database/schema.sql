-- ---------------------------------------------------------------------------
-- Personal Expense Tracker - database schema
--
-- Hibernate creates this table automatically on the first run because
-- spring.jpa.hibernate.ddl-auto=update. This script is the readable reference
-- of the same schema and can be executed manually with:
--
--   mysql -u root -p < database/schema.sql
-- ---------------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS expense_tracker_db;
USE expense_tracker_db;

CREATE TABLE IF NOT EXISTS expenses (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    title       VARCHAR(100)    NOT NULL,
    amount      DECIMAL(12, 2)  NOT NULL,
    category    VARCHAR(50)     NOT NULL,
    description VARCHAR(255)    DEFAULT NULL,
    date        DATE            NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- Optional sample rows (use them if you do not want to start with an empty table)
-- INSERT INTO expenses (title, amount, category, description, date) VALUES
--     ('Lunch',      250.00, 'Food',       'Team lunch at office', '2026-11-10'),
--     ('Metro card', 500.00, 'Travel',     'Monthly recharge',     '2026-11-11'),
--     ('Notebook',   120.50, 'Stationery', 'Semester notes',       '2026-11-12');
