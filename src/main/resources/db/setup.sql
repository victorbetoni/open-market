CREATE TABLE IF NOT EXISTS market_items(
    holder VARCHAR(64),
    unique_id VARCHAR(64),
    item_stack VARCHAR(300),
    price DOUBLE,
    expire_at DATE,
    PRIMARY KEY (holder, unique_id)
);