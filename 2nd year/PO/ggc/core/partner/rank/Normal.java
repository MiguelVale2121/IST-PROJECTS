package ggc.core.partner.rank;
import java.io.Serializable;
import ggc.core.partner.Partner;

/** Implements Normal class */
public class Normal implements Rank, Serializable {
    
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    
    public double getDiscount(int period, int N) {
        if (period >= N){
            return 0.90;
        } else {
            return 1.00;
        }
        
    }
    
    public double getPenalty(int period, int N){
        if (period >= N){
            return 1.00;
        }

        else if (N > period && period >= 0) {
            return 1.00;
        }

        else if (0 < Math.abs(period) && Math.abs(period) <= N) {

            return 1 + Math.abs(period) * 0.05;

        }

        else if (Math.abs(period) > N) {
            return 1 + Math.abs(period) * 0.10;
        }

        else {
            return 1.00;
        }
        
    }
    
    
    public String getRankName() {
        return "NORMAL";
    }


    public double getPointsPenalty(int period){
        return 0*period;
    }


    public void updateRank(Partner partner, int points) {
        if (points > 2000) {
            partner.setRank(new Elite());
        }
    }
}
