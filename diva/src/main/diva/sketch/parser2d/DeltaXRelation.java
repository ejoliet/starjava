/*
 * $Id: DeltaXRelation.java,v 1.2 2001/07/22 22:01:50 johnr Exp $
 *
 * Copyright (c) 1998-2001 The Regents of the University of California.
 * All rights reserved. See the file COPYRIGHT for details.
 */
package diva.sketch.parser2d;
import diva.sketch.recognition.SceneElement;
import java.awt.geom.Rectangle2D;

/**
 * Calculate the euclidean distance between given sites on pairs of
 * input rectangles.
 *
 * @author  Michael Shilman (michaels@eecs.berkeley.edu)
 * @version $Revision: 1.2 $
 * @rating  Red
 */
public class DeltaXRelation implements Relation {
    /**
     * The name that this relation goes by in text form.
     */
    public static String NAME = "deltaX";
    
    /**
     * The site of interest on R1.
     */
    private int _site1;

    /**
     * The site of interest on R2.
     */
    private int _site2;

    /**
     * Calculate the delta X between the given sites on two input
     * elements (e1.x - e2.x).  The sites should be one of CENTER,
     * NORTH_WEST, NORTH, etc.
     */
    public DeltaXRelation(int site1, int site2) {
        setSite1(site1);
        setSite2(site2);
    }
    
    /**
     * Return the distance between the constructor-specified sites on
     * r1 and r2, respectively.
     */
    public double apply (SceneElement e1, SceneElement e2) {
        Rectangle2D r1 = e1.getBounds();
        Rectangle2D r2 = e2.getBounds();
        return RelationUtilities.siteX(r1, _site1) -
            RelationUtilities.siteX(r2, _site2);
    }

    /**
     * Debugging output.
     */
    public static void debug (String s) {
        System.out.println(s);
    }

    /**
     * The name of this relation.
     */
    public String getName() {
        return NAME;
    }

    /**
     * Get the site for the first rectangle.
     */
    public int getSite1() {
        return _site1;
    }

    /**
     * Get the site for the second rectangle.
     */
    public int getSite2() {
        return _site2;
    }
    
    /**
     * Set the site for the first rectangle.
     */
    public void setSite1(int site1) {
        RelationUtilities.checkSite(site1);
        _site1 = site1;
    }

    /**
     * Set the site for the second rectangle.
     */
    public void setSite2(int site2) {
        RelationUtilities.checkSite(site2);
        _site2 = site2;
    }

    /**
     * Pretty print the relation in the grammar format.
     */
    public String toString(String e1Name, String e2Name) {
        return getName() + "(" + e1Name + "." + RelationUtilities.printSite(_site1)
            + ", " + e2Name + "." + RelationUtilities.printSite(_site2) + ")";
    }

    /**
     * Print out the contents of this relation.
     */
    public String toString() {
        String out = "DeltaXRelation[\n";
        out = out + "  Site1: " + RelationUtilities.printDirection(_site1) + "\n";
        out = out + "  Site2: " + RelationUtilities.printDirection(_site2) + "\n";
        out = out + "]";
        return out;
    }    
}






