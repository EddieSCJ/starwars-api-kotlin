#!/usr/bin/env bash
# Run the app using local stack SQS and S3

export SQS_QUEUE_URL=http://localhost:4566/queue/planet-delete.fifo
export SQS_ENDPOINT=http://localhost:4566
export SQS_REGION=us-east-1
# AWS_PROFILE needs to be set, but dev isn't actually used

# Create the queue
aws --endpoint-url $SQS_ENDPOINT sqs create-queue --queue-name planet-delete.fifo --region us-east-1 --attributes '{"FifoQueue":"true", "ContentBasedDeduplication":"true"}' --output table | cat

# Clear any existing messages from the queue
aws --endpoint-url $SQS_ENDPOINT sqs purge-queue --region us-east-1 --queue-url $SQS_QUEUE_URL