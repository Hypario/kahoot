#!/usr/bin/env bash

LIBS=""

for lib in $(find ./libs -name *.jar); do
  LIBS+="$lib:"
done

LIBS+="src"

mkdir out
javac -cp $LIBS -sourcepath src $(find . -name '*.java') -d out

pushd out || exit
  mkdir build
  jar -cvfm build/server.jar ../src/server/META-INF/MANIFEST.MF .
  jar -cvfm build/client.jar ../src/client/META-INF/MANIFEST.MF .
  jar -cvfm build/database.jar ../src/database/META-INF/MANIFEST.MF .
popd || exit
