package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by talz on 08/12/13.
 */

public class MinimaxState implements State {
    private Graph g;
    private Agent a;
    private MinimaxState father;
    private double value;
    private ArrayList<MinimaxState> ancestors;
    private boolean expandable;
    private int action; // the action that was done in order to reach this state
    private ArrayList<Agent> agents;
    private ArrayList<MinimaxState> children;

    // initial state constructor
    public MinimaxState(Graph g, AdversarialAgent a) throws IOException {
        this.g = Graph.dupGraph(g);
        this.a = Agent.dupAgent(a, this.g);
        this.value = -1;
        this.expandable = true;
        this.ancestors = new ArrayList<MinimaxState>();
        setAction(-2);
        this.agents = dupAgents(a.getAgents());
        this.children = null;
    }

    public MinimaxState(MinimaxState father, int action) throws IOException { // 0 - noop , 1 and up - drive
        this.g = Graph.dupGraph(father.getGraph());
        this.a = Agent.dupAgent(father.getAgent(), this.g);
        this.setAncestors(father.getAncestors());
        this.setAction(action);
        this.father = father;
        this.value = -1;
        this.expandable = true;
        this.agents = dupAgents(father.getAgents());
        this.children = null;
        this.a = getOther(this.a);
    }

    //TODO MAKE SURE ITS WORKING
    private ArrayList<Agent> dupAgents(ArrayList<Agent> agents) {
        ArrayList<Agent> dup = new ArrayList<Agent>(2);
        if (a.getSerial() == 1) {
            dup.add(a);
            dup.add(Agent.dupAgent(agents.get(1), g));
        } else {
            dup.add(Agent.dupAgent(agents.get(1), g));
            dup.add(a);
        }
        return dup;
    }

    public Agent getOther(Agent a) {
        if (a == agents.get(0)) {
            return agents.get(1);
        } else {
            return agents.get(0);
        }
    }


    public ArrayList<MinimaxState> getChildren() {
        return children;
    }

    public void SetChildren() throws IOException {
        setExpandable(false);
        Vertex location = getOther(a).getLocation();
        Iterator<Edge> it = location.getEdges().iterator();
        children = new ArrayList<MinimaxState>();
        while (it.hasNext()) {
            Edge e = it.next();
            MinimaxState d3s = new MinimaxState(this, 3), // drive first son;
                    d2s = new MinimaxState(this, 2),   // etc
                    d1s = new MinimaxState(this, 1),
                    d0s = new MinimaxState(this, 0);
            d3s.doAction(d3s.getGraph().getDupEdge(e), 1, true, true);
            d2s.doAction(d2s.getGraph().getDupEdge(e), 1, true, false);
            d1s.doAction(d1s.getGraph().getDupEdge(e), 1, false, true);
            d0s.doAction(d0s.getGraph().getDupEdge(e), 1, false, false);

            if (d0s.expandable) {
                children.add(d0s);
            }
            if (d1s.expandable) {
                children.add(d1s);
            }
            if (d2s.expandable) {
                children.add(d2s);
            }
            if (d3s.expandable) {
                children.add(d3s);
            }
        }
        MinimaxState ns = new MinimaxState(this, -1);    // noop son
        ns.doAction(null, 0, true, true);
        if (ns.expandable) {
            children.add(ns);
        }
    }


    private void doAction(Edge edge, int action, boolean tc, boolean te) throws IOException {
        int flag = a.actions.get(action).action(a, edge, tc, te, false);    // does the action

        if (flag == -1 || !isNotVisited()) {
            setExpandable(false);
        }

    }


    public String toString() {
//		return a.getLocation().toString() + '\n' + a.toString() + '\n' + " hv : " + getValue() +" isGoal? " + isGoalState();
        return g.toString() + '\n' + a.toString() + '\n' + " hv : " + getValue() + " isGoal? " + isGoalState() + " expandable? = " + isExpandable();

    }

    /* checks if we reached goal state - no chemicals on graph and no chems on agent*/
    public boolean isGoalState() {
        return g.getTotalChem() == 0;
    }


    public Graph getGraph() {
        return this.g;
    }

    public Vertex get_A_target() {
        int index = (this.a).getGoal_index(); // not sure that we can get to this field...
        return (this.g).getVertex(index);
    }

    public Vertex get_A_loc() {
        return (this.a).getLocation();
    }


    public ArrayList<MinimaxState> getAncestors() {
        return ancestors;
    }

    public void setAncestors(ArrayList<MinimaxState> ancestors) {
        this.ancestors = ancestors;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof NormalState)) return false;
        boolean ans = g.equals(((NormalState) obj).getGraph()) && a.equals(((NormalState) obj).getAgent());
        return ans;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Agent getAgent() {
        return a;
    }

    // checks whether this state occured in the past
    public boolean isNotVisited() {
        MinimaxState s = this; // the next loops checks if this state is equal to one of its elders
        while (s.getFather() != null) {
            s = s.getFather();
            if (this.equals(s))
                return false;
        }
        return true;
    }

    public MinimaxState getFather() {
        return father;
    }

    // returns the child of the root
    public MinimaxState nextActionStep() {
        if (this.father == null) { // this condition is illegal. error occured if this message is printed
            System.out.println("this is wrong, the root is not supposed to return");
            return this;
        } else if (this.father != null && this.father.getFather() == null) { //if you are one of the children of the root
            return this;
        } else {    // else go one level up
            return this.father.nextActionStep();
        }
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

}
