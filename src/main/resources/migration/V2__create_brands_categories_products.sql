CREATE TABLE brands (
    id SERIAL PRIMARY KEY,
    name VARCHAR UNIQUE,
    description TEXT
);

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR UNIQUE,
    description TEXT
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    brand_id INTEGER,
    category_id INTEGER,
    price NUMERIC NOT NULL,
    stock_quantity INTEGER,
    description TEXT,
    CONSTRAINT fk_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id)
);