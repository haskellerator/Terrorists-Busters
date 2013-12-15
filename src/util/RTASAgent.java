package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:12
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class RTASAgent extends Agent {    // a star agent

    private ArrayList<NormalState> expansionQueue;
    private int expansionSteps; // T
    private int weightFactor; // F
    private int totalSteps;
    private int maximumExpansions;


    //copy constructor
    public RTASAgent(RTASAgent other, Graph g) {
        super(other, g);
        expansionQueue = null;
        expansionSteps = other.getES();
        weightFactor = other.getWF();
        totalSteps = other.getTS();
    }

    public RTASAgent(int serN, char t, Vertex initial, Vertex goal,
                     ArrayList<Action> actions, int wf, int me) {
        super(serN, t, initial, goal, actions);
        expansionQueue = null;
        expansionSteps = 0;
        weightFactor = wf;
        totalSteps = 0;
        System.out.println(me);
        maximumExpansions = me;
    }


    @Override
    public void decide(Graph g) throws IOException {
        expansionSteps = 0;
        expansionQueue = new ArrayList<NormalState>();

        NormalState ans = aStar(g);
        Vertex anl = ans.getAgent().getLocation(), //agent next location
                parallel = g.retDupVertex(anl); // the parallel on this world
        Edge e = g.getEdge(location, parallel);  // getting the edge
        int actionToDo = ans.getAction();        // action as specified in state class
        boolean tkch = false, tkes = false;
        if (actionToDo == -1) {                  // noop
            actions.get(0).action(this, null, false, false, true);
            totalSteps += expansionSteps;
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
        System.out.println(totalSteps);
        System.out.println(expansionSteps);
    }

    /* this will be the algorithm for the greedy heuristic best first search */
    private NormalState aStar(Graph g) throws IOException {
        NormalState s = new NormalState(g, this), s2; // initial
        expansionQueue.add(s);
        //	setValues(expansionQueue);
        s.hvalue();
//		System.out.println("~~~ is starting now");

//		System.out.println(s);
//		System.out.println(!s.isGoalState());
//		System.out.println(!s.isUltimateGoal(expansionQueue));
        while (/*!s.isGoalState() &&*/ !s.isUltimateGoal(expansionQueue) && expansionSteps <= maximumExpansions) {
            expansionQueue = s.expand(expansionQueue, 2); // expands a child
            expansionSteps += 1;
            setValues(expansionQueue);        // set values to those that are not evaluated yet
            s2 = chooseChild(expansionQueue);    // chooses the child with the minimal evalutation value
//			System.out.println(s2);
            s = s2;
//			System.out.println(!s.isGoalState());
//			System.out.println(!s.isUltimateGoal(expansionQueue));

        }
        return s.nextActionStep();
    }

//	// prints the decision made in the end
//	private void printDecision(ArrayList<MinimaxState> sarr) {
//		Iterator<MinimaxState> it = sarr.iterator();
//		while(it.hasNext()){
//			MinimaxState s = it.next();
//			System.out.println(" -------------------------" );
//			System.out.println(s.getAgent().getLocation().toString() + '\n' + " hv : " + s.getValue() +" isGoal? " + s.isGoalState());
//			System.out.println(" -------------------------" );
//		}
//	}

    // picks the best child
/*	private MinimaxState chooseChild(ArrayList<MinimaxState> children) {
        Iterator<MinimaxState> it = children.iterator();
		boolean firstExpandable = false, goalNotFound = true;
		MinimaxState ret = it.next(),other;
		while(it.hasNext() && !firstExpandable && goalNotFound){
			 ret = it.next();
			 if(ret.isExpandable())
				 firstExpandable = true;
			 if(ret.isGoalState()){
				 goalNotFound = false;
			 }
		}

		while(it.hasNext() && goalNotFound){
			other = it.next();
			if(other.getValue() < ret.getValue() && other.isExpandable() || other.isGoalState()){
				ret = other;
			}
			if(ret.isGoalState())
				goalNotFound = false;
		}

		return ret;
	}*/

    // picks the best child
    private NormalState chooseChild(ArrayList<NormalState> children) {
        Iterator<NormalState> it = children.iterator();
        boolean firstExpandable = false, goalNotFound = true;
        NormalState ret = it.next(), other;
        while (it.hasNext() && !firstExpandable/* && goalNotFound*/) {
            ret = it.next();
            if (ret.isExpandable())
                firstExpandable = true;
            //	 if(ret.isGoalState()){
            //		 goalNotFound = false;
            //	 }
        }

        while (it.hasNext() /*&& goalNotFound*/) {
            other = it.next();
            if (other.getValue() < ret.getValue() && other.isExpandable()/* || other.isGoalState()*/) {
                ret = other;
            }
            //	if(ret.isUltimateGoal(children))
            //		goalNotFound = false;
        }

        return ret;
    }


    /* this function sets the values of all the children according to the heuristic*/
    private void setValues(ArrayList<NormalState> children) {
        Iterator<NormalState> it = children.iterator();
        while (it.hasNext()) {

            NormalState s = it.next();
            if (s.getValue() == -1) {
                Heuristic he = new Heuristic(s);
                double h = he.get_h_value(s.getAgent().getGoal()),
                        g = he.g();
                s.setValue(h + g/* heurist function here that also takes location here*/);

            }
        }
    }

    public int getSteps() {
        return totalSteps;
    }

    @Override
    public String toString() {
        String s = ">>> Agent " + serialNum + " " + super.type + " : location = " + super.location.getIndex() + " , carry chems = " + super.chemicals + " , has escort = " + super.escort +
                " , cost = " + super.cost + " , actions made = " + super.actionsMade + ", expansions this turn = " + expansionSteps + ", total expansions = " + totalSteps;
        return s;
    }

    public double getPreformance() {
//		System.out.println("");
//		System.out.println(getCost());
//		System.out.println(weightFactor);
//		System.out.println(totalSteps);
        return ((getCost() * weightFactor) + totalSteps);
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

    public int getMaximumExpansions() {
        return maximumExpansions;
    }

//	public void setMaximumExpansions(int maximumExpansions) {
//		this.maximumExpansions = maximumExpansions;
//	}
}
