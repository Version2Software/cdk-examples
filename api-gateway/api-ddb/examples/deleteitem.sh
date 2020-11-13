#!/bin/bash

curl -X POST -H "x-api-key: $API_KEY" --data "@deleteitem.json" "$API_ENDPOINT/DeleteItem"
