CREATE TABLE IF NOT EXISTS organisations_schema.orders (
    id             UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    total_amount   DOUBLE PRECISION NOT NULL,
    shipped BOOLEAN NOT NULL
);