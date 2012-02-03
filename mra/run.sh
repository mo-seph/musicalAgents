#!/bin/bash

JADE=lib/jade
java -classpath ./bin:$JADE/jade.jar:$JADE/iiop.jar:$JADE/http.jar:$JADE/Base64.jar com.moseph.mra.agent.run.ScoreAgents
