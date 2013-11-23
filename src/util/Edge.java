package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:07
 * To change this template use File | Settings | File Templates.
 */

public class Edge {
    private Vertex first;
    private Vertex second;
    private Boolean blocked;
    private double weight;

    // copy constuctor for the graph
    public Edge(Edge other){
        this.first = null;
        this.second = null;
        this.blocked = other.isBlocked();
        this.weight = other.getWeight();

    }

    public Edge(Vertex f, Vertex s,int w, Boolean bl){
        this.first = f;
        this.second = s;
        this.weight = w;
        this.blocked = bl;
    }

    public Vertex otherSide(Vertex from){
        if (from == first) return second;
        return first;
    }

    public String toString(Vertex from){
        String ans = "#---#\t   ";
        if(from == first)
            ans += first.getIndex() + " ---> " + second.getIndex() ;
        else
            ans += second.getIndex() + " ---> " + first.getIndex() ;

        ans += " ( weight: " + weight + " , clear: " + Boolean.valueOf(!blocked) ;
        return ans + " ) ";
    }

    public double getWeight() {
        return weight;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void switchBlocked() {
        blocked = !blocked;

    }

    public void setBlocked(boolean state){
        blocked = state;
    }


    @Override
    public String toString(){
        String ans = "#---#\t   ";
        ans += first.getIndex() + " ---> " + second.getIndex() ;
        ans += " ( weight: " + weight + " , clear: " + Boolean.valueOf(!blocked) ;
        return ans + " ) ";
    }

    public boolean hasVertex(Vertex v){
        return v == first || v == second;
    }

    public Vertex getFirst(){
        return first;
    }

    public Vertex getSecond(){
        return second;
    }

    // for the state function
    public void setup(Vertex fst, Vertex snd){
        this.first = fst;
        this.second = snd;
    }

    public boolean compare(Edge other){
        return (this.first.getIndex() == other.getFirst().getIndex() &&
                this.second.getIndex() == other.getSecond().getIndex()) ||
                (this.first.getIndex() == other.getSecond().getIndex() &&
                        this.second.getIndex() == other.getFirst().getIndex());
    }


    // need to check also the weight ????
    public boolean equals(Object obj){
        if(obj instanceof Edge){
            Edge e = (Edge) obj;
            boolean v_comp = ((e.first.equals(first) && e.second.equals(second))
                    || (e.first.equals(second) && e.second.equals(first)))
                    && e.getWeight() == this.getWeight() && e.isBlocked() == this.isBlocked();
            return ( v_comp );// && this.weight == ((Edge) obj).getWeight() );
        }
        else return false;
    }

    public boolean equals2(Object obj){
        if(obj instanceof Edge){
            Edge e = (Edge) obj;
            boolean v_comp = ((e.first.equals(first) && e.second.equals(second))
                    || (e.first.equals(second) && e.second.equals(first)))
                    && e.getWeight() == this.getWeight();
            return ( v_comp );// && this.weight == ((Edge) obj).getWeight() );
        }
        else return false;
    }


    // checks that edges are duplicates
    public boolean notIdentical(Edge e){
        if(this.equals(e) && this !=e){
            if(!first.notIdentical(e.getFirst())){
                System.out.println(1);
                return false;
            } else if(!first.notIdentical(e.getSecond())){
                System.out.println(2);
                return false;
            } else if(!second.notIdentical(e.getFirst())){
                System.out.println(3);
                return false;
            } else if(!second.notIdentical(e.getSecond())){
                System.out.println(4);
                return false;
            }
        }
        return true;
    }


}
