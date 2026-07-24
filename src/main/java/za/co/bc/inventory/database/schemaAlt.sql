-- ============================================================
-- UNIVERSITY CLEANING INVENTORY & ISSUANCE SYSTEM
-- PostgreSQL Database Setup
-- ============================================================


-- ============================================================
-- DROP EXISTING TABLES
-- ============================================================

-- Child tables must be removed before parent tables because
-- of foreign-key relationships.

DROP TABLE IF EXISTS stock_issuance CASCADE;
DROP TABLE IF EXISTS material CASCADE;
DROP TABLE IF EXISTS cleaners CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS users CASCADE;


-- ============================================================
-- USERS
-- ============================================================

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,

                       full_name VARCHAR(120) NOT NULL,

                       username VARCHAR(60) NOT NULL UNIQUE,

                       email VARCHAR(150) NOT NULL UNIQUE,

                       password VARCHAR(255) NOT NULL,

                       role VARCHAR(30) NOT NULL,

                       CONSTRAINT chk_user_role
                           CHECK (role IN ('Supervisor', 'Storekeeper'))
);


-- ============================================================
-- SUPPLIERS
-- ============================================================

CREATE TABLE suppliers (
                           id SERIAL PRIMARY KEY,

                           name VARCHAR(120) NOT NULL,

                           contact_person VARCHAR(120) NOT NULL,

                           phone VARCHAR(25) NOT NULL,

                           email VARCHAR(150) NOT NULL UNIQUE,

                           address VARCHAR(250) NOT NULL
);


-- ============================================================
-- CLEANERS
-- ============================================================

CREATE TABLE cleaners (
                          id SERIAL PRIMARY KEY,

                          full_name VARCHAR(120) NOT NULL,

                          employee_number VARCHAR(40) NOT NULL UNIQUE,

                          phone VARCHAR(25) NOT NULL,

                          email VARCHAR(150) NOT NULL UNIQUE,

                          department VARCHAR(120) NOT NULL
);


-- ============================================================
-- MATERIALS
-- ============================================================

CREATE TABLE material (
                          id SERIAL PRIMARY KEY,

                          name VARCHAR(120) NOT NULL,

                          description VARCHAR(255),

                          category VARCHAR(100) NOT NULL,

                          quantity INTEGER NOT NULL DEFAULT 0,

                          unit VARCHAR(50) NOT NULL,

                          supplier VARCHAR(120),

                          status VARCHAR(50) NOT NULL DEFAULT 'Available',

                          reorder_level INTEGER NOT NULL DEFAULT 10,

                          CONSTRAINT chk_material_quantity
                              CHECK (quantity >= 0),

                          CONSTRAINT chk_reorder_level
                              CHECK (reorder_level >= 0)
);


-- ============================================================
-- STOCK ISSUANCE
-- ============================================================

CREATE TABLE stock_issuance (
                                id SERIAL PRIMARY KEY,

                                material_id INTEGER NOT NULL,

                                cleaner_id INTEGER NOT NULL,

                                quantity INTEGER NOT NULL,

                                issued_by_user INTEGER NOT NULL,

                                issue_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_stock_material
                                    FOREIGN KEY (material_id)
                                        REFERENCES material(id)
                                        ON UPDATE CASCADE
                                        ON DELETE RESTRICT,

                                CONSTRAINT fk_stock_cleaner
                                    FOREIGN KEY (cleaner_id)
                                        REFERENCES cleaners(id)
                                        ON UPDATE CASCADE
                                        ON DELETE RESTRICT,

                                CONSTRAINT fk_stock_user
                                    FOREIGN KEY (issued_by_user)
                                        REFERENCES users(id)
                                        ON UPDATE CASCADE
                                        ON DELETE RESTRICT,

                                CONSTRAINT chk_issued_quantity
                                    CHECK (quantity > 0)
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_material_name
    ON material(name);

CREATE INDEX idx_material_category
    ON material(category);

CREATE INDEX idx_material_quantity
    ON material(quantity);

CREATE INDEX idx_cleaners_name
    ON cleaners(full_name);

CREATE INDEX idx_cleaners_department
    ON cleaners(department);

CREATE INDEX idx_suppliers_name
    ON suppliers(name);

CREATE INDEX idx_stock_material
    ON stock_issuance(material_id);

CREATE INDEX idx_stock_cleaner
    ON stock_issuance(cleaner_id);

CREATE INDEX idx_stock_issue_date
    ON stock_issuance(issue_date);


-- ============================================================
-- DUMMY USERS
-- ============================================================

INSERT INTO users
(full_name, username, email, password, role)
VALUES
    (
        'System Supervisor',
        'supervisor',
        'supervisor@university.ac.za',
        'Password123',
        'Supervisor'
    ),
    (
        'Main Storekeeper',
        'storekeeper',
        'storekeeper@university.ac.za',
        'Password123',
        'Storekeeper'
    );


-- ============================================================
-- DUMMY SUPPLIERS
-- ============================================================

INSERT INTO suppliers
(name, contact_person, phone, email, address)
VALUES
    (
        'CleanPro Supplies',
        'Sarah Jacobs',
        '0125550101',
        'sales@cleanpro.co.za',
        '15 Industrial Road, Pretoria'
    ),
    (
        'Hygiene Solutions',
        'Michael Dlamini',
        '0115550202',
        'orders@hygienesolutions.co.za',
        '24 Main Reef Road, Johannesburg'
    ),
    (
        'Campus Cleaning Distributors',
        'Lerato Molefe',
        '0125550303',
        'info@campuscleaning.co.za',
        '80 University Avenue, Pretoria'
    ),
    (
        'EcoClean Products',
        'Daniel Smith',
        '0215550404',
        'sales@ecoclean.co.za',
        '19 Green Street, Cape Town'
    );


-- ============================================================
-- DUMMY CLEANERS
-- ============================================================

INSERT INTO cleaners
(
    full_name,
    employee_number,
    phone,
    email,
    department
)
VALUES
    (
        'Nomsa Nkosi',
        'CLN001',
        '0725551001',
        'nomsa.nkosi@university.ac.za',
        'Administration'
    ),
    (
        'Thabo Mokoena',
        'CLN002',
        '0735551002',
        'thabo.mokoena@university.ac.za',
        'Computer Science'
    ),
    (
        'Lindiwe Khumalo',
        'CLN003',
        '0745551003',
        'lindiwe.khumalo@university.ac.za',
        'Library'
    ),
    (
        'Sipho Dlamini',
        'CLN004',
        '0765551004',
        'sipho.dlamini@university.ac.za',
        'Engineering'
    ),
    (
        'Ayanda Ndlovu',
        'CLN005',
        '0785551005',
        'ayanda.ndlovu@university.ac.za',
        'Student Residence'
    );


-- ============================================================
-- DUMMY MATERIALS
-- ============================================================

INSERT INTO material
(
    name,
    description,
    category,
    quantity,
    unit,
    supplier,
    status,
    reorder_level
)
VALUES
    (
        'Floor Cleaner',
        'Industrial floor cleaning liquid',
        'Cleaning Chemicals',
        50,
        'Litres',
        'CleanPro Supplies',
        'Available',
        10
    ),
    (
        'Disinfectant',
        'Multi-purpose surface disinfectant',
        'Cleaning Chemicals',
        8,
        'Litres',
        'Hygiene Solutions',
        'Low Stock',
        10
    ),
    (
        'Mop Heads',
        'Replacement cotton mop heads',
        'Cleaning Equipment',
        30,
        'Units',
        'Campus Cleaning Distributors',
        'Available',
        5
    ),
    (
        'Heavy Duty Gloves',
        'Reusable protective cleaning gloves',
        'Protective Equipment',
        5,
        'Pairs',
        'EcoClean Products',
        'Low Stock',
        10
    ),
    (
        'Black Refuse Bags',
        'Heavy-duty refuse bags',
        'Waste Management',
        100,
        'Bags',
        'CleanPro Supplies',
        'Available',
        20
    ),
    (
        'Paper Towels',
        'Industrial paper towel rolls',
        'Consumables',
        45,
        'Rolls',
        'Hygiene Solutions',
        'Available',
        15
    );


-- ============================================================
-- DUMMY STOCK ISSUANCE HISTORY
-- ============================================================

-- issued_by_user is an INTEGER and references users.id.

INSERT INTO stock_issuance
(
    material_id,
    cleaner_id,
    quantity,
    issued_by_user,
    issue_date
)
VALUES
    (
        1,
        1,
        2,
        1,
        CURRENT_TIMESTAMP - INTERVAL '3 days'
    ),
    (
        2,
        2,
        2,
        1,
        CURRENT_TIMESTAMP - INTERVAL '2 days'
    ),
    (
        3,
        3,
        1,
        2,
        CURRENT_TIMESTAMP - INTERVAL '1 day'
    ),
    (
        1,
        4,
        3,
        2,
        CURRENT_TIMESTAMP - INTERVAL '5 hours'
    ),
    (
        4,
        5,
        2,
        1,
        CURRENT_TIMESTAMP - INTERVAL '1 hour'
    );


-- ============================================================
-- VERIFY DATABASE
-- ============================================================

SELECT * FROM users ORDER BY id;

SELECT * FROM suppliers ORDER BY id;

SELECT * FROM cleaners ORDER BY id;

SELECT * FROM material ORDER BY id;

SELECT * FROM stock_issuance
ORDER BY issue_date DESC;