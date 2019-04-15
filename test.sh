#!/bin/bash

# parameter $1: k $2: model

# generate file only with score
awk -F',' '{print $1}' test/$2.f1.score.t > test/$2.f1.score.o
awk -F',' '{print $1}' test/$2.f2.score.t > test/$2.f2.score.o
awk -F',' '{print $1}' test/$2.f3.score.t > test/$2.f3.score.o
awk -F',' '{print $1}' test/$2.f4.score.t > test/$2.f4.score.o
awk -F',' '{print $1}' test/$2.f5.score.t > test/$2.f5.score.o

# test performance of each model
java -jar target/RankLib-2.12-SNAPSHOT.jar -test test.txt -score test/$2.f1.score.o -metric2T WINDCG@$1
java -jar target/RankLib-2.12-SNAPSHOT.jar -test test.txt -score test/$2.f2.score.o -metric2T WINDCG@$1
java -jar target/RankLib-2.12-SNAPSHOT.jar -test test.txt -score test/$2.f3.score.o -metric2T WINDCG@$1
java -jar target/RankLib-2.12-SNAPSHOT.jar -test test.txt -score test/$2.f4.score.o -metric2T WINDCG@$1
java -jar target/RankLib-2.12-SNAPSHOT.jar -test test.txt -score test/$2.f5.score.o -metric2T WINDCG@$1

# generate final file only with score
awk -F',' '{print $1}' test/$2.final.score > test/$2.final.score.o

# test final performance
java -jar target/RankLib-2.12-SNAPSHOT.jar -test test.txt -score test/$2.final.score.o -metric2T WINDCG@$1
