package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:14
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class World {
    private Graph worldmap;
    private ArrayList<Agent> agents;
    private ArrayList<Action> actions;
    private int horizon;          // valid only for the Adversarial games
    private int gameType;         // valid only for the Adversarial games
    private int stepsFromStart;
    public static double reward;

    public World(Graph g, ArrayList<Agent> ag, ArrayList<Action> ac, int horizon, int gameType) {
        worldmap = g;
        agents = ag;
        actions = ac;
        ((Drive) ac.get(1)).setWorld(this);      // sets the pointer for the end game function - in case hell breaks loose
        this.horizon = horizon;
        this.gameType = gameType;
        stepsFromStart = 0;

        // this condition is added to updated the agents scores in case of adversarial game
        // so that the rewards will be applied correctly
        if (this.gameType > 0) {
            for (Action a : actions) {
                a.setAgents(agents);
                a.setReward(this.reward);
                a.setGameType(this.gameType);
            }
        }
    }

    public void startSimulation(int mode) throws IOException {
        System.out.println("~~~ Simulation Starting");
        if (mode == 1) {
            normalGame();
        } else {
            adversarialGame();
        }

    }

    public void normalGame() throws IOException {
        Iterator<Agent> ait;
        boolean chemicalsExists = true;
        while (chemicalsExists) {
            ait = agents.iterator();
            while (ait.hasNext() && chemicalsExists) {
                printMidState();
                ait.next().decide(worldmap);
                chemicalsExists = chemicalsExists();
            }
        }

        ait = agents.iterator();
        while(ait.hasNext()){
            Agent ag = ait.next();
            if(ag.getType() == 'h'){
                ((HumanAgent) ag).closeStream();
            }
        }
        printResults();
    }

    private void adversarialGame() throws IOException {
        Iterator<Agent> ait;
        boolean chemicalsExists = true;
        while (chemicalsExists && horizon >= stepsFromStart) {
            ait = agents.iterator();
            while (ait.hasNext() && chemicalsExists) {
                printMidState();
                ait.next().decide(worldmap);
                if (Minimax.currentNumOfChems > worldmap.getTotalChem())
                    Minimax.currentNumOfChems = worldmap.getTotalChem();
                chemicalsExists = chemicalsExists();
            }
            stepsFromStart++;
        }

        ait = agents.iterator();
        while (ait.hasNext()) {
            Agent ag = ait.next();
            if (ag.getType() == 'h') {
                ((HumanAgent) ag).closeStream();
            }
        }
        printResults();    // TODO MAYBE CREATE A VERSION FOR ADVERSARIAL
    }

    private boolean chemicalsExists() {
        return !(getTotalChem() == 0);
    }

    private int getTotalChem() {
        return worldmap.getTotalChem();
    }

    public void printMidState(){
        System.out.println(worldmap.toString());
        Iterator<Agent> it = agents.iterator();
        while(it.hasNext())
            System.out.println(it.next().toString());
        System.out.println("\n--------------------------\n");

    }


    // two cases, for normal game and for adversarial game
    public void printResults() {
        System.out.println("~~~ All the chems have been disposed of. Order has been restored, goodbye!");
        System.out.println("~~~ The total cost is : " + totalScore());
        Iterator<Agent> it = agents.iterator();
        if (gameType > 0) {
            while (it.hasNext()) {
                Agent a = it.next();
                if (a.getType() == 'h') {
                    System.out.println("~~~ Human agent " + a.getSerial() + " { Rewards : " + a.getReward() + " , Cost : " + a.getCost() +
                            " , Score : " + ((HumanAgent) a).getScore() + " , Actions : " + a.getActionsMade());
                } else {
                    System.out.println("~~~ Adversarial agent " + a.getSerial() + " { Rewards : " + a.getReward() + " , Cost : " + a.getCost() +
                            " , Score : " + ((AdversarialAgent) a).getScore() + " , Actions : " + a.getActionsMade());
                }
            }
        } else {
            while (it.hasNext()) {
                Agent a = it.next();
                if (a.getType() == 'b') {
                    System.out.println("~~~ Agent " + a.getSerial() + " { Score : " + a.getCost() + " , Actions : " + a.getActionsMade() + " , Preformance : "
                            + ((GBFSAgent) a).getPreformance() + " , Expansions : " + ((GBFSAgent) a).getSteps() + " }");
                } else if (a.getType() == 'a') {
                    System.out.println("~~~ Agent " + a.getSerial() + " { Score : " + a.getCost() + " , Actions : " + a.getActionsMade() + " , Preformance : "
                            + ((ASAgent) a).getPreformance() + " , Expansions : " + ((ASAgent) a).getSteps() + " }");
                } else if (a.getType() == 'r') {
                    System.out.println("~~~ Agent " + a.getSerial() + " { Score : " + a.getCost() + " , Actions : " + a.getActionsMade() + " , Preformance : "
                            + ((RTASAgent) a).getPreformance() + " , Expansions : " + ((RTASAgent) a).getTS() + " }");
                } else
                    System.out.println("~~~ Agent " + a.getSerial() + " { Score : " + a.getCost() + " , Actions : " + a.getActionsMade() + " }");
            }
        }
    }

    private double totalScore(){
        double total = 0;
        Iterator<Agent> it = agents.iterator();
        while(it.hasNext())
            total += it.next().getCost();
        return total;
    }


}



