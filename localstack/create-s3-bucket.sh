#!/usr/bin/env bash
export ENDPOINT=http://localhost:4566

echo "Created Bucket, located in:"
awslocal s3api create-bucket --bucket planets-bucket --output table | cat


# Add a file to bucket
echo ""
echo "Adding file helloworld.txt to the bucket:"

touch helloworld.txt
awslocal s3 cp helloworld.txt s3://planets-bucket/planets-helloworld

#List files in bucket
echo ""
echo "Listing Bucket Files:"

awslocal s3 ls s3://planets-bucket --recursive --human-readable --summarize