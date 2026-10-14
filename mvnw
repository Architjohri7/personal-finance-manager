#!/bin/sh
# Lightweight Maven bootstrap for systems without Maven installed.
set -eu
MAVEN_VERSION=3.9.9
MAVEN_HOME_DIR="${HOME}/.m2/wrapper/dists/apache-maven-${MAVEN_VERSION}"
MAVEN_BIN="${MAVEN_HOME_DIR}/apache-maven-${MAVEN_VERSION}/bin/mvn"
if command -v mvn >/dev/null 2>&1; then exec mvn "$@"; fi
if [ ! -x "$MAVEN_BIN" ]; then
  mkdir -p "$MAVEN_HOME_DIR"
  ARCHIVE="${MAVEN_HOME_DIR}/maven.tar.gz"
  echo "Maven not found. Downloading Maven ${MAVEN_VERSION}..."
  curl -fL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -o "$ARCHIVE"
  tar -xzf "$ARCHIVE" -C "$MAVEN_HOME_DIR"
  rm -f "$ARCHIVE"
fi
exec "$MAVEN_BIN" "$@"
