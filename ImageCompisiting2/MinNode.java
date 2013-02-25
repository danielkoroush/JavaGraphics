import java.util.Vector;
import java.awt.Point;
class MinNode{

    public MinNode(double Cost, MinNode parent, MinNode right, MinNode left){
        cost = Cost;
        p=parent;
        l=left;
        r=right;
    }
 
    public Vector list=new Vector();
    public double cost;
    public MinNode p=null;
    public MinNode l=null;
    public MinNode r=null;
}