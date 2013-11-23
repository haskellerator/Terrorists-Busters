package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:11
 * To change this template use File | Settings | File Templates.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class InputParser {

    public static String fileName;

    public static Graph inputFileParser() throws IOException {
        Graph g = null;
        BufferedReader reader = new BufferedReader(new FileReader(fileName)); // opens the file for reading
        String line = null;

		/* reads the lines of the input file one by one, and parsing the arguments given */
        while(( line = reader.readLine()) != null )
        {
            if(line.length() != 0){
                char action = line.charAt(1); 	// the letter tells us which action to take
                int arg1 = getInteger(line, 3);	// most of the time there are 2 paremeters for each action
                int arg2 = getInteger(line, 7);
                if(action == 'V'){				// means that the file has read the amount of vertices in the graph
                    g = new Graph(arg1);		// creates a new graph with that amount, no edge exist this stage

				/* parse an edge, with the nodes that it connects between, and if its blocked or clear */
                } else if (action == 'E'){
                    int w = getInteger(line, 12);
                    boolean isClear= readClear(line.charAt(14));
                    g.updateEdge(arg1,arg2,w,!isClear); 	// connects the nodes

				/* M and C with tells where to set chemicals and military in the graph */
                } else if (action == 'M'){
                    g.getVertex(arg1-1).setMUnits(arg2);
                } else if (action == 'C'){
                    g.getVertex(arg1-1).setChemicals(arg2);
                } else {
                    System.out.println("An error occured.. quitting"); // in case of unhandled read
                    reader.close();
                    return null;
                }
            }
        }
        reader.close();
        return g;
    }


    private static Boolean readClear(char c) {
        return 'C' == c;
    }

    /* converts s[index] to integer, can do so up to 3 digits numbers*/
    public static int getInteger(String s,int index){
        int i = 0, acc = 0;
        while(true){
            int flag = Character.digit(s.charAt(index+i),10);
            if( flag == -1) return acc;
            acc = acc * 10;
            acc += flag;
            i++;
        }
    }

    public static ArrayList<Action> actionsSetup() throws IOException{
        System.out.println("~~~ Now setting actions cost");
        System.out.println("~~~ please Choose a cost for the no-op action:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<Action> a = new ArrayList<Action>(2);
        double c = costParser(br);
        a.add(new NoOp(c));
        System.out.println("~~~ please Choose a cost of going to hell:");
        c = costParser(br);
        a.add(new Drive(c));
        return a;
    }

    public static ArrayList<Agent> agentsSetup(Graph g, ArrayList<Action> actions) throws IOException{
        System.out.println("~~~ Now choosing agents:");
        System.out.println("~~~ How many agents would you like to deploy?");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int usrsNum = numOfAgents(br);

        ArrayList<Agent> as = new ArrayList<Agent>(usrsNum);
        for(int i = 0; i < usrsNum ; i++){
            System.out.println("~~~ What is the type of agent "+ (i+1) +"?");
            System.out.println("~~~ Options: h - human, t - terrorist buster, g - greedy");
            System.out.println("~~~          b - greedy bfs, a - A*, r - runtime A*");
            System.out.println("~~~ What is your choice??");
            Agent ag = options(br,i,g,actions);
            if (ag == null) return null;
            as.add(ag);
        }
        return as;
    }

    private static int startingPoint(BufferedReader br,int legalNumber) throws IOException {
        //int n = Character.digit(br.readLine().charAt(0),10);
        int n = Integer.parseInt(br.readLine());
        if(n <= 0 || n > legalNumber) {
            System.out.println("~~~ Invalid Number");
            return startingPoint(br, legalNumber);
        }
        return n-1;
    }


    private static int numOfAgents(BufferedReader br) throws IOException {
        int n = Integer.parseInt(br.readLine());
        if(n <= 0 ) {
            System.out.println("~~~ Invalid Number");
            return numOfAgents(br);
        }
        return n;
    }

    private static double costParser(BufferedReader br) throws IOException  {
        System.out.println("~~~ Please enter a valid cost:");
        double d;
        try {
            d = Double.parseDouble(br.readLine());
        } catch (NumberFormatException e) {
            d = -1;
        }
        if(d <= 0) {
            System.out.println("~~~ Invalid Number");
            return costParser(br);
        }
        return d;
    }

    private static Agent options(BufferedReader br,int index,Graph g, ArrayList<Action> actions) throws IOException{
        int start, start2, end,wf,mes; // weight factor , maximum expansion steps
        char mode = br.readLine().charAt(0);
        if('h' == mode ){
			/* human agent setup */
            System.out.println("~~~ Please choose entry location: ");
            start = startingPoint(br, g.getVertexAmount());
            System.out.println("~~~ Please choose goal location: ");
            end =startingPoint(br, g.getVertexAmount());
            return new HumanAgent(index+1, mode, g.getVertex(start),g.getVertex(end), actions);
        } else if('b' == mode ) {
			/* greedy bfs agent setup */
            System.out.println("~~~ Please choose entry location: ");
            start = startingPoint(br, g.getVertexAmount());
            System.out.println("~~~ Please choose goal location: ");
            end = startingPoint(br, g.getVertexAmount());
            System.out.println("~~~ Please choose weight factor: ");
            wf = startingPoint(br, Integer.MAX_VALUE);
            return new GBFSAgent(index+1,mode,g.getVertex(start),g.getVertex(end),actions,wf);
        } else if('a' == mode ) {
			/* A star agent setup */
            System.out.println("~~~ Please choose entry location: ");
            start = startingPoint(br, g.getVertexAmount());
            System.out.println("~~~ Please choose goal location: ");
            end = startingPoint(br, g.getVertexAmount());
            System.out.println("~~~ Please choose weight factor: ");
            wf = startingPoint(br, Integer.MAX_VALUE) ;
            return new ASAgent(index+1,mode,g.getVertex(start),g.getVertex(end),actions,wf+1);
        } else if('r' == mode ) {
			/* runtime A star setup */
            System.out.println("~~~ Please choose entry location: ");
            start = startingPoint(br, g.getVertexAmount());
            System.out.println("~~~ Please choose goal location: ");
            end = startingPoint(br, g.getVertexAmount());
            System.out.println("~~~ Please choose weight factor: ");
            wf = startingPoint(br, Integer.MAX_VALUE);
            System.out.println("~~~ Please choose a maximum for the expansion steps each round: ");
            mes = startingPoint(br, Integer.MAX_VALUE);
            return new RTASAgent(index+1,mode,g.getVertex(start),g.getVertex(end),actions,wf+1,mes+1);
        } else if('q' == mode ) {
            System.out.println("~~~ Exiting, bye bye");
            return null;
        }
        else if ( mode =='z' ){
            // Zero Sum Game
            System.out.println("~~~ Please choose max entry location: ");
            start = startingPoint(br, g.getVertexAmount());
            //System.out.println("~~~ Please choose goal location: ");
            //end = startingPoint(br, g.getVertexAmount());
            System.out.println("~~~ Please choose min entry location: ");
            start2 = startingPoint(br, g.getVertexAmount());
            return new ZeroSumAgent(/* TODO */);
        }
        else if ( mode =='n' ){
            // Non!! Zero Sum Game
            System.out.println("~~~ Please choose entry location: ");
            start = startingPoint(br, g.getVertexAmount());
            //System.out.println("~~~ Please choose goal location: ");
            //end = startingPoint(br, g.getVertexAmount());
            System.out.println("~~~ Please choose min entry location: ");
            start2 = startingPoint(br, g.getVertexAmount());
            return new NonZeroSumAgent(/* TODO */);
        }
        else if ( mode =='f' ){
            // Zero Sum Game
            System.out.println("~~~ Please choose entry location: ");
            start = startingPoint(br, g.getVertexAmount());
            //System.out.println("~~~ Please choose goal location: ");
            //end = startingPoint(br, g.getVertexAmount());
            System.out.println("~~~ Please choose min entry location: ");
            start2 = startingPoint(br, g.getVertexAmount());
            return new FullyCooperativeAgent(/* TODO */);
        }
        else{
            System.out.println("~~~ error, unsupported option: " + mode + ",retry");
            return options(br,index,g,actions);
        }
    }

}
