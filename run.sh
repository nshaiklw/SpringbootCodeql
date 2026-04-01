#!/usr/bin/env bash
set -euo pipefail

# Simple helper to build and run the Spring Boot application.
# Usage: ./run.sh [--server.port=8081] [other java args]
# Env:
#   NO_KILL=1    - do not attempt to stop an existing instance of the same jar

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

echo "Building project (skip tests)..."
./mvnw -DskipTests package

JAR=target/springbootCodeql-0.0.1-SNAPSHOT.jar
if [ ! -f "$JAR" ]; then
  echo "ERROR: jar not found: $JAR" >&2
  exit 1
fi

# If there's an existing process running the same jar, stop it unless NO_KILL=1 is set
if [ -z "${NO_KILL:-}" ]; then
  # find PIDs whose command line contains the jar path
  PIDS=$(pgrep -f "${JAR}" || true)
  if [ -n "$PIDS" ]; then
    echo "Found existing process(es) running $JAR: $PIDS"
    echo "Stopping existing process(es) gracefully..."
    kill $PIDS || true
    # wait up to 5s for them to exit
    for i in {1..5}; do
      sleep 1
      STILL=$(pgrep -f "${JAR}" || true)
      if [ -z "$STILL" ]; then
        break
      fi
      echo "waiting for existing process(es) to stop... ($i)"
    done
    # any remaining will be killed hard
    STILL=$(pgrep -f "${JAR}" || true)
    if [ -n "$STILL" ]; then
      echo "Forcing shutdown of remaining process(es): $STILL"
      kill -9 $STILL || true
    fi
  fi
else
  echo "NO_KILL=1 set, will not stop existing instances of $JAR"
fi

# If the user already supplied a --server.port or -Dserver.port, honor it.
PORT_SPECIFIED=""
for a in "$@"; do
  case "$a" in
    --server.port=*) PORT_SPECIFIED="${a#--server.port=}"; break;;
    -Dserver.port=*) PORT_SPECIFIED="${a#-Dserver.port=}"; break;;
  esac
done

if [ -n "$PORT_SPECIFIED" ]; then
  SELECTED_PORT="$PORT_SPECIFIED"
else
  # Try a sequence of candidate ports and pick the first free one.
  CANDIDATES="8080 8081 8082 8083 8084 8085 8086 8087 8088 8089 8090"
  SELECTED_PORT=""
  for p in $CANDIDATES; do
    if ! lsof -iTCP:"$p" -sTCP:LISTEN -n -P >/dev/null 2>&1; then
      SELECTED_PORT="$p"
      break
    fi
  done

  if [ -z "${SELECTED_PORT:-}" ]; then
    echo "ERROR: no free port found in $CANDIDATES" >&2
    exit 1
  fi
  # Append the selected port to the argument list
  set -- "$@" "--server.port=$SELECTED_PORT"
fi

echo "Running jar: $JAR on port ${SELECTED_PORT:-<unknown>}"
exec java -jar "$JAR" "$@"