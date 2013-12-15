package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:08
 * To change this template use File | Settings | File Templates.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class EntryPoint {

    public static void main(String[] args) throws IOException {
        System.out.println("~~~ Welcome to the simulator ");
        if(args.length < 1){
            System.out.println("~~~ no graph file given ");
            return;
        }
        InputParser.fileName = args[0];
        System.out.println("~~~ Parsing file: "+ args[0] +", and Creating a Graph ");
        // creating graph
        Graph g = InputParser.inputFileParser();
        Minimax.totalNumOfChems = g.getTotalChem();
        Minimax.currentNumOfChems = Minimax.totalNumOfChems;
        InputParser.br = new BufferedReader(new InputStreamReader(System.in));

        ArrayList<Action> actions = InputParser.actionsSetup();			// creating actions array

        System.out.println("~~~ Finished parsing\n~~~ Please choose your game:\n~~~ 1 - Normal, 2 - Adversarial");
        ArrayList<Agent> agents;
        int mode = InputParser.inputNum(3), horizon = 0, gameType = 0;

        double reward = 0;

        if (mode == 1) {
            agents = InputParser.normalGameAgentsSetup(g, actions);    // creating the agents
        } else {   // mode == 2
            System.out.println("~~~ Please choose the reward for destroying the chemicals:");
            World.reward = InputParser.costParser();
            System.out.println("~~~ Please choose the number of steps for the game:");
            horizon = InputParser.inputNum(Integer.MAX_VALUE);
            System.out.println("~~~ Please choose your game type:\n~~~ 1 - zero-sum, 2 - non zero-sum (semi cooperative), 3 - fully cooperative");
            gameType = InputParser.inputNum(4);
            Minimax.gameType = gameType;
            System.out.println("~~~ Please choose the depth of the search:");
            Minimax.maxDepth = InputParser.inputNum(Integer.MAX_VALUE);
            agents = InputParser.adversarialGameAgentsSetup(g, actions, gameType); //TODO REMOVE GAME TYPE FROM THE AGENTS
        }
        /* if any is null, it means some parameter is incorrect and an ending is forced*/
        if (g == null || actions == null || agents == null || (mode == 2 && (horizon <= 0 || gameType <= 0 || gameType > 3))) {
            System.out.println("~~~ Simulation ended prematurely, goodbye bye ");
            return;
        }
        World wld = new World(g, agents, actions, horizon, gameType);                        // creating a world in which everything occurs
        wld.startSimulation(mode);                                            // starting...
        System.out.println("~~~ Simulation ended");

    }


    public static void badEnd(World wld) {
        System.out.println("~~~ the terrorists gained control on the chems. you have failed");
        wld.printResults();
        System.exit(0);
    }





}

