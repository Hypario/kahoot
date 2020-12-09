#!/usr/bin/env bash

if [ -d "out" ]; then
	rm -rf out
fi

LIBS=""

for LIB in $(find libs -name "*.jar"); do
	LIBS+="$LIB:"
done

LIBS+="src"

mkdir out
javac -cp $LIBS -sourcepath src/ -d out $(find . -name "*.java")

pushd out
	mkdir build

	if [ -f "../.env" ]; then
		cp ../.env ./.env
	fi

	jar -cvfm build/server.jar ../src/server/manifest.mf .
	jar -cvfm build/client.jar ../src/client/manifest.mf .
	jar -cvfm build/database.jar ../src/database/manifest.mf .
popd
