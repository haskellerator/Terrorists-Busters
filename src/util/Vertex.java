package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:14
 * To change this template use File | Settings | File Templates.
 */

import java.util.ArrayList;
import java.util.Iterator;


public class Vertex {
    private ArrayList<Edge> edges;
    private int chemicals;
    private int militaryUnits;
    private int index;
    private diikstra_ver dv;

    // copy constructor for the state
    public Vertex(Vertex other){
        this.edges = new ArrayList<Edge>();
        this.chemicals = other.getChemicals();
        this.militaryUnits = other.getMUnits();
        this.index = other.getIndex();
        this.dv = null;
    }

    public Vertex(int i) {
        index = i;
        edges = new ArrayList<Edge>(0);
        setDv(null);
    }

    public int getIndex() {
        return index;
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public String toString(){
        String ans = "@---@ v " + index + " ( chems: " + chemicals + " , mUnits: " + militaryUnits + " )" ;
        for(int i = 0; i < edges.size(); i++)
            ans += "\n" + edges.get(i).toString(this);
        return ans;
    }

    public void setChemicals(int a) {
        chemicals = a;
    }

    public int getChemicals() {
        return chemicals;
    }

    public Edge getEdgeWith(Vertex other){
        Iterator<Edge> it = edges.iterator();
        while(it.hasNext()){
            Edge e = it.next();
            if(e.otherSide(this) == other){
                return e;
            }
        }
        return null;
    }

    public ArrayList<Edge> getEdges(){
        return edges;
    }

    public int getMUnits() {
        return militaryUnits;
    }

    public boolean hasMUnits(){
        return militaryUnits > 0;
    }

    public void setMUnits(int i){
        militaryUnits = i;
    }

    public void decMUnits(){
        if (hasMUnits()){
            militaryUnits -= 1;
        }
    }

    public boolean hasChems() {
        return chemicals > 0;
    }

    public void decChems(){
        if (hasChems()){
            chemicals -= 1;
        }
    }

    public diikstra_ver getDv() {
        return dv;
    }

    public void setDv(diikstra_ver dv) {
        this.dv = dv;
    }

    // some old func
    public boolean compare(Vertex olocation) {
        return olocation.getIndex() == this.index;
    }


    // makes sure it is a duplicate and not the same object
    public boolean notIdentical(Vertex other){
        return this.equals(other) && other != this;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Vertex)
            return ((Vertex)obj).getIndex() == this.index
                    && ((Vertex)obj).getChemicals() == this.chemicals
                    && ((Vertex)obj).getMUnits() == this.militaryUnits;
        else return false;
    }


    public boolean equals2(Object obj) {
        if(obj instanceof Vertex)
            return ((Vertex)obj).getIndex() == this.index;
        else return false;
    }

    public void deledgewith(Vertex v2){
        for(int i=0; i<edges.size(); i++){
            Edge e = edges.get(i);
            if ( (e.getSecond() ).getIndex() == v2.getIndex() ){
                edges.remove(i);
                i--;
            }
        }
    }

    public void deledge(int i){
        this.edges.remove(i);
    }



}

