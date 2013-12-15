package util;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:12
 * To change this template use File | Settings | File Templates.
 */

public class NoOp implements Action {
    private double cost;
    private ArrayList<Agent> agents;
    private double reward;
    private int gameType;

    public NoOp(double cost) {
        this.cost = cost;
        System.out.println("~~~ Noop is set with the minimal price of " + this.cost + " per action");
    }

    @Override
    public int action(Agent a, Edge e, boolean tkch, boolean tkes, boolean real) {
        // counter cost
        a.AddCost(cost);
        a.IncActionsCount();

        if (a.onGoalState() && a.getLocation().getChemicals() > 0) {
            setAgentsRewards(a);
            a.getLocation().setChemicals(a.getLocation().getChemicals() - 1);
        }

        if (real) // prints message if not a state
            System.out.println("~~~ Agent " + a.getSerial() + " did nothing");
        return 1;
    }

    @Override
    public double getCost(Agent a, Edge e, boolean tkch, boolean tkes) {
        return cost;
    }

    public void setAgents(ArrayList<Agent> agents) {
        this.agents = agents;
    }

    @Override
    public void setGameType(int gt) {
        this.gameType = gt;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }


    private void setAgentsRewards(Agent a) {
        if (gameType == 0) return;     // normal game nothing happens
        else {                         // adversarial game
            for (Agent ag : agents) {
                if (gameType == 1) {
                    if (a == ag) {
                        a.setReward(a.getReward() + reward);
                    } else {
                        a.setReward(a.getReward() - reward);
                    }
                } else if (gameType == 2) {
                    if (a == ag) {
                        a.setReward(a.getReward() + reward);
                    }
                } else { // gameType == 3
                    ag.setReward(ag.getReward() + reward);
                }
            }
        }
    }

}
