package util;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by talz on 08/12/13.
 */

public class AdversarialAgent extends Agent {
    private ArrayList<Agent> agents;
    private int gameType;

    // initializer constructor
    public AdversarialAgent(int serialNum, char type, Vertex initial, Vertex goal, ArrayList<Action> actions, ArrayList<Agent> agents, int gameType) {
        super(serialNum, type, initial, goal, actions);
        this.agents = agents;
        this.setGameType(gameType);
    }

    // copy constructor
    public AdversarialAgent(AdversarialAgent other, Graph g) {
        super(other, g);
        this.agents = other.getAgents();
        this.gameType = other.getGameType();
    }

    @Override
    public void decide(Graph g) throws IOException {
        MinimaxState root = new MinimaxState(g, this);
        Minimax.curAgent = getSerial();
        MinimaxState ans = Minimax.alphaBetaPruning(root);
        //MinimaxState ans = Minimax.minimax(root);
        Vertex anl = ans.getAgent().getLocation(), //agent next location
                parallel = g.retDupVertex(anl); // the parallel on this world
        Edge e = g.getEdge(location, parallel);  // getting the edge
        int actionToDo = ans.getAction();        // action as specified in state class
        boolean tkch = false, tkes = false;
        if (actionToDo == -1) {                  // noop
            actions.get(0).action(this, null, false, false, true);
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
    }

    //TODO NEED TO OVERRIDE SCORE FOR GAME 2 OR WRITE NEW SCORE FUNCTION
    @Override
    public String toString() {
        String s = ">>> Adversarial Agent " + serialNum + " : location = " + location.getIndex() + " , reward = " + reward
                + " , cost = " + cost + " , actions made = " + actionsMade;
        return s;
    }

    public double getScore() {
        return reward - cost;
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public void setAgents(ArrayList<Agent> agents) {
        this.agents = agents;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }
}
