package ciir.umass.edu.learning.tree;

import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.metric.MetricScorer;
import ciir.umass.edu.utilities.MergeSorter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Improved LambdaMART algorithm.
 */
public class WLambdaMART extends LambdaMART {

    protected double sumIdealScore = -1;

    public WLambdaMART()
    {
        super();
    }

    public WLambdaMART(List<RankList> samples, int[] features, MetricScorer scorer)
    {
        super(samples, features, scorer);
    }

    public String name()
    {
        return "WLambdaMART";
    }

    public Ranker createNew()
    {
        return new WLambdaMART();
    }

    // use cache to compute weighted overall score
    protected float computeModelScoreOnTraining()
    {
        int current = 0;
        List<RankList> rlList = new ArrayList<RankList>();
        for (int i = 0; i < samples.size(); ++i)
        {
            rlList.add(rank(i, current));
            current += samples.get(i).size();
        }

        return (float)scorer.score(rlList);
    }

    // use cache to compute weighted overall score
    protected float computeModelScoreOnValidation()
    {
        List<RankList> rlList = new ArrayList<RankList>();
        for (int i = 0; i < validationSamples.size(); ++i)
        {
            int[] idx = MergeSorter.sort(modelScoresOnValidation[i], false);
            rlList.add(new RankList(validationSamples.get(i), idx));
        }

        return (float)scorer.score(rlList);
    }

    /*protected void computePseudoResponses()
    {
        super.computePseudoResponses();

        // only need to sum up once
        if (sumIdealScore < 0)
        {
            double sum = 0;
            for (int i = 0; i < samples.size(); ++i)
            {
                sum += scorer.idealScore(samples.get(i));
            }
            sumIdealScore = sum;
        }

        if (sumIdealScore <= 0) {System.err.println("sum of ideal score less than zero!"); return;}
        // need to multiply size of samples since it will gonna do the average
        for (int i = 0; i < weights.length; ++i)
        {
            weights[i] = weights[i] / sumIdealScore;
            pseudoResponses[i] = pseudoResponses[i] / sumIdealScore;
            // weights[i] = weights[i];
            // pseudoResponses[i] = pseudoResponses[i];
            // weights[i] = weights[i] * samples.size() / sumIdealScore;
            // pseudoResponses[i] = pseudoResponses[i] * samples.size() / sumIdealScore;
        }
    }*/

}
