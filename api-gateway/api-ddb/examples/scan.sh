#!/bin/bash

curl -X POST -H "x-api-key: $API_KEY" --data "@scan.json" $API_ENDPOINT/Scan
