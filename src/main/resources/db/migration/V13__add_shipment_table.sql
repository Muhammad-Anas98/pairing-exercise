CREATE TABLE IF NOT EXISTS organisations_schema.shipments
(
    id              UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    shipment_date   DATE NOT NULL,
    organization_id UUID NOT NULL,
    shipped_items   JSONB,
    shipment_amount DOUBLE PRECISION NOT NULL,
    order_id UUID   NOT NULL,
    FOREIGN KEY (order_id) REFERENCES organisations_schema.orders(id)
);