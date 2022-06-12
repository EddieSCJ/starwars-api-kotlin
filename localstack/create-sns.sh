#!/usr/bin/env bash
# Run the app using local stack SQS and S3

export SNS_ENDPOINT=http://localhost:4566
export SNS_REGION=us-east-1

# Create the queue
aws --endpoint-url $SNS_ENDPOINT sns create-topic --name starwars-planet-delete --region $SNS_REGION --output table | cat
