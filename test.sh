#!/bin/bash

# parameter $1: k

# generate file only with score
awk -F',' '{print $1}' test/f1.score.t > test/f1.score.o
awk -F',' '{print $1}' test/f2.score.t > test/f2.score.o
awk -F',' '{print $1}' test/f3.score.t > test/f3.score.o
awk -F',' '{print $1}' test/f4.score.t > test/f4.score.o
awk -F',' '{print $1}' test/f5.score.t > test/f5.score.o

# test performance of each model
java -jar test.jar -test test.txt -score test/f1.score.o -metric2T WINDCG@$1
java -jar test.jar -test test.txt -score test/f2.score.o -metric2T WINDCG@$1
java -jar test.jar -test test.txt -score test/f3.score.o -metric2T WINDCG@$1
java -jar test.jar -test test.txt -score test/f4.score.o -metric2T WINDCG@$1
java -jar test.jar -test test.txt -score test/f5.score.o -metric2T WINDCG@$1

# generate final file only with score
awk -F',' '{print $1}' test/final.score > test/final.score.o

# test final performance
java -jar test.jar -test test.txt -score test/final.score.o -metric2T WINDCG@$1
