package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:07
 * To change this template use File | Settings | File Templates.
 */

import java.util.ArrayList;
import java.util.Iterator;


public class diikstra {

    private Vertex courent;
    private Vertex target;
    private Vertex next;

    public diikstra (Vertex s){
        this.courent = s;
        this.target = null;
        this.next = null;
    }

    public diikstra (Vertex s, Vertex t){
        this.courent = s;
        this.target = t;
        this.next = null;
    }

    // return a list of diikstra_ver with their d[] from s
    public ArrayList<diikstra_ver> get_all_d(Vertex s, Graph G){
        // the return list:
        ArrayList <diikstra_ver> ans = new ArrayList<diikstra_ver>();

        // init: nothing has visited yet
        for (int i=1; i <= G.getVertexAmount(); i++){
            diikstra_ver v = new diikstra_ver(i);
            ans.add(v);
        }
        // the source vertex is visited and distance is 0
        diikstra_ver start = ans.get(s.getIndex()-1);
        start.set_dis(0);

        // the Queue
        ArrayList <diikstra_ver> Q = new ArrayList<diikstra_ver>();
        // insert start into Q
        Q.add(start);

        // the loop of Dijkstra
        while(!Q.isEmpty()){
            diikstra_ver v = choose_min_dis(Q);

            Q.remove(getIndexInList(Q, v.get_index())) ;
            //v.setVisit(true);

            // synchronize with the real ver
            Vertex vv = G.getVertex(v.get_index() -1 );

            ArrayList<Edge> vv_edges = vv.getEdges();
            // for every nb of vv in the not visited list: do relax
            for (int i = 0; i<vv_edges.size(); i++){
                Edge e = vv_edges.get(i);
                Vertex uu = e.otherSide(vv);
                diikstra_ver u = ans.get(uu.getIndex() -1 );
                // relax on u
                if ( (u.get_dis() > v.get_dis() + e.getWeight() ) && !u.isVisited() ){
                    u.set_dis(v.get_dis() + e.getWeight());
                    u.setPrev(v.get_index());
                    Q.add(u);
                }// end relax
            }// end for
        }// end while
        return ans;
    } // end get_all_d


    /*
     * get a list of d[] with only clean paths
     */
    public ArrayList<diikstra_ver> find_d_of_clean(Vertex s, Graph G){
        Graph G_tag = get_cleanG(G);
        return get_all_d(s, G_tag);
    }


    /*
     * return a new G with only clean e
     */
    public Graph get_cleanG(Graph G){
        Graph G_tag = new Graph(G);
        ArrayList<Edge> edges = G_tag.getEdges();
        for (int i=0; i< edges.size(); i++){
            Edge e = edges.get(i);
            if (e.isBlocked()){
                Vertex v1 = e.getFirst();
                Vertex v2 = e.getSecond();
                G_tag.deledge(i);
                v1.deledgewith(v2);
                v2.deledgewith(v1);
                i--; // need to dec i because we have del from the list
            }
        }
        return G_tag;
    }


    // return the vertex that we need to go to in the path from s to t
    //return v such that e(s,v) is in the path from s to t
    // this is a greedy function
    // only path that is UnBlocked!!
    public Vertex get_next (Vertex s, Vertex t, Graph G){

        Graph G_tag = get_cleanG(G);
        ArrayList<diikstra_ver> d_list = get_all_d(s, G_tag); // here we use only in the clear e

        int t_index_in_dList = getIndexInList(d_list, t.getIndex());
        diikstra_ver v = d_list.get(t_index_in_dList);
        int pai = v.getPrev();

        while (pai != s.getIndex()){
            v = d_list.get(getIndexInList(d_list, pai));
            pai = v.getPrev();
        }
        Vertex ans = G_tag.getVertex(v.get_index()-1);

        return ans;
    }


    // return the vertex that we need to go to in the path from s to t
    //return v such that e(s,v) is in the path from s to t
    // this is a greedy function
    // don't care about blocked way!!!!!
    public Vertex get_next_with_army(ArrayList<diikstra_ver> d_list,
                                     Vertex s, Vertex t, Graph G){

        // v is the ver that represent t
        int v_i_in_dList = getIndexInList(d_list, t.getIndex());
        diikstra_ver v = d_list.get(v_i_in_dList -1);
        // u is the ver that represent pai[v]
        diikstra_ver u;
        boolean found = (s.getIndex() == v.get_index() );
        int i;
        while (!found){ // while the parent is not s
            i = getIndexInList( d_list, v.getPrev() );
            u = d_list.get(i-1);
            if (s.getIndex() == u.get_index())
                found = true;
            else{
                v_i_in_dList = getIndexInList(d_list, u.get_index());
                v = d_list.get(v_i_in_dList -1);
            }
        }

        // now v is the next of s
        // but v is diikstra ver...
        return G.getVertex(v.get_index()-1);
    }


    // if a terrorist buster need to find the closest terrorist from s
    // assume that s is with army
    // return the closest t with e(u,t) is blocked
    public Vertex get_closest_terorist(Vertex s, Graph G){
        ArrayList<diikstra_ver> d_list = get_all_d(s, G);
        int i = 0;
        if (s.getIndex() == 0)
            i++;
        diikstra_ver v = d_list.get(i);
        double best = v.get_dis();
        for (int j = 0; j < d_list.size(); j++){
            diikstra_ver vv = d_list.get(j);
            if ( vv.get_dis() < best && vv.get_index()-1 !=s.getIndex() ){
                Edge e = G.getEdge(s, G.getVertex(v.get_index()-1) );
                if (e.isBlocked()){
                    v = vv;
                    best = v.get_dis();
                }
            }
        }

        // check if we really find something
        Vertex ans = G.getVertex(v.get_index() -1 );
        ArrayList<Edge> ans_edges = ans.getEdges();
        boolean really = false;
        for (int k=0; k < ans_edges.size(); k++)
            if ( (ans_edges.get(i)).isBlocked() )
                really = true;
        if (really == true)
            return G.getVertex(v.get_index()-1);
        else // there are no terrorists!!
            return null;
    }

    // if a terrorist buster need to find his army
    // return next in the way from s to army (s,next) is in E
    // make sure that the path is unBlocked!! do this in the find next function
    public Vertex get_closest_army(Vertex s, Graph G){
        Graph G_tag = get_cleanG(G);
        ArrayList<diikstra_ver> d_list = get_all_d(s, G_tag);
        boolean found_t = false;
        diikstra_ver v;
        Vertex t = null;
        int v_index_in_Qlist;
        while (!d_list.isEmpty() && !found_t){
            v = choose_min_dis(d_list);
            t = G_tag.getVertex(v.get_index()-1);
            v_index_in_Qlist = getIndexInList(d_list, v.get_index());
            d_list.remove(v_index_in_Qlist);
            if (t.hasMUnits()){
                found_t = true;
            }// end first if
        }// end while


        if (!found_t){
            System.out.println("can't get to any army, please choose no-op");
            return null;
        }
        else{
            d_list = find_d_of_clean(s, G_tag);
            Vertex next = get_next(s, t, G);
            return next;
        }// end else
    } // end function


    public double get_t_dis_in_dList(ArrayList<diikstra_ver> d_list, int index){
        int i = getIndexInList(d_list, index);
        diikstra_ver v = d_list.get(i);
        return v.get_dis();
    }


    public int getIndexInList(ArrayList<diikstra_ver> d_list,
                              int index) {
        // return the index in d_list of the v with v.get_index =  index
        // very important when we add a new member and delete
        // the add and delete is in the diikstra loop
        Iterator<diikstra_ver> it = d_list.iterator();
        diikstra_ver v = it.next();
        boolean found = v.get_index() == index;
        int ans = 0;
        while (it.hasNext() && !found){
            v = it.next();
            found = v.get_index() == index;
            ans ++ ;
        }
        // if we get to the end of list and did't find...
        if (!found)
            ans = -1;
        return ans;
    }


    // this is only for self use
    private diikstra_ver choose_min_dis(ArrayList<diikstra_ver> list) {
        //return the vertex with the min d
        //int i = 0;
        diikstra_ver best = list.get(0);
        for (int i = 1; i < list.size(); i++){
            if ( (list.get(i)).get_dis() < best.get_dis() )
                best = list.get(i);
        }
        return best;
    }

    public Vertex getCourent() {
        return courent;
    }

    public Vertex getTarget() {
        return target;
    }


    public Vertex getNext() {
        return next;
    }



}
