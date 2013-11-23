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
import util.InputParser;



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
        System.out.println("~~~ Finished parsing.");

        ArrayList<Action> actions = InputParser.actionsSetup();			// creating actions array
        ArrayList<Agent> agents = InputParser.agentsSetup(g,actions);	// creating the agents


		/* if any is null, it means some parameter is incorrect and an ending is forced*/

        if(g == null || actions == null || agents == null){
            System.out.println("~~~ Simulation ended prematurely, goodbye bye ");
            return;
        }
        World wld = new World(g,agents,actions);   						// creating a world in which everything occurs
        wld.startSimulation();											// statring...
        System.out.println("~~~ Simulation ended");

    }


    public static void badEnd(World wld) {
        System.out.println("~~~ the terrorists gained control on the chems. you have failed");
        wld.printResults();
        System.exit(0);
    }





}

