package ggc.core.partner.rank;
import java.io.Serializable;
import ggc.core.partner.Partner;

/** Implements Elite class */
public class Elite implements Rank, Serializable {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;
    
    public double getDiscount(int period, int N) {
        if (period >= N){
            return 0.90;
        }

        else if (N > period && period >= 0) {
            return 0.90;
        }

        else if (0 < Math.abs(period) && Math.abs(period) <= N) {
            return 0.95;
        }

        else if (Math.abs(period) > N) {
            return 1.00;
        }

        else {
            return 1.00;
        }
        
    }

    public double getPenalty(int period, int N) {
        return 1 + (0 * period * N);
    }
    
    public String getRankName() {
        return "ELITE";
    }

    public double getPointsPenalty(int period){
        if(period < -15) {
            return 0.25;
        } else {
            return 1.00;
        }
    }

    public void updateRank(Partner partner, int points) {
        if (points <= 25000) {
            partner.setRank(new Elite());
        }
    }
}
