package util;

/**
 * Created with IntelliJ IDEA.
 * User: talz
 * Date: 23/11/13
 * Time: 02:03
 * To change this template use File | Settings | File Templates.
 */

public interface Action {
    public int action(Agent a,Edge e, boolean tc, boolean te, boolean real);
    public double getCost(Agent a, Edge e ,boolean tkch, boolean tkes );
}
