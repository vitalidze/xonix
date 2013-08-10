package su.litvak.xonix;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly
 * Date: 03.08.13
 * Time: 1:18
 * To change this template use File | Settings | File Templates.
 */
public class FieldCutTest {
    // TODO write Emily diagonal test case (left-up-left-up)
    @Test
    public void testVerticalCut1() {
        new FieldFixture(5, 5)
            .path(2, 1,
                  2, 2,
                  2, 3,
                  2, 4,
                  2, 5)
            .waterRect(1, 1, 1, 5)
        .check();
    }

    @Test
    public void testVerticalCut2() {
        new FieldFixture(5, 5)
            .path(4, 1,
                  4, 2,
                  4, 3,
                  4, 4,
                  4, 5)
            .waterRect(5, 1, 1, 5)
        .check();
    }

    @Test
    public void testHorizontalCut1() {
        new FieldFixture(5, 5)
            .path(1, 2,
                  2, 2,
                  3, 2,
                  4, 2,
                  5, 2)
            .waterRect(1, 1, 5, 1)
        .check();
    }

    @Test
    public void testHorizontalCut2() {
        new FieldFixture(5, 5)
            .path(1, 4,
                  2, 4,
                  3, 4,
                  4, 4,
                  5, 4)
            .waterRect(1, 5, 5, 1)
        .check();
    }

    @Test
    public void testDiagonalCut1() {
        new FieldFixture(5, 5)
            .path(3, 1,
                  4, 2,
                  5, 3)
            .waterPoints(4, 1,
                         5, 1,
                         5, 2)
        .check();
    }

    @Test
    public void testDiagonalCut2() {
        new FieldFixture(5, 5)
            .path(1, 2,
                  2, 3,
                  3, 4,
                  4, 5)
            .waterPoints(1, 3,
                         1, 4,
                         1, 5,
                         2, 4,
                         2, 5,
                         3, 5)
        .check();
    }

    @Test
    public void testZeroArea() {
        new FieldFixture(5, 5).path(2, 1, 3, 1, 4, 1).check();
        new FieldFixture(5, 5).path(5, 4, 5, 5).check();
        new FieldFixture(5, 5).path(2, 5, 3, 5, 4, 5, 5, 5).check();
        new FieldFixture(5, 5).path(1, 2, 1, 3, 1, 4).check();
    }

    @Test
    public void testZeroStartNonZeroEnd() {
        new FieldFixture(5, 5)
            .path(1, 5,
                  1, 4,
                  1, 3,
                  2, 3,
                  2, 2,
                  2, 1)
            .waterRect(1, 1, 2, 2)
        .check();
    }
}
