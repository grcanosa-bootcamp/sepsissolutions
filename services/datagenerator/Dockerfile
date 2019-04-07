FROM alpine:3.9

RUN apk add --update coreutils && rm -rf /var/cache/apk/*
VOLUME /data
WORKDIR /work

COPY BASE.csv /work/
COPY *.sh /work/

CMD ["/work/startmanual.sh","/data/"]