#FROM postgres:14-alpine
FROM timescale/timescaledb:latest-pg14
EXPOSE 8080
ENV POSTGRES_USER=narrative
ENV POSTGRES_PASSWORD=narrative
ENV POSTGRES_DB=narrative
ENV PGDATA=/var/lib/postgresql/data
RUN apk add openjdk17-jre
WORKDIR /opt/docker
ADD target/docker/stage/1/opt/docker /opt/docker
ADD target/docker/stage/2/opt/docker /opt/docker
ADD docker/start_svc.sh /opt/docker
RUN chmod 777 /opt/docker/start_svc.sh
RUN chmod 777 /opt/docker/bin/narrative-test
ENTRYPOINT ["/opt/docker/start_svc.sh"]
CMD ["postgres"]
