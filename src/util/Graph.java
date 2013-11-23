package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:09
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class Graph {
    private ArrayList<Vertex> vertexs;
    private ArrayList<Edge> edges;

    public Graph(Graph other){
        vertexs = new ArrayList<Vertex>(other.getVertexAmount());
        edges = new ArrayList<Edge>(other.getEdges().size());

        Iterator<Vertex> vit = other.getVertices().iterator();
        while(vit.hasNext()){
            vertexs.add(new Vertex(vit.next()));
        }

        Iterator<Edge> eit = other.getEdges().iterator();
        while(eit.hasNext()){
            Edge e=eit.next() ,e2 = new Edge(e);
            edges.add(e2);
            int os =  e.getFirst().getIndex()-1,ss = e.getSecond().getIndex()-1;	// one side , second side
            e2.setup(vertexs.get(os), vertexs.get(ss));
            vertexs.get(os).addEdge(e2);
            vertexs.get(ss).addEdge(e2);
        }
    }

    public Graph(int vNumber){

        vertexs = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        for(int i = 0 ; i < vNumber ; i++){
            vertexs.add(i, new Vertex(i+1)); // have to test that the vectors are add correctly
        }
    }


    public static Graph dupGraph(Graph g2) throws IOException{
        Graph g = InputParser.inputFileParser();
        Iterator<Vertex> vit2 = g2.getVertices().iterator(), vit = g.getVertices().iterator();
        while(vit.hasNext()){
            Vertex v = vit.next(),v2 = vit2.next();;
            v.setChemicals(v2.getChemicals());
            v.setMUnits(v2.getMUnits());
        }
        Iterator<Edge> eit2 = g2.getEdges().iterator(), eit = g.getEdges().iterator();
        while(eit.hasNext()){
            Edge e = eit.next(), e2 = eit2.next();
            e.setBlocked(e2.isBlocked());
        }
        return g;
    }


    // static graph display function
    public String toString(){
        String ans = "\n<<<<<<< World Map >>>>>>>\n\n";
        for (int i = 0 ; i < vertexs.size() ; i ++ ){
            ans +=  "" +vertexs.get(i) +"\n";
        }
        ans +=  "\n--------------------------\n";
        return ans;
    }

    public void updateEdge(int fstNdIdx, int sndNdIdx, int w, boolean isClear) {
        Vertex v1 = vertexs.get(fstNdIdx-1), v2 = vertexs.get(sndNdIdx-1);
        Edge e = new Edge(v1,v2,w,isClear);
        edges.add(e);
        v1.addEdge(e);
        v2.addEdge(e);
    }

    public Vertex getVertex(int i) {
        return vertexs.get(i);
    }

    public Edge getEdge(Vertex first, Vertex second){
        return first.getEdgeWith(second);
    }

    public int getVertexAmount() {
        return vertexs.size();
    }

    public int getTotalChem() {
        int chems = 0;
        Iterator<Vertex> it = vertexs.iterator();
        while(it.hasNext()){
            chems += it.next().getChemicals();
        }
        return chems;
    }

    public int getTotalEscorts() {
        int escorts = 0;
        Iterator<Vertex> it = vertexs.iterator();
        while(it.hasNext()){
            escorts += it.next().getMUnits();
        }
        return escorts;
    }

    public ArrayList<Edge> getEdges(){
        return edges;
    }

    public ArrayList<Vertex> getVertices(){
        return vertexs;
    }

    // makes all edges in the graph unblocked
    public void setEdgesUnblocked(){
        Iterator<Edge> it = edges.iterator();
        while(it.hasNext()){
            Edge e = it.next();
            if(e.isBlocked())
                e.switchBlocked();
        }
    }

    public Edge retDup(Edge e){
        Iterator<Edge> it = edges.iterator();
        while(it.hasNext()){
            Edge e2 = it.next();
            if(e2.compare(e))
                return e2;
        }
        return null;
    }

    public Edge getDupEdge(Edge other) {
        Iterator<Edge> it = this.getEdges().iterator();
        while(it.hasNext()){
            Edge e = it.next();
            if(e.equals2(other))
                return e;
        }
        System.out.println("this is a mistake inside get dup edge");
        return null;
    }



    public Vertex retDupVertex(Vertex olocation) {
        Iterator<Vertex> it = vertexs.iterator();
        while(it.hasNext()){
            Vertex v = it.next();
            if(v.equals2(olocation)){

                return v;
            }
        }
        return null;
    }

    public boolean notIdentical(Graph g){ // Change name to: notIdentical_G
        if(this == g) return false;
        if(this.equals(g)){
            int ea = this.edges.size(), // this edges amount
                    gea = g.getEdges().size(); // g edges amount
            Edge  e=null,ge = null;
            for( int i = 0 ; i < ea ; i++){
                e = edges.get(i);
                for(int j = 0 ; j < gea ; j++){
                    ge = g.getEdges().get(j);
                    e.equals(ge);
                } // end j for
            } // end i for
        }// end if
        return true;
    }


    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        else if(!(obj instanceof Graph)){
            return false;
        }
        boolean ans = true;
        Graph g2 = (Graph) obj;

        Iterator<Vertex> vit2 = g2.getVertices().iterator(), vit = this.getVertices().iterator();
        while(vit.hasNext() && ans){
            Vertex v = vit.next(),v2 = vit2.next();;
            ans = v.equals(v2);

        }
        Iterator<Edge> eit2 = g2.getEdges().iterator(), eit = this.getEdges().iterator();
        while(eit.hasNext() && ans){
            Edge e = eit.next(), e2 = eit2.next();
            ans = e.equals(e2);
        }

        return ans;
    }




    public void deledge(int i){
        this.edges.remove(i);
    }

    public ArrayList<Vertex> getChemV() {
        ArrayList<Vertex> ans = new ArrayList<Vertex>();
        for (int i=0; i<this.getVertexAmount(); i++){
            if ( ( (this.vertexs).get(i) ).hasChems() ){
                ans.add( (this.vertexs).get(i) );
            }
        }// end for
        return ans;
    }



}


