FROM python:3.8-slim as base
FROM base as builder
RUN apt-get -qq update
RUN apt-get install -y --no-install-recommends g++
COPY requirements.txt .
RUN pip install --prefix="/install" -r requirements.txt
FROM base
WORKDIR /loadgen
COPY --from=builder /install /usr/local
COPY locustfile.py .
ENV GEVENT_SUPPORT=True
ENTRYPOINT locust --host="http://${FRONTEND_ADDRESS}" --headless -u "${USERS:-10}" 2>&1
