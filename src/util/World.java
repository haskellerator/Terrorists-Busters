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

    public World(Graph g, ArrayList<Agent> ag, ArrayList<Action> ac){
        worldmap = g;
        agents = ag;
        actions = ac;
        ((Drive) ac.get(1)).setWorld(this);
    }

    public void startSimulation() throws IOException{
        System.out.println("~~~ Simulation Starting");
        Iterator<Agent> ait;
        boolean b = true;
        while(b){
            ait = agents.iterator();
            while(ait.hasNext() && b){
                printMidState();
                ait.next().decide(worldmap);
                //worldmap.setVertexToFalse();
                b = isNotDone();
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

    private boolean isNotDone() {
        return !(  (getTotalChem() == 0) && noChemsOnAgents() ) ;
    }

    private boolean noChemsOnAgents() {
        Iterator<Agent> it = agents.iterator();
        while(it.hasNext())
            if(it.next().hasChems())
                return false;
        return true;
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

    // old func

//	public void printResults() {
//		System.out.println("~~~ All the chems have been disposed of. Order has been restored, goodbye!");
//		System.out.println("~~~ The total score is : " + totalScore());
//		Iterator<Agent> it = agents.iterator();
//		while(it.hasNext()){
//			Agent a = it.next();
//			if(a.getType() == 'b'){
//				System.out.println("~~~ Agent " + a.getSerial() + " { Score : "+ a.getScore() + " , Actions : " + a.getActions() + " , Preformance : "
//			+ ((HGSAgent)a).getPreformance() + " , Expansions : " 	+ ((HGSAgent)a).getSteps() + " }" );
//			} else if(a.getType() == 'a'){
//				System.out.println("~~~ Agent " + a.getSerial() + " { Score : "+ a.getScore() + " , Actions : " + a.getActions() + " , Preformance : "
//			+ ((ASAgent)a).getPreformance() + " , Expansions : " 	+ ((ASAgent)a).getSteps() + " }" );
//			}if( a.getType() == 'r' ){
//				System.out.println("~~~ Agent " + a.getSerial() + " { Score : "+ a.getScore() + " , Actions : " + a.getActions() + " , Preformance : "
//			+ ((RTASAgent)a).getPreformance() + " , Expansions : " 	+ ((RTASAgent)a).getSteps() + " }" );
//			}else
//				System.out.println("~~~ Agent " + a.getSerial() + " { Score : "+ a.getScore() + " , Actions : " + a.getActions() + " }" );
//		}
//	}

    // new func
    public void printResults() {
        System.out.println("~~~ All the chems have been disposed of. Order has been restored, goodbye!");
        System.out.println("~~~ The total score is : " + totalScore());
        Iterator<Agent> it = agents.iterator();
        while(it.hasNext()){
            Agent a = it.next();
            if(a.getType() == 'b'){
                System.out.println("~~~ Agent " + a.getSerial() + " { Score : "+ a.getScore() + " , Actions : " + a.getActionsMade() + " , Preformance : "
                        + ((GBFSAgent)a).getPreformance() + " , Expansions : " 	+ ((GBFSAgent)a).getSteps() + " }" );
            } else if(a.getType() == 'a'){
                System.out.println("~~~ Agent " + a.getSerial() + " { Score : "+ a.getScore() + " , Actions : " + a.getActionsMade() + " , Preformance : "
                        + ((ASAgent)a).getPreformance() + " , Expansions : " 	+ ((ASAgent)a).getSteps() + " }" );
            }else if( a.getType() == 'r' ){
                System.out.println("~~~ Agent " + a.getSerial() + " { Score : "+ a.getScore() + " , Actions : " + a.getActionsMade() + " , Preformance : "
                        + ((RTASAgent)a).getPreformance() + " , Expansions : " 	+ ((RTASAgent)a).getTS() + " }" );
            }else
                System.out.println("~~~ Agent " + a.getSerial() + " { Score : "+ a.getScore() + " , Actions : " + a.getActionsMade() + " }" );
        }
    }

    private double totalScore(){
        double total = 0;
        Iterator<Agent> it = agents.iterator();
        while(it.hasNext())
            total += it.next().getScore();
        return total;
    }


}



