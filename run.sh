#!/bin/sh
# Plays interactively, or pipe a script:  ./run.sh < runs/stage1_victory.txt
java -cp out rr.Main "$@"
