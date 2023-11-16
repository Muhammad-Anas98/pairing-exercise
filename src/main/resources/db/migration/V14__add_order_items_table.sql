CREATE TABLE IF NOT EXISTS organisations_schema.order_items
(
       id       UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
       order_id UUID NOT NULL,
       item_id  UUID NOT NULL,
       FOREIGN KEY (order_id) REFERENCES organisations_schema.orders(id),
       FOREIGN KEY (item_id) REFERENCES organisations_schema.items(id)
);