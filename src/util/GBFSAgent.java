package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:08
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class GBFSAgent extends Agent { //  greedy BFS agent
    private int expansionSteps; // T
    private int weightFactor; // F
    private int totalSteps;


    //copy constuctor
    public GBFSAgent(GBFSAgent other, Graph g) {
        super(other, g);
        expansionSteps = other.getES();
        weightFactor = other.getWF();
        totalSteps = other.getTS();
    }

    public GBFSAgent(int serN, char t, Vertex initial, Vertex goal,
                     ArrayList<Action> actions, int wf) {
        super(serN, t, initial, goal, actions);
        expansionSteps = 0;
        weightFactor = wf;
        totalSteps = 0;
    }

    @Override
    public void decide(Graph g) throws IOException {
        NormalState ans = greedyBFS(g);
        Vertex anl = ans.getAgent().getLocation(), //agent next location
                parallel = g.retDupVertex(anl); // the parallel on this world
        Edge e = g.getEdge(location, parallel);  // getting the edge

        int actionToDo = ans.getAction();        // action as specified in state class
        boolean tkch = false, tkes = false;
        if (actionToDo == -1) {                  // noop
            actions.get(0).action(this, null, false, false, true);
            totalSteps += expansionSteps;
            expansionSteps = 0;
            return;
        }
        if (actionToDo == 2 || actionToDo == 3) {
            tkch = true;
        }

        if (actionToDo == 1 || actionToDo == 3) { //
            tkes = true;
        }
        // drive with flags
        actions.get(1).action(this, e, tkch, tkes, true);
        totalSteps += expansionSteps;
        expansionSteps = 0;

    }

    /* this will be the algorithm for the greedy heuristic best first search */
    private NormalState greedyBFS(Graph g) throws IOException {
        NormalState s = new NormalState(g, this), s2; // initial
        while (s.getValue() != 0 || (!s.isGoalState())) {
            ArrayList<NormalState> children = s.expand(null, 1); // return legal children children

            if (children.size() == 0) {
                System.out.println("~~~ Agent is in a loop, all possible children were generated earlier");
                printDecision(s.getAncestors());
                System.exit(0);
            }

            expansionSteps += 1;
            setValues(children);        // set values to children
            s2 = chooseChild(children);    // chooses the child with the mininmal heuristic value
            s2.getAncestors().add(s);
            s = s2;
        }
        s.getAncestors().add(s);                                    // adds the leaf state to the ancestors
        printDecision(s.getAncestors());
        return s.getAncestors().get(1);
    }


    // prints the decision made in the end
    private void printDecision(ArrayList<NormalState> sarr) {
        Iterator<NormalState> it = sarr.iterator();
        while (it.hasNext()) {
            NormalState s = it.next();
            System.out.println(" -------------------------");
            System.out.println(s.getAgent().getLocation().toString() + '\n' + " hv : " + s.getValue() + " isGoal? " + s.isGoalState());
            System.out.println(" -------------------------");
        }
    }

    // picks the best child
    private NormalState chooseChild(ArrayList<NormalState> children) {
        Iterator<NormalState> it = children.iterator();
        NormalState ret = it.next(), other;
        while (it.hasNext()) {
            other = it.next();
            if (other.getValue() < ret.getValue()) {
                ret = other;
            }
        }
        return ret;
    }

    /* this function sets the values of all the children according to the heuristic*/
    private void setValues(ArrayList<NormalState> children) {
        Iterator<NormalState> it = children.iterator();
        while (it.hasNext()) {
            NormalState s = it.next();
            Heuristic h = new Heuristic(s);
            s.setValue(h.get_h_value(s.getAgent().getGoal())/* heurist function here */);
        }
    }

    @Override
    public String toString() {
        String s = ">>> Agent " + serialNum + " " + super.type + " : location = " + super.location.getIndex() + " , carry chems = " + super.chemicals + " , has escort = " + super.escort +
                " , cost = " + super.cost + " , actions made = " + super.actionsMade + ", expansions this turn = " + expansionSteps + ", total expansions = " + totalSteps;
        return s;
    }

    public double getPreformance() {
        return ((getCost() * weightFactor) + totalSteps);
    }

    public int getSteps() {
        return totalSteps;
    }

    public int getTS() {
        return totalSteps;
    }

    public int getWF() {
        return weightFactor;
    }

    public int getES() {
        return expansionSteps;
    }

}

