#!/bin/sh

# Minimal POSIX gradle wrapper launcher. Relies on `gradle/wrapper/gradle-wrapper.jar`
# being present. If it's missing, run `gradle wrapper` once (using any system Gradle)
# or simply open the project in Android Studio, which will populate the jar
# automatically based on `gradle/wrapper/gradle-wrapper.properties`.

set -e

DIR=$(cd "$(dirname "$0")" && pwd)
WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "ERROR: $WRAPPER_JAR is missing." >&2
  echo "Bootstrap it once with either:" >&2
  echo "  1) Open this folder in Android Studio (recommended), or" >&2
  echo "  2) Install Gradle (https://gradle.org/install/) and run 'gradle wrapper' here." >&2
  exit 1
fi

JAVA_BIN="$(command -v java || true)"
if [ -z "$JAVA_BIN" ]; then
  echo "ERROR: no 'java' on PATH. Install JDK 17." >&2
  exit 1
fi

exec "$JAVA_BIN" -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
