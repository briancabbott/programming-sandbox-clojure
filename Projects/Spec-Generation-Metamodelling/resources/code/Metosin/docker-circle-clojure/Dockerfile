FROM clojure:boot-2.8.2-alpine
RUN apk add --no-cache curl docker git gzip openssh-client py-pip tar py2-yaml \
    python-dev libffi-dev openssl-dev gcc libc-dev make && \
    pip install docker-compose
