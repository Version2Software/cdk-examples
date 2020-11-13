#!/bin/bash

curl -X POST -H "x-api-key: $API_KEY" --data "@updateitem.json" "$API_ENDPOINT/UpdateItem"
