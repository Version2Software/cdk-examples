#!/bin/bash

curl -X POST -H "x-api-key: $API_KEY" --data "@batchwriteitem.json" "$API_ENDPOINT/BatchWriteItem"
