package ciir.umass.edu.learning.tree;

import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.metric.WeightedMetricScorer;
import ciir.umass.edu.utilities.MergeSorter;

import java.util.ArrayList;
import java.util.List;

public class WLambdaMART extends LambdaMART {

    protected double sumWeight = -1;

    public WLambdaMART()
    {
        super();
    }

    public WLambdaMART(List<RankList> samples, int[] features, WeightedMetricScorer scorer)
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

    protected void computePseudoResponses()
    {
        super.computePseudoResponses();

        // only need to sum up once, here only have one thread
        if (sumWeight < 0)
        {
            double sum = 0;
            for (int i = 0; i < samples.size(); ++i)
            {
                sum += scorer.weight(samples.get(i));
            }
            sumWeight = sum;
        }

        if (sumWeight <= 0) {System.err.println("sum of weight score less than zero!"); return;}
        for (int i = 0; i < weights.length; ++i)
        {
            weights[i] /= sumWeight;
            pseudoResponses[i] /= sumWeight;
        }
    }

}
