#!/bin/bash

curl -X POST -H "x-api-key: $API_KEY" --data "@putitem.json" "$API_ENDPOINT/PutItem"
