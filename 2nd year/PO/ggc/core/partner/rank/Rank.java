package ggc.core.partner.rank;
import ggc.core.partner.Partner;

/** Rank interface */
public interface Rank {
    /**
     * Get price discount quote for the supplied period 
     * according to the supplied deadline
     * @param period time period
     * @param N deadline period
     * @return price discount quote
     */
    public double getDiscount(int period, int N);

    /**
     * Get price penalty quote for the supplied period 
     * according to the supplied deadline
     * @param period time period
     * @param N deadline period
     * @return price penalty quote
     */
    public double getPenalty(int period, int N);

    /**
     * Get points penalty quote for the period elapsed after dealine
     * @param period
     * @return
     */
    public double getPointsPenalty(int period);

    /**
     * Get rank name
     * @return rank name
     */
    public String getRankName();

    /**
     * Check if the rank is valid for the supplied points
     * @param points
     * @return true (valid) / false (not valid)
     */
    public void updateRank(Partner partner, int points);
}
