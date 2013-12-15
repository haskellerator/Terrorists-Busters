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


public class NormalState implements State { // search tree state
    private Graph g;
    private Agent a;
    private ArrayList<NormalState> ancestors;
    private NormalState father;
    private double value;
    private boolean expandable;
    private int action; // the action that was done in order to reach this state
    // -2 means its the root of the tree
    // -1 = noop, positive is drive with binary flags
    //e.g 2 = 10 in binary means chems = true, escort = false;

    // initial state constructor
    public NormalState(Graph g, Agent a) {
        this.g = g;
        this.a = a;
        this.value = 0;
        this.expandable = true;
        this.setAncestors(new ArrayList<NormalState>());
        setAction(-2);
    }


    // might be unneeded
    public NormalState(Graph g, Agent a, NormalState father, int action) throws IOException { // 0 - noop , 1 - drive
        this.g = Graph.dupGraph(g);
        this.a = Agent.dupAgent(a, this.g);
        this.setAncestors(father.getAncestors());
        this.getAncestors().add(father); // push the father at the end
        this.setAction(action);
        this.expandable = true;
    }

    public NormalState(NormalState father, int action) throws IOException { // 0 - noop , 1 - drive
        this.g = Graph.dupGraph(father.getGraph());
        this.a = Agent.dupAgent(father.getAgent(), this.g);
        this.ancestors = father.getAncestors();
        this.setAction(action);
        this.father = father;
        this.value = -1;
        this.expandable = true;
    }


    // returns the childrens of the action, if array null, creates one;
    public ArrayList<NormalState> expand(ArrayList<NormalState> ans, int mode) throws IOException {
        setExpandable(false);
        Vertex location = a.getLocation();
        Iterator<Edge> it = location.getEdges().iterator();
        if (ans == null)
            ans = new ArrayList<NormalState>();
        while (it.hasNext()) {
            Edge e = it.next();
            NormalState d1s = new NormalState(this, 3), // drive first son;
                    d2s = new NormalState(this, 2),   // etc
                    d3s = new NormalState(this, 1),
                    d4s = new NormalState(this, 0);
            d1s.morph(d1s.getGraph().getDupEdge(e), 1, true, true, mode);
            d2s.morph(d2s.getGraph().getDupEdge(e), 1, true, false, mode);
            d3s.morph(d3s.getGraph().getDupEdge(e), 1, false, true, mode);
            d4s.morph(d4s.getGraph().getDupEdge(e), 1, false, false, mode);

            if (d4s.expandable) {
                d4s.hvalue();
                ans.add(d4s);
            }
            if (d3s.expandable) {
                d3s.hvalue();
                ans.add(d3s);
            }
            if (d2s.expandable) {
                d2s.hvalue();
                ans.add(d2s);
            }
            if (d1s.expandable) {
                d1s.hvalue();
                ans.add(d1s);
            }
        }
        NormalState ns = new NormalState(this, -1);    // noop son
        ns.morph(null, 0, true, true, mode);
        if (ns.expandable) {
            ns.hvalue();
            ans.add(ns);
        }
        return ans;
    }


    private void morph(Edge edge, int action, boolean tc, boolean te, int mode) throws IOException {
        int flag = a.actions.get(action).action(a, edge, tc, te, false);    // does the action

        if (flag == -1 || !isNotVisited(mode)) {
            setExpandable(false);
        }

    }


    public String toString() {
//		return a.getLocation().toString() + '\n' + a.toString() + '\n' + " hv : " + getValue() +" isGoal? " + isGoalState();
        return g.toString() + '\n' + a.toString() + '\n' + " hv : " + getValue() + " isGoal? " + isGoalState() + " expandable? = " + isExpandable();

    }

    /* checks if we reached goal state - no chemicals on graph and no chems on agent*/
    public boolean isGoalState() {
        return g.getTotalChem() == 0 && a.hasChems() == false;
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


    public ArrayList<NormalState> getAncestors() {
        return ancestors;
    }

    public void setAncestors(ArrayList<NormalState> ancestors) {
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

    public NormalState getFirstAnc() {
        if (ancestors.isEmpty()) return null;
        else return ancestors.get(ancestors.size() - 1);
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
    public boolean isNotVisited(int mode) { // 1 - greedy, else - a star or runtime a star
        if (mode == 1) {
            Iterator<NormalState> it = ancestors.iterator();
            while (it.hasNext()) {
                if (it.next().equals(this))
                    return false;
            }
            return true;
        } else {
            NormalState s = this; // the next loops checks if this state is equal to one of its elders
            while (s.getFather() != null) {
                s = s.getFather();
                if (this.equals(s))
                    return false;
            }
            return true;
        }
    }

    public void setFather(NormalState father) {
        this.father = father;
    }

    public NormalState getFather() {
        return father;
    }

    // returns the child of the root
    public NormalState nextActionStep() {
        if (this.father == null) { // this condition is illegal. error occured if this message is printed
            return this;
        } else if (this.father != null && this.father.getFather() == null) { //if you are one of the children of the root

            return this;
        } else {    // else go one level up
            return this.father.nextActionStep();
        }
    }

    public void hvalue() {
        if (this.getValue() == -1) {
            Heuristic he = new Heuristic(this);
            double h = he.get_h_value(this.getAgent().getGoal()),
                    g = he.g();
            this.setValue(h + g/* heurist function here that also takes location here*/);
        }
    }

    // checks whether this state is the best goal choice by checking it is the cheapest of the expandable paths
    public boolean isUltimateGoal(ArrayList<NormalState> tree) {
        if (!this.isGoalState())
            return false;

        for (NormalState s : tree) {
            if (s.isExpandable() && s.getValue() < this.value) return false;
        }

        return true;
    }


}

