package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:09
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.util.ArrayList;

public class Greedy_Agent extends Agent {

	/*
	 * A  greedy agent, that works as follows:
	 * the agent should compute the shortest currently unblocked path to its target,
	 * 			and follow it, carrying chems if possible.
	 * If there is no such path, do no-op.
	 */

    double no_opCost = 0.1;

    public Greedy_Agent (int serialNum, char type,
                         Vertex initial, Vertex goal,
                         ArrayList<Action> actions) {
        super(serialNum, type, initial,goal, actions);
    }

    @Override
    public void decide(Graph G) throws IOException {
        Vertex s = super.getLocation();
        Vertex t = this.goal;
        if (s.getIndex() == t.getIndex() ) {
            do_no_op(s,t);
        }
        else {
            diikstra d = new diikstra(s, t);

            Graph G_tag = d.get_cleanG(G);
            Vertex next1 = d.get_next(s, t, G_tag);
            Vertex next = null;
            if (next1 != null)
                next = G.getVertex(next1.getIndex() -1);

            if (next != null){
                if (this.hasChems()){
                    drive_with_chem(s, next,G);
                }
                else {
                    if (s.hasChems()) {
                        drive_with_s_chem(s, next, G);
                    }
                    else
                        drive_without(s, next);
                } // end else  cases that we not have chem
            } // end if next is not null
            else do_no_op(s,t);

        }
    }


    // not Override!
    private void move(Vertex to, double w) {
        this.location = to;
        this.AddCost(w);
        this.actionsMade ++;
    }


    public void drive_with_chem(Vertex s, Vertex to, Graph G){
        //we have chem and to is not null
        System.out.println(this.getClass().toString() + " now moving from: " +
                s.getIndex() + "to " + to.getIndex() + " with my own chem");
        Vertex t2 = G.getVertex(to.getIndex() -1);
        Edge e = s.getEdgeWith(t2);
        double w = e.getWeight() * 2;
        this.move(t2, w);
    }


    public void drive_with_s_chem(Vertex s, Vertex to, Graph G){
        //we don't have chem but we sure that s have!!!
        setChem(s.hasChems());
        s.decChems();
        System.out.println(" I take some Chem from " + s.getIndex() + "ha ha ha");
        drive_with_chem(s, to , G);
    }


    public void drive_without(Vertex s, Vertex to){
        //we don't have chem
        System.out.println(this.getClass().toString() + " now moving from: " +
                s.getIndex() + "to " + to.getIndex() + " without any chem");
        Edge e = s.getEdgeWith(to);
        double w = e.getWeight();
        this.move(to, w);
    }


    public void do_no_op(Vertex s, Vertex t){
        if (s.getIndex() == t.getIndex() ){
            System.out.println("standing on target");
            actions.get(0).action(this, null, true, true, true);
        }
        else{
            System.out.println(this.getClass().toString() + " can't find a clear way from " +
                    this.location.getIndex() + " to " + this.goal.getIndex());
            System.out.println("therefore doing no-op");
            this.move(this.getLocation(), no_opCost);
        }
    }

    private void setChem(boolean b){
        super.chemicals = b;
    }


}


