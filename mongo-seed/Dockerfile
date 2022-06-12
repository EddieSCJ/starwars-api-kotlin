FROM mongo

COPY init.json /init.json
CMD mongoimport --uri mongodb://admin:password@mongodb:27017/starwars?authSource=admin --db starwars --collection  user  --type json --file /init.json --jsonArray 