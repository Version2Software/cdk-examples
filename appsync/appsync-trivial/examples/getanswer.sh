#/bin/bash

curl $GRAPHQL_URL \
-X POST \
-H 'Content-Type: application/json' \
-H "x-api-key: $API_KEY" \
-d '{"query":"query {getAnswer}"}'

printf "\n"
