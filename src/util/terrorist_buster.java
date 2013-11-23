package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:13
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.util.ArrayList;


public class terrorist_buster extends Agent {
	/*
	 * =====================================================================
	 * if at a node with military unit, find the terrorist-blocked edge that can be reached
	 * 		with least cost, and move towards it (with escort) in the least-cost path.
	 * ======================================================================================
	 * Otherwise, first get to a vertex with military units.
	 *	====================================================
	 */

    int kills;
    int moves;

    public terrorist_buster (int serialNum, char type,
                             Vertex initial, Vertex goal,
                             ArrayList<Action> actions) {
        super(serialNum, type, initial,goal, actions);
        this.kills = 0;
        this.moves = 0;
    }


    @Override
    public void decide(Graph G) throws IOException {
        Vertex s = this.getLocation();
        Vertex t = this.goal;

        boolean b = s.getIndex() != t.getIndex();

        if (b){
            if (super.hasEscort() )
                this.haveArmy(s, t, G);
            else
            if (s.hasMUnits())
                this.useSarmy(s, t, G);
            else
            if (findArmy(s, G) != null){
                Vertex next = G.getVertex( findArmy(s, G).getIndex() -1 );

                Edge e = s.getEdgeWith(next);
                double weight = e.getWeight();
                this.move(next, weight);
            }
            else{ // do no-op
                this.move(this.location, 0.0);
            }
        }
        else {
            // s is t
            // going to find some terrorist!!
            diikstra d = new diikstra(s);
            Vertex t_tag = d.get_closest_terorist(s, G);
            if (t_tag != null){
                // set the new target
                this.goal = t_tag;
                // going to kill them!!
                this.decide(G);
            }
            else {
                System.out.println(" there are no terrorist to kill!!");
                System.out.println("Agent: " + this.getClass().toString() + " number: " + super.serialNum +
                        " is no longer exist");
                actions.get(0).action(this, null, false, false, true);
            }

        }

    }// end of function



    // the move..
    // not Override!
    public void move(Vertex to, double score){
        this.moves ++;
        this.location = to;
        this.AddCost(score);
        this.actionsMade ++;
    }


    // if the agent have army unit:
    public void haveArmy(Vertex s, Vertex t, Graph G){
        diikstra d = new diikstra(s,t);
        ArrayList<diikstra_ver> d_list = d.get_all_d(s, G);
        Vertex next1 = d.get_next_with_army(d_list, s, t, G);
        Vertex next = G.getVertex(next1.getIndex());
        Edge e = s.getEdgeWith(next);
        if (e.isBlocked())
            this.kills ++;
        double weight = e.getWeight() * 2;
        System.out.println(this.getClass().toString() + " now moving from: " + this.location.toString() +
                " to: " + next.toString() + " ready to kill some terrorist" );
        this.move(next,weight);
    }


    public Vertex findArmy(Vertex s, Graph G){
        diikstra d = new diikstra(s);
        Vertex next = d.get_closest_army(s, G);
        if (next != null){
            // the move
            System.out.println("Agent " + super.getType() + " now moving from: " +
                    (this.location).getIndex() + " to: " + next.getIndex() + " (still without army)" );
            return next;
        }
        else return null;
    }

    public void useSarmy(Vertex s, Vertex t, Graph G){
        this.escort = true;
        s.decMUnits();
        haveArmy(s, t, G);
    }


}
