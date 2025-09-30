

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR UNIQUE,
    password TEXT,
    full_name VARCHAR,
    email VARCHAR,
    role VARCHAR NOT NULL,
    is_locked BOOLEAN,
    created_at TIMESTAMP
);



CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR,
    email VARCHAR,
    phone VARCHAR,
    address TEXT,
    created_at TIMESTAMP
);