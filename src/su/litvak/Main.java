package su.litvak;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 02.08.13
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args) {
        Field f = new Field(5, 5);

        System.out.println(f);

        Set<Point> path = new HashSet<Point>();
        path.add(new Point(1, 2));
        path.add(new Point(2, 2));
        path.add(new Point(3, 2));
        path.add(new Point(3, 3));
        path.add(new Point(3, 4));
        path.add(new Point(2, 4));
        path.add(new Point(1, 4));
        f.cut(path);

        System.out.println(f);
    }
}
