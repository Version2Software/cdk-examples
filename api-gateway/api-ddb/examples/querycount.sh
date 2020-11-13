#!/bin/bash

curl -X POST -H "x-api-key: $API_KEY" --data "@querycount.json" "$API_ENDPOINT/Query"
