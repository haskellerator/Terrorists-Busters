package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:12
 * To change this template use File | Settings | File Templates.
 */

public class NoOp implements Action{
    private double cost;

    public NoOp(double cost){
        this.cost = cost;
        System.out.println("~~~ Noop is set with the minimal price of " + this.cost + " per action");
    }

    @Override
    public int action(Agent a, Edge e, boolean tkch, boolean tkes, boolean real) {
        // counter cost
        a.AddCost(cost);
        a.IncActionsCount();

        if(a.onGoalState() && a.getLocation().getChemicals() > 0)
            a.getLocation().setChemicals(a.getLocation().getChemicals()-1);

        if(real) // prints message if not a state
            System.out.println("~~~ Agent " + a.getSerial() + " did nothing");
        return 1;
    }

    @Override
    public double getCost(Agent a, Edge e, boolean tkch, boolean tkes) {
        return cost;
    }

}
