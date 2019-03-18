package ciir.umass.edu.metric;

import ciir.umass.edu.learning.RankList;
import java.util.List;

public abstract class WeightedMetricScorer extends MetricScorer {

    // score for all list is the weighted average, weight is the ideal score(score of ground truth)
    public double score(List<RankList> rl)
    {
        double score = 0.0;
        double sumIdealScore = 0.0;
        for(int i = 0; i < rl.size(); ++i)
        {
            double weight = idealScore(rl.get(i));
            score += score(rl.get(i)) * weight;
            sumIdealScore += weight;
        }
        return score / sumIdealScore;
    }
}
