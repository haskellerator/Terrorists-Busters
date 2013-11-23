package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:06
 * To change this template use File | Settings | File Templates.
 */

public class diikstra_ver {
    private boolean visited;
    private double distans;
    private int index;
    private int prev;

    public diikstra_ver(int i){
        this.distans = Double.MAX_VALUE;
        this.index = i;
        this.visited = false;
        this.prev = -1;
    }

    public double get_dis(){
        return this.distans;
    }

    public void set_dis(double n){
        this.distans = n;
    }

    public int get_index(){
        return this.index;
    }

    public void set_index(int i) {
        this.index = i;
    }

    public boolean isVisited(){
        return this.visited;
    }

    public void setVisit(boolean b){
        this.visited = b;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }

    public int getPrev() {
        return prev;
    }

}
