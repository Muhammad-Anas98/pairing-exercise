INSERT INTO organisations_schema.shipments
( shipment_date, organization_id, shipped_items, shipment_amount, order_id)
VALUES
    ('2023-11-15', 'c3f64a6e-d1f0-4a2d-bb39-14ab2e0d37a1',
     '[{"item_id": "962b8b48-7d64-4136-9408-8c31d9da13a3", "quantity": 2}, {"item_id": "962b8b48-7d64-4136-9408-8c31d9da13a4", "quantity": 3}]'::jsonb,
     150.0, '962b8b48-7d64-4136-9408-8c31d9da13a2');
