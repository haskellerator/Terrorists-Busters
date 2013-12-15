package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:11
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.util.ArrayList;


// in this class we will implement all the min-max algorithms

/*
 * the value of a node or a state is
 * the value of the best son state + the path cost from son to this
 * for ex: if we choose to go from v1 to v2 then the val of v1
 * will be val(v2) +  weight of e(v1,v2)
 * 				mul by 2 or 4 if carrying chem or army..
 *
 * so what is a terminal??
 *  it will be state such that its goal state and
 *  there are no chem in the game... (and maybe more)
 */

public class Minimax {
    public static int gameType;
    public static int maxDepth; // the number of steps
    public static int curAgent;
    private static int otherAgent;
    public static int totalNumOfChems;
    public static int currentNumOfChems;

    public static MinimaxState alphaBetaPruning(MinimaxState root/*,int depth*/) throws IOException {
        //s.SetChildren();                                      // creates the children of the state
        //ArrayList<MinimaxState> children = s.getChildren();   // keeps them in the array
        //Iterator<MinimaxState> it = children.iterator();
        //double v = NonGoalStateEval(s);
        //while (it.hasNext() ){
        //    MinimaxState st = it.next();
        //    v = maxValue(st, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, maxDepth - 1);
        //    st.setValue(v);
        //}
        setOtherAgent();
        /*double v =*/
        maxValue(root, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, maxDepth);     // run the algo
        //MinimaxState ans = findNextMove(root);


        return findNextMove(root); // return the best son
    }

    private static void setOtherAgent() {
        if (curAgent == 1)
            otherAgent = 2;
        else
            otherAgent = 1;
    }

    public static double maxValue(MinimaxState st, Double a, Double b, int depth) throws IOException {
        if (st.isGoalState() || depth == 0) {                // recursive stop condition
            return evalFunction(st);
        } else {
            double val = Double.NEGATIVE_INFINITY, newVal;   // values setup
            st.SetChildren();                                // children creation
            ArrayList<MinimaxState> children = st.getChildren();
            for (MinimaxState child : children) {              // iteration on the children

                if (gameType == 3) newVal = maxValue(child, a, b, depth - 1);
                else newVal = minValue(child, a, b, depth - 1); // recursive call on the children
                child.setValue(newVal);                      // save the value in the child state
                if (val < newVal) {                           // check condition
                    val = newVal;                            // value update
                    st.setValue(val);                        // change value in cur state
                }
                if (val >= b)                                 // check if can prune
                    return val;
                a = Math.max(a, val);                         // update alpha
            }
            return val;
        }
    }


    public static double minValue(MinimaxState st, Double a, Double b, int depth) throws IOException {
        if (st.isGoalState() || depth == 0)                 // recursive stop condition
            return evalFunction(st);
        else {
            double val = Double.POSITIVE_INFINITY, newVal;   // values setup
            st.SetChildren();                                // children creation
            ArrayList<MinimaxState> children = st.getChildren();
            for (MinimaxState child : children) {              // iteration on the children
                newVal = maxValue(child, a, b, depth - 1); // recursive call on the children
                child.setValue(newVal);                      // save the value in the child state
                if (val > newVal) {                           // check condition
                    val = newVal;                            // value update
                    st.setValue(val);                        // change value in cur state
                }
                if (val <= a)                                 // check if can prune
                    return val;
                b = Math.min(b, val);                        // update beta
            }
            return val;
        }
    }

    private static MinimaxState findNextMove(MinimaxState root) {
        ArrayList<MinimaxState> children = root.getChildren();
        for (MinimaxState child : children)
            if (child.getValue() == root.getValue())
                return child;
        return null;
    }

    private static double evalFunction(MinimaxState s) {
        if (s.isGoalState()) {
            return totalNumOfChems * World.reward;
        } else if (gameType == 1) {
            return zeroSumEval(s);
        } else if (gameType == 2) {
            return nonZeroSumEval(s);
        } else {
            return cooperativeEval(s);
        }
    }

    private static double zeroSumEval(MinimaxState s) {
        Heuristic heuristic = new Heuristic(s);

        /* chemicals distance from goal * 2*/
        double arg1 = heuristic.get_h_value(s.getAgents().get(curAgent - 1).getGoal());

        /*current agent's distance from any chem*/
        double arg2 = heuristic.get_h_value(s.getAgents().get(curAgent - 1).getLocation());

        /* other agent's distance from any chem*/
        double arg3 = heuristic.get_h_value(s.getAgents().get(otherAgent - 1).getLocation());

        /* i divide by two because the values of arg2 and arg3 are supposed to be without the 2 multiplication */
        return ((currentNumOfChems - s.getGraph().getTotalChem()) * World.reward) - (arg1 + ((arg2 - arg3) / 2.0));
    }

    private static double nonZeroSumEval(MinimaxState s) {
        Heuristic heuristic = new Heuristic(s);

        /* chemicals distance from goal * 2*/
        double arg1 = heuristic.get_h_value(s.getAgents().get(curAgent - 1).getGoal());

        /*current agent's distance from any chem*/
        double arg2 = heuristic.get_h_value(s.getAgents().get(curAgent - 1).getLocation());

        /* other agent's distance from any chem*/
        double arg3 = heuristic.get_h_value(s.getAgents().get(otherAgent - 1).getLocation());

        /* i divide by two because the values of arg2 and arg3 are supposed to be without the 2 multiplication */
        return ((currentNumOfChems - s.getGraph().getTotalChem()) * World.reward) - (arg1 + ((arg2 + arg3) / 2.0));
    }


    private static double cooperativeEval(MinimaxState s) {
        Heuristic heuristic = new Heuristic(s);
        return ((currentNumOfChems - s.getGraph().getTotalChem()) * World.reward) - heuristic.get_h_value(s.getAgent().getGoal());
    }



    /*another purposed heuristic, total score minus the penalty for reaching this reaching this state*/


/*    public MinimaxState AlphaBetaPruning(MinimaxState s, int depth) throws IOException{
        s.SetChildren();
        ArrayList<MinimaxState> children = s.getChildren();
        Iterator<MinimaxState> it = children.iterator();
        double v = NonGoalStateEval(s);
        while (it.hasNext() ){
            MinimaxState st = it.next();
            v = maxValue(st, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth-1);
            st.setValue(v);
        }
        MinimaxState ans = findNextMove(children,v);
        //TODO - update the parent of and to be s

        return ans;
    }

    public double maxValue(MinimaxState st, Double a, Double b, int depth) throws IOException{
        if ( st.isGoalState() ){
            return evalFunction(st);
        }else{
            double v = Double.NEGATIVE_INFINITY;
            if (depth == 0){
                //TODO if we have to stop the expands...
                // cost will be the reward until now minus the cost to get here (reward - cost)
            }
            else{// expand..
                st.SetChildren();
                ArrayList<MinimaxState> list = st.getChildren();
                for(int i=0; i<list.size(); i++){
                    v = Math.max(v, minValue(st, a,b, depth-1) );
                    if (v >= b)
                        return v;
                    a = Math.max(a, v);
                }
                return v;
            }
            return v;
        }
    }


    public double minValue(MinimaxState st, Double a, Double b, int depth) throws IOException{
        if ( st.isGoalState() )
            return evalFunction(st);
        else{
            double v = Double.POSITIVE_INFINITY;
            if (depth ==0){
                //TODO
                // cost will be the reward until now minus the cost to get here (reward - cost)
            }
            else{
                st.SetChildren();
                ArrayList<MinimaxState> list = st.getChildren();
                for(int i=0; i<list.size(); i++){
                    v = Math.min ( v, (maxValue(st, a, b, depth-1) ) );
                    if (v <= a)
                        return v;
                    b = Math.min(b, v);
                }
                return v;
            }
            return v;
        }
    }

    private MinimaxState findNextMove(ArrayList<MinimaxState> list, double v) {
        Iterator<MinimaxState> it = list.iterator();
        MinimaxState st;
        while (it.hasNext()){
            st = it.next();
            if (st.getValue() == v)
                return st;
        }

        return null;
    }


    private static double NonGoalStateEval(MinimaxState s) {
        return 0;
    }

    private static double evalFunction(MinimaxState s) {
        return 0;
    }


    //TODO A FUNCTION THAT RETURNS THE CHOSEN SON OF THE ROOT
    /*NormalState st;
    Agent a;
    boolean max;

    public Minimax(NormalState st_, Agent a_, boolean b){
        this.st = st_;
        this.a = a_;
        this.max = b;
    }
*/


}
