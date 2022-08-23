FROM bitnami/kafka:latest
USER root

COPY init-kafka.sh .

RUN chmod +x init-kafka.sh
CMD ./init-kafka.sh