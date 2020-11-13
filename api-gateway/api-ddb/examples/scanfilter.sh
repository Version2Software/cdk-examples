#!/bin/bash

curl -X POST -H "x-api-key: $API_KEY" --data "@scanfilter.json" $API_ENDPOINT/Scan
