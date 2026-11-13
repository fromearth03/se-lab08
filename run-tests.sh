#!/usr/bin/env bash
# compiles everything and runs the junit tests
set -euo pipefail
cd -- "$(dirname -- "$0")"
mkdir -p bin
javac --release 8 -Xlint:-options -cp 'lib/*' -d bin src/recursion/*.java test/recursion/*.java
if [ "$#" -eq 0 ]; then set -- recursion.FileSearchTest recursion.PermutationsTest; fi
java -cp 'bin:lib/*' org.junit.runner.JUnitCore "$@"
