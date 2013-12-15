package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:04
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.util.ArrayList;

public abstract class Agent {
    protected int serialNum;
    protected char type;
    protected boolean chemicals;
    protected boolean escort;
    protected Vertex location;
    protected Vertex goal;
    protected double cost;
    protected int actionsMade;
    protected ArrayList<Action> actions;
    protected boolean isDemi;
    protected boolean nextAction;
    protected Vertex initialLoc; // for the a star algorithms
    protected double reward;

    // copy constructor for the states
    public Agent(Agent other, Graph g) {
        this.serialNum = other.getSerial();
        this.type = other.getType();
        this.location = g.retDupVertex(other.getLocation());
        this.goal = g.retDupVertex(other.getGoal());
        this.cost = other.getCost();
        this.actionsMade = other.getActionsMade();
        this.actions = other.getActions();
        this.isDemi = true;
        this.nextAction = true;
        this.initialLoc = g.retDupVertex(other.getInitialLoc());
        this.reward = other.getReward();
    }

    public Agent(int serN, char t, Vertex initial, Vertex goal, ArrayList<Action> actions) {
        serialNum = serN;
        type = t;
        chemicals = false;
        escort = false;
        location = initial;
        this.goal = goal;
        cost = 0;
        actionsMade = 0;
        this.actions = actions;
        isDemi = false;
        this.nextAction = true;
        this.initialLoc = location;
        this.reward = 0;
        System.out.println("~~~ Agent " + serialNum + " of type -" + t + "- is ready for action in region " + location.getIndex() + " with goal in " + goal.getIndex() + ", sir!");
    }

    public abstract void decide(Graph g) throws IOException;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    //	    Setters and Getters       //
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    @Override
    public String toString() {
        String s = ">>> Agent " + serialNum + " " + type + " : location = " + location.getIndex() + " , carry chems = " + chemicals + " , has escort = " + escort +
                " , cost = " + cost + " , actions made = " + actionsMade;
        return s;
    }

    public Vertex getLocation() {
        return location;
    }

    public boolean hasChems() {
        return chemicals;
    }

    public boolean hasEscort() {
        return escort;
    }

    public double getCost() {
        return cost;
    }

    public void AddCost(double hellCost) {
        this.cost += hellCost;

    }

    public void move(Vertex to) {
        location = to;

    }

    public int getSerial() {
        return serialNum;
    }

    public boolean onGoalState() {
        return location == goal;
    }

    public int getActionsMade() {
        return actionsMade;
    }

    public void IncActionsCount() {
        actionsMade += 1;
    }

    public char getType() {
        return type;
    }

    public Vertex getGoal() {
        return goal;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setEscort(boolean val) {
        escort = val;
    }


    public void pickupChems(boolean val) {
        chemicals = val;
    }


    public static Agent dupAgent(Agent a, Graph g) {
        if (a.getType() == 'b') {
            return dupGBFSAgent(a, g);
        } else if (a.getType() == 'a') {
            if (a instanceof ASAgent)
                return dupASAgent(a, g);
            else
                return dupAdversarialAgent(a, g);
        } else if (a.getType() == 'r') {
            return dupRTASAgent(a, g);
        } else if (a.getType() == 'h') {
            return dupHumanAgent(a, g);
        } else {
            System.out.println("~~~ agent " + a.getType() + " is not supported here");
            return null;
        }
    }

    private static HumanAgent dupHumanAgent(Agent a, Graph g) {
        return new HumanAgent((HumanAgent) a, g);
    }

    private static RTASAgent dupRTASAgent(Agent a, Graph g) {
        return new RTASAgent((RTASAgent) a, g);
    }

    private static ASAgent dupASAgent(Agent a, Graph g) {
        return new ASAgent((ASAgent) a, g);
    }

    private static AdversarialAgent dupAdversarialAgent(Agent a, Graph g) {
        return new AdversarialAgent((AdversarialAgent) a, g);
    }

    private static GBFSAgent dupGBFSAgent(Agent a, Graph g) {
        return new GBFSAgent((GBFSAgent) a, g);
    }


    public boolean isNextAction() {
        return nextAction;
    }

    public void setNextAction(boolean nextAction) {
        this.nextAction = nextAction;
    }


    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        else if (!(obj instanceof Agent)) return false;

        return ((Agent) obj).getLocation().equals(this.location);

    }


    public int getGoal_index() {
        return (this.goal).getIndex();
    }


    public Vertex getInitialLoc() {
        return initialLoc;
    }


    public void setInitialLoc(Vertex initialLoc) {
        this.initialLoc = initialLoc;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }
}
