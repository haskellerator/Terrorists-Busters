package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:10
 * To change this template use File | Settings | File Templates.
 */

import java.util.ArrayList;
import java.util.Iterator;

public class Heuristic {
    private State st;
    private Graph G;
    private Vertex s;
    private diikstra d;
    public ArrayList<diikstra_ver> d_list;


    public Heuristic(State st) {
        this.st = st;
        this.G = st.getGraph();
//		this.s = st.get_A_loc();
        this.s = st.getAgent().getGoal();
        this.d = new diikstra(this.s);
        this.d_list = (this.d).get_all_d(this.s, this.G);
    }

    public double h() {
        double val = 0;
        Iterator<diikstra_ver> it = d_list.iterator();
        while (it.hasNext()) {
            diikstra_ver v = it.next();
            if (G.getVertex(v.get_index() - 1).hasChems()) {
                val += v.get_dis();
            }
        }
        return val;
    }

    public double get_h_value(Vertex t) {
        ArrayList<Vertex> chems = G.getChemV();
        double ans = 0;
        double t_dis = (this.d).get_t_dis_in_dList(d_list, t.getIndex());

        int v_c_index;
        int v_index;
        double v_dis;
        double dis;

        for (int i = 0; i < chems.size(); i++) {
            // get the index of the vertex in chem ver
            v_c_index = (chems.get(i)).getIndex();
            // get the index of him in d_list
            v_index = (this.d).getIndexInList(this.d_list, v_c_index);
            // get his distance
            v_dis = ((this.d_list).get(v_index)).get_dis();
            // compute the distance between t and chems.get(i):
            dis = Math.abs(t_dis - v_dis);
            // inc the ans
            ans = ans + dis;
        }

        return ans * 2;

    }

    public double get_f_value(Double H_value) {
        double ans = 0;

        ArrayList<Vertex> chems = G.getChemV();

        int v_c_index;
        int v_index;
        double v_dis;

        for (int i = 0; i < chems.size(); i++) {
            // get the index of the vertex in chem ver
            v_c_index = (chems.get(i)).getIndex();
            // get the index of him in d_list
            v_index = (this.d).getIndexInList(this.d_list, v_c_index);
            // get his distance
            v_dis = ((this.d_list).get(v_index)).get_dis();
            // we already know that d[s] = 0
            // therefore we only accumulate the d[v]
            ans = ans + v_dis;
        }
        return ans;
    }

    // return the dis between s and t
    public double get_dis(Vertex s, Vertex t) {
        int s_index = this.d.getIndexInList(this.d_list, s.getIndex());
        int t_index = this.d.getIndexInList(this.d_list, t.getIndex());
        double s_dis = ((this.d_list).get(s_index)).get_dis();
        double t_dis = ((this.d_list).get(t_index)).get_dis();
        double ans = Math.abs(s_dis - t_dis);
        return ans;
    }

    public double g() {
        return st.getAgent().getCost();
    }


}

