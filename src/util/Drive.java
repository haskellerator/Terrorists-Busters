package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:07
 * To change this template use File | Settings | File Templates.
 */

public class Drive implements Action{
    private double hellCost;
    private World world;

    public Drive(double cost){
        hellCost = cost;
        System.out.println("~~~ Drive is set with a huge price of " + this.hellCost + " in case hell breaks loose");
    }



    @Override
    public int action(Agent a, Edge e, boolean tkch, boolean tkes , boolean real) {
        Vertex location = a.getLocation(), dest =e.otherSide(location);

        String msg = "~~~ Agent is now in region " + location.getIndex();
        // takes chemicals according to the value of tkch
        if (tkch && location.getChemicals() > 0){
            a.pickupChems(true);
            location.setChemicals(location.getChemicals()-1);
            msg += "\n~~~ Agent " + a.getSerial() + " picked chemicals";
        }

        // takes escort according to the value of tkes
        if (tkes && location.getMUnits() > 0){
            a.setEscort(true);
            location.setMUnits(location.getMUnits()-1);
            msg += "\n~~~ Agent " + a.getSerial() + " takes escort with him";
        }

        // in case the worst happens
        if( !validForPass(a, e, a.hasChems(),a.hasEscort())){
            a.AddCost(getCost(a,e, a.hasChems(),a.hasEscort()));
            a.IncActionsCount();
            if(real){
                EntryPoint.badEnd(world);
                return -1;
            }else
                return -1;
        }

//		System.out.println(a.hasChems());

        // if the conditions feat, the edge is freed
        if(e.isBlocked() && a.hasEscort() && !a.hasChems()){
            e.switchBlocked();
            msg += "\n~~~ The terrorists on the edge " + e.toString() + " decided to quit the way of terror";
        }

        // the actual move and its effects
        msg += "\n~~~ Agent moves from " + location.getIndex() + " to " + dest.getIndex();
        a.move(dest);
        a.AddCost(getCost(a, e,a.hasChems(),a.hasEscort()));

        if(real){ 	// this condition for the real moves
            a.setInitialLoc(a.getLocation());
        }
        location = a.getLocation();
        if (tkch && a.hasChems()){
            a.pickupChems(false); // agent drops chemicals
            location.setChemicals(location.getChemicals()+1);
            msg += "\n~~~ Agent drops chems";
            if(a.onGoalState())
                location.setChemicals(location.getChemicals()-1);
        }
        if (tkes && a.hasEscort()){
            a.setEscort(false); // agent drop chemicals
            location.setMUnits(location.getMUnits()+1);
            msg += "\n~~~ Agent leaves escort";

        }
        // counters
        a.IncActionsCount(); // TODO maybe move outside the action
        a.setEscort(false);
        a.pickupChems(false);

        if(real) // if its in state then the message is not printed
            System.out.println(msg);
        return 1;
    }

    static boolean validForPass(Agent a, Edge e, boolean tkch, boolean tkes ){
        return (
                (!e.isBlocked())
                        || (e.isBlocked() && !tkch)
                        || (e.isBlocked() && tkch && tkes)
        );
    }

    @Override
    public double getCost(Agent a, Edge e, boolean tkch, boolean tkes ) {
        double cost = e.getWeight();
        if( tkch ) cost = cost*2;
        if( tkes ) cost = cost*2;
        if(!validForPass(a,e, tkch, tkes )) return hellCost;
        return cost;
    }

    public void setWorld(World world) {
        this.world = world;

    }

}
