#!/bin/bash

# parameter $1: modelname $2: testfile

# get score of each model
java -jar target/RankLib-2.12-SNAPSHOT.jar -load models/f1.$1 -rank $2 -score test/$1.f1.score
java -jar target/RankLib-2.12-SNAPSHOT.jar -load models/f2.$1 -rank $2 -score test/$1.f2.score
java -jar target/RankLib-2.12-SNAPSHOT.jar -load models/f3.$1 -rank $2 -score test/$1.f3.score
java -jar target/RankLib-2.12-SNAPSHOT.jar -load models/f4.$1 -rank $2 -score test/$1.f4.score
java -jar target/RankLib-2.12-SNAPSHOT.jar -load models/f5.$1 -rank $2 -score test/$1.f5.score

# format
awk -F'\t' 'BEGIN{OFS=","}{print $3,$1}' test/$1.f1.score > test/$1.f1.score.t
awk -F'\t' 'BEGIN{OFS=","}{print $3,$1}' test/$1.f2.score > test/$1.f2.score.t
awk -F'\t' 'BEGIN{OFS=","}{print $3,$1}' test/$1.f3.score > test/$1.f3.score.t
awk -F'\t' 'BEGIN{OFS=","}{print $3,$1}' test/$1.f4.score > test/$1.f4.score.t
awk -F'\t' 'BEGIN{OFS=","}{print $3,$1}' test/$1.f5.score > test/$1.f5.score.t

# use average score as final score
awk -F',' -f avg.awk test/$1.f1.score.t test/$1.f2.score.t test/$1.f3.score.t test/$1.f4.score.t test/$1.f5.score.t > test/$1.final.score
