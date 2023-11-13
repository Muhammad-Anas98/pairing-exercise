CREATE TABLE IF NOT EXISTS organisations_schema.items (
    id         UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    item_name  VARCHAR(255) NOT NULL,
    quantity   INT NOT NULL,
    price      DOUBLE PRECISION NOT NULL
);