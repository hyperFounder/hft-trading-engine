#!/bin/bash

# Configuration
URL="http://localhost:8080/api/v1/trading/execute"
PORTFOLIO_URL="http://localhost:8080/api/v1/trading/portfolio/1"
ID_KEY="test-key-$(date +%s)"

echo "--- Initializing Idempotency Test ---"
echo "Target Key: $ID_KEY"

# 1. First Execution
echo "Sending First Request..."
RESPONSE1=$(curl -s -X POST $URL \
  -H "Content-Type: application/json" \
  -H "X-Idempotency-Key: $ID_KEY" \
  -d '{"userId": 1, "symbol": "BTC", "payWith": "USD", "quantity": 1}')
echo "Response 1: $RESPONSE1"

# 2. Immediate Retry (Simulating Network Retry)
echo "Sending Second Request (Same Key)..."
RESPONSE2=$(curl -s -X POST $URL \
  -H "Content-Type: application/json" \
  -H "X-Idempotency-Key: $ID_KEY" \
  -d '{"userId": 1, "symbol": "BTC", "payWith": "USD", "quantity": 1}')
echo "Response 2: $RESPONSE2"

# 3. Validation
echo "--- Results ---"
if [ "$RESPONSE1" == "$RESPONSE2" ]; then
    echo "SUCCESS: Responses match perfectly."
else
    echo "FAILURE: Responses differ."
fi

echo "Checking final balance for User 1..."
curl -s $PORTFOLIO_URL | json_pp || echo "Install json_pp for pretty print"