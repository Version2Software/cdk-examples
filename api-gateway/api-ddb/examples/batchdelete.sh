#!/bin/bash

curl -X POST -H "x-api-key: $API_KEY" --data "@batchdelete.json" "$API_ENDPOINT/BatchWriteItem"
