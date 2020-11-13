#!/bin/bash

curl -X POST -H "x-api-key: $API_KEY" --data "@query.json" "$API_ENDPOINT/Query"
