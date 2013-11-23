package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:11
 * To change this template use File | Settings | File Templates.
 */

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

public class MinMax {

    State st;
    Agent a;
    boolean max;

    public MinMax(State st_, Agent a_, boolean b){
        this.st = st_;
        this.a = a_;
        this.max = b;
    }


    // return the max choice
    public State choose_max(ArrayList<State> children){
        State ans = children.get(0);
        for (int i=1; i<children.size(); i++){
            if (ans.getValue() < (children.get(i)).getValue() )
                ans = children.get(i);
        }
        return ans;
    }

    // return the min choice
    public State choose_min(ArrayList<State> children){
        State ans = children.get(0);
        for (int i=1; i<children.size(); i++){
            if (ans.getValue() > (children.get(i)).getValue() )
                ans = children.get(i);
        }
        return ans;
    }


}
