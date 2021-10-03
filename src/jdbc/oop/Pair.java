package jdbc.oop;

public class Pair<X, Y> {

    public final X x;
    public final Y y;

    public Pair(X x, Y y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return x.toString() + " " + y.toString();
    }
}
