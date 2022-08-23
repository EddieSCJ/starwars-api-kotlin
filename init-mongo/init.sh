#!/usr/bin/env bash

echo "Start inserting data into mongo database"
mongoimport --uri mongodb://admin:password@mongodb:27017/starwars?authSource=admin --db starwars --collection  user  --type json --file /init.json --jsonArray
echo "Data inserted successfully"