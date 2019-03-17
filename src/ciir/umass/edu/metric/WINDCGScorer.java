package ciir.umass.edu.metric;

import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.utilities.Sorter;

import java.util.Arrays;
import java.util.HashMap;

// Weighted Improved NDCG Scorer
public class WINDCGScorer extends WeightedMetricScorer{
    protected HashMap<String, Double> idealIDCGScore = null;

    public WINDCGScorer()
    {
        super();
        idealIDCGScore = new HashMap<>();
    }

    @Override
    public double idealScore(RankList rl) {
        if(rl.size() == 0) return 0;

        // check cache first
        Double s = idealIDCGScore.get(rl.getID());
        if (s != null) return s;

        // compute ideal score
        int size = (k > rl.size() || k <= 0) ? rl.size() : k;
        double rel[] = getNormalizedRelevanceLabels(rl);
        double score = getIDCGScore(rel, size, true);
        idealIDCGScore.put(rl.getID(), score);

        return score;
    }

    @Override
    public double score(RankList rl) {
        if(rl.size() == 0) return 0;

        // compute score
        int size = (k > rl.size() || k <= 0) ? rl.size() : k;
        double rel[] = getNormalizedRelevanceLabels(rl);
        double score = getIDCGScore(rel, size, false);

        // compute ideal score
        double idealScore = 0;
        // check cache first
        Double s = idealIDCGScore.get(rl.getID());
        if (s != null) idealScore = s;
        else
        {
            idealScore = getIDCGScore(rel, size, true);
            idealIDCGScore.put(rl.getID(), idealScore);
        }
        if (idealScore <= 0) {System.out.println("ideal score <= 0! " + idealScore); return 0;}

        // return score normalized by ideal
        return score / idealScore;
    }

    protected double getIDCGScore(double rel[], int topK, Boolean ideal)
    {
        double score = 0.0;
        if (ideal)
        {
            int[] idx = Sorter.sort(rel, false);
            for (int i = 0; i < topK; ++i)
            {
                score += gain(rel[idx[i]]) * discount(i);
            }
        }
        else
        {
            for (int i = 0; i < topK; ++i)
            {
                score += gain(rel[i]) * discount(i);
            }
        }
        return score;
    }

    @Override
    public MetricScorer copy() {
        return new WINDCGScorer();
    }

    @Override
    public String name() {
        return "WINDCG@" + k;
    }

    @Override
    public double[][] swapChange(RankList rl) {
        // initialize changes
        double[][] changes = new double[rl.size()][];
        for(int i=0;i<rl.size();i++)
        {
            changes[i] = new double[rl.size()];
            Arrays.fill(changes[i], 0);
        }

        int size = (k > rl.size() || k <= 0) ? rl.size() : k;
        double rel[] = getNormalizedRelevanceLabels(rl);

        // compute ideal score
        double idealScore = 0;
        // check cache first
        Double s = idealIDCGScore.get(rl.getID());
        idealScore = (s != null) ? s : getIDCGScore(rel, size, true);

        if (idealScore <= 0) {System.out.println("ideal score <= 0! " + idealScore); return changes;}

        for(int i=0;i<size;i++)
        {
            for(int j=i+1;j<rl.size();j++)
            {
                changes[j][i] = changes[i][j] = (discount(i) - discount(j)) * (gain(rel[i]) - gain(rel[j])) / idealScore;
            }
        }

        return changes;
    }

    // rel: ground truth / normalized ground truth
    protected double gain(double rel)
    {
        return Math.pow(2.0, rel)-1;
    }

    // pos: the ranking position in the list
    protected double discount(int pos)
    {
        return 1 / (1<<(pos-1));
    }
}
