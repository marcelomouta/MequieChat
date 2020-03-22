#!/bin/bash

if [ -d "server/bin" ] ; then
    rm -Rf server/bin/*
fi

if [ -d "client/bin" ] ; then
    rm -Rf client/bin/*
fi

# compile server
javac -d server/bin -cp server/src server/src/mequie/main/MequieServer.java

#compile client
javac -d client/bin -cp client/src:server/src client/src/mequie/main/Mequie.java

echo "MequieChat (client & server) compilled with success :-)"
