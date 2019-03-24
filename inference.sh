#!/bin/bash

# parameter $1: modelname

# get score of each model
java -jar test.jar -load models/f1.$1 -rank test.txt -score test/f1.score
java -jar test.jar -load models/f2.$1 -rank test.txt -score test/f2.score
java -jar test.jar -load models/f3.$1 -rank test.txt -score test/f3.score
java -jar test.jar -load models/f4.$1 -rank test.txt -score test/f4.score
java -jar test.jar -load models/f5.$1 -rank test.txt -score test/f5.score

# format
awk -F'\t' 'BEGIN{OFS=","}{print $3,$1}' test/f1.score > test/f1.score.t
awk -F'\t' 'BEGIN{OFS=","}{print $3,$1}' test/f2.score > test/f2.score.t
awk -F'\t' 'BEGIN{OFS=","}{print $3,$1}' test/f3.score > test/f3.score.t
awk -F'\t' 'BEGIN{OFS=","}{print $3,$1}' test/f4.score > test/f4.score.t
awk -F'\t' 'BEGIN{OFS=","}{print $3,$1}' test/f5.score > test/f5.score.t

# use average score as final score
awk -F',' -f avg.awk test/f1.score.t test/f2.score.t test/f3.score.t test/f4.score.t test/f5.score.t > test/final.score
