package ggc.core.partner.rank;
import java.io.Serializable;
import ggc.core.partner.Partner;

/** Implements Selection class */
public class Selection implements Rank, Serializable {
    
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    public double getDiscount(int period, int N) {
        
        if (period >= N) {
            return 0.90;
        }

        else if (N > period && period >= 0) {
            if (period >= 2) {
                return 0.95;
            }
            else {
                return 1.00;
            }
        }

        else if (0 < Math.abs(period) && Math.abs(period) <= N) {
            return 1.00;

        }

        else if(Math.abs(period) > N) {
            return 1.00;
        }

        else {
            return 1.00;
        }
    }

    public double getPenalty(int period, int N) {

        if (period>=N){
            return 1.00;
        }

        else if (N > period && period >= 0) {
            return 1.00;
        }

        else if (0 < Math.abs(period) && Math.abs(period) <= N) {
            if (period >= -1) {
                return 1.00;
            } 

            else {
                return 1 + Math.abs(period) * 0.02;
            }

        }

        else if(Math.abs(period) > N) {
            return 1 + Math.abs(period) * 0.05;
        }

        else {
            return 1.00;
        }
    }

    public String getRankName(){
        return "SELECTION";
    }

    
    public double getPointsPenalty(int period){
        if(period < -2) {
            return 0.10;
        } else {
            return 1.00;
        }
    }


    public void updateRank(Partner partner, int points) {
        if (25000 < points) {
            partner.setRank(new Elite());
        } else if (points <= 2000) {
            partner.setRank(new Normal());
        }
    }
}
