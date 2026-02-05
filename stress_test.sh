#!/bin/bash

echo "STARTING HFT STRESS TEST..."
echo "--------------------------------"

# We will spawn 100 concurrent requests.
# Each tries to buy 1 BTC ($50,000).
# Initial Balance: $100,000.
# EXPECTED: 2 Successes, 8 Failures.

for i in {1..100}
do
   (
     response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST http://localhost:8080/api/v1/trading/execute \
       -H "Content-Type: application/json" \
       -d '{"userId": 1, "symbol": "BTC", "payWith": "USD", "quantity": 1.0}')

     echo "Trade #$i: $response"
   ) &
done

# Wait for all background requests to finish
wait

echo "--------------------------------"
echo "TEST COMPLETE. Checking final balance..."
echo "--------------------------------"

# Check the final state
curl -s http://localhost:8080/api/v1/trading/portfolio/1 | json_pp