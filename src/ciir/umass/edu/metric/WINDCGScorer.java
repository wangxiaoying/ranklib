package ciir.umass.edu.metric;

import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.utilities.Sorter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

// Weighted Improved NDCG Scorer
public class WINDCGScorer extends MetricScorer{
    protected HashMap<String, Double> idealIDCGScore = null;
    protected double decayRate = 0.5;
    protected double rewardRate = 1.0;
    protected double[] discount = null;//cache

    public WINDCGScorer()
    {
        super();
        idealIDCGScore = new HashMap<>();
        if(discount == null)
        {
            discount = new double[100];
            discount[0] = 1;
            for(int i = 1; i < discount.length; i++)
                discount[i] = discount[i - 1] * (1 - (decayRate / k));
        }
    }

    public void setDecayRate(double dr) {decayRate = dr;}
    public void setRewardRate(double rr) {rewardRate = rr;}

    @Override
    public double idealScore(RankList rl) {
        if(rl.size() == 0) return 0;

        // check cache first
        Double s = idealIDCGScore.get(rl.getID());
        if (s != null) return s;

        // compute ideal score
        int size = (k > rl.size() || k <= 0) ? rl.size() : k;
        int rel[] = getRelevanceLabels(rl);
        double score = getIDCGScore(rel, size, true);
        idealIDCGScore.put(rl.getID(), score);

        return score;
    }

    @Override
    public double score(RankList rl) {
        if(rl.size() == 0) return 0;

        // compute score
        int size = (k > rl.size() || k <= 0) ? rl.size() : k;
        int rel[] = getRelevanceLabels(rl);
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
        if (idealScore <= 0) {System.err.println("ideal score <= 0! " + idealScore); return 0;}
        if (score > idealScore) System.err.println("score is larger than ideal: " + score + ">" + idealScore);

        // return score normalized by ideal
        return score / idealScore;
    }

    // un-normalized score
    public double naiveScore(RankList rl)
    {
        if(rl.size() == 0) return 0;

        // compute score
        int size = (k > rl.size() || k <= 0) ? rl.size() : k;
        int rel[] = getRelevanceLabels(rl);
        return getIDCGScore(rel, size, false);
    }

    // optimize here: SUM(score/ideal*ideal)/sumIdeal = SUM(score)/sumIdeal
    public double score(List<RankList> rl)
    {
        double score = 0.0;
        double sumIdealScore = 0.0;
        for(int i = 0; i < rl.size(); ++i)
        {
            score += naiveScore(rl.get(i));
            sumIdealScore += idealScore(rl.get(i));
        }
        return score / sumIdealScore;
    }

    protected double getIDCGScore(int rel[], int topK, Boolean ideal)
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
        int rel[] = getRelevanceLabels(rl);


        for(int i=0;i<size;i++)
        {
            for(int j=i+1;j<rl.size();j++)
            {
                // for each RankList we use the ideal as its weight in weighted average, therefore do not divide ideal here
                changes[j][i] = changes[i][j] = (discount(i) - discount(j)) * (gain(rel[i]) - gain(rel[j]));
            }
        }

        return changes;
    }

    protected double gain(int rel)
    {
        return (rewardRate == 1) ? (double)rel : Math.pow(rel, rewardRate);
    }

    // pos: the ranking position in the list, start from 0
    protected double discount(int pos)
    {
        if(pos < discount.length)
            return discount[pos];

        //we need to expand our cache
        int cacheSize = discount.length + 100;
        while(cacheSize <= pos)
            cacheSize += 100;
        double[] tmp = new double[cacheSize];
        System.arraycopy(discount, 0, tmp, 0, discount.length);
        for(int i=discount.length;i<tmp.length;i++)
            discount[i] = discount[i-1] * (1 - (decayRate / k));
        discount = tmp;

        return discount[pos];
    }
}
