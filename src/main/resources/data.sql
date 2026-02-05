-- 1. Create Assets (Market Data)
INSERT INTO assets (symbol, price, version) VALUES ('BTC', 50000.00, 0);
INSERT INTO assets (symbol, price, version) VALUES ('USD', 1.00, 0);

-- 2. Create a Portfolio for User ID 1 (The Trader)
-- Give them $100,000 USD to start
INSERT INTO portfolios (user_id, asset_symbol, quantity) VALUES (1, 'USD', 100000.00);