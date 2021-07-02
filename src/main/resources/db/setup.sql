CREATE TABLE IF NOT EXISTS market(
    holder VARCHAR(64),
    item VARCHAR(64),
    PRIMARY KEY (holder, item)
);

CREATE TABLE IF NOT EXISTS market_items(
    unique_id VARCHAR(64) PRIMARY KEY,
    item_stack VARCHAR(300),
    expire_at DATE
);