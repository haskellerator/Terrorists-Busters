package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:10
 * To change this template use File | Settings | File Templates.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HumanAgent extends Agent {

    private boolean adversarial;
    private boolean reqChems;
    private boolean askEsc;
    BufferedReader br;

    public HumanAgent(int serialNum, char type, Vertex initial, Vertex goal, ArrayList<Action> actions, boolean adversarial) {
        super(serialNum, type, initial, goal, actions);
        this.br = new BufferedReader(new InputStreamReader(System.in));
        this.adversarial = adversarial;
    }


    //copy constuctor for the adversarial games
    public HumanAgent(HumanAgent other, Graph g) {
        super(other, g);
        adversarial = true;
        this.br = other.br;
    }

    @Override
    public void decide(Graph g) throws IOException {
        System.out.println("~~~ agent " + super.serialNum + ",( C:" + reqChems + ",M:" + askEsc + ") what would you do? (first 4 options won't skip your turn)");
        reqChems = false;
        askEsc = false;
        choose(g);
    }

    private void choose(Graph g) throws IOException {
        System.out.println("~~~ options : v - show world");
        System.out.println("~~~           s - shows the immidiate surroundings (current vertex and it's outgoing edges)");
        System.out.println("~~~           r - request escort (if available on location)");
        System.out.println("~~~           p - pickup chemicals (if available on location)");
        System.out.println("~~~           d - drive");
        System.out.println("~~~           n - no-op");
        char cmd = parseOp();
        Edge e = null;
        switch (cmd) {
            case 'v':
                System.out.println(g.toString());
                choose(g);
                break;
            case 's':
                System.out.println(super.location.toString());
                System.out.println("\n--------------------------\n");
                choose(g);
                break;

            case 'r':
                if (!askEsc && super.location.getMUnits() > 0) {
                    askEsc = true;
                    System.out.println("~~~ Escort acquired");
                    System.out.println("\n--------------------------\n");
                } else if (askEsc) {
                    System.out.println("~~~ You are already Escorted");
                    System.out.println("\n--------------------------\n");
                } else {
                    System.out.println("~~~ No escort on region");
                    System.out.println("\n--------------------------\n");
                }
                choose(g);
                break;


            case 'p':
                if (!reqChems && super.location.getChemicals() > 0) {
                    reqChems = true;
                    System.out.println("~~~ Chemicals loaded");
                    System.out.println("\n--------------------------\n");
                } else if (reqChems) {
                    System.out.println("~~~ You are already loaded");
                    System.out.println("\n--------------------------\n");
                } else {
                    System.out.println("~~~ No chemicals on region");
                    System.out.println("\n--------------------------\n");
                }
                choose(g);
                break;


            case 'd':
                System.out.println("~~~ where would you like to go?:");
                e = parseNum(g);
                super.actions.get(1).action(this, e, reqChems, askEsc, true);
                break;
            case 'n':
                super.actions.get(0).action(this, null, false, false, true);
                break;
        }// end case
    }// end function choose

    private char parseOp() throws IOException {
        String s = br.readLine();
        char fst = s.charAt(0);
        if (fst == 'v' || fst == 's' || fst == 'r' || fst == 'p' || fst == 'n' || fst == 'd') { // fst is valid arg, and in case of d, s also must have length = 3
            return fst;
        } else {
            System.out.println("~~~ invalid input, retry...");
            return parseOp();
        }
    }

    private Edge parseNum(Graph g) throws IOException {
        int num;
        try {
            num = Integer.parseInt(br.readLine());

        } catch (NumberFormatException e) {
            num = -1;
        }
        if (num - 1 >= 0 && num - 1 < g.getVertexAmount() && location.getEdgeWith(g.getVertex(num - 1)) != null) {
            return location.getEdgeWith(g.getVertex(num - 1));
        } else {
            System.out.println("~~~ Invalid Vertex to travel, try again.");
            return parseNum(g);
        }
    }

    public void closeStream() throws IOException {
        br.close();
    }


    public boolean isReqChems() {
        return reqChems;
    }

    public void setReqChems(boolean reqChems) {
        this.reqChems = reqChems;
    }

    public boolean isAskEsc() {
        return askEsc;
    }

    public void setAskEsc(boolean askEsc) {
        this.askEsc = askEsc;
    }

    // TODO ADD TOSTRING AND SCORE FUNCTIONS FOR ADVERSARIAL


    public double getScore() {
        return reward - cost;
    }

    @Override
    public String toString() {
        String s = null;
        if (adversarial) {
            s = ">>> Human Agent " + serialNum + " : location = " + location.getIndex() + " , reward = " + reward
                    + " , cost = " + cost + " , actions made = " + actionsMade;
        } else {
            s = ">>> Agent " + serialNum + " " + type + " : location = " + location.getIndex() + " , carry chems = " + chemicals + " , has escort = " + escort +
                    " , cost = " + cost + " , actions made = " + actionsMade;
        }
        return s;
    }

}

