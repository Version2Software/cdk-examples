#!/bin/bash

curl -X POST -H "x-api-key: $API_KEY" --data "@getitem.json" "$API_ENDPOINT/GetItem"
