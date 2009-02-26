/* ********************************************************
 * This file automatically generated by SphMap.pl.
 *                   Do not edit.                         *
 **********************************************************/

package uk.ac.starlink.ast;


/**
 * Java interface to the AST SphMap class
 *  - map 3-d Cartesian to 2-d spherical coordinates. 
 * A SphMap is a Mapping which transforms points from a
 * 3-dimensional Cartesian coordinate system into a 2-dimensional
 * spherical coordinate system (longitude and latitude on a unit
 * sphere centred at the origin). It works by regarding the input
 * coordinates as position vectors and finding their intersection
 * with the sphere surface. The inverse transformation always
 * produces points which are a unit distance from the origin
 * (i.e. unit vectors).
 * <h4>Licence</h4>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public Licence as
 * published by the Free Software Foundation; either version 2 of
 * the Licence, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be
 * useful,but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public Licence for more details.
 * <p>
 * You should have received a copy of the GNU General Public Licence
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place,Suite 330, Boston, MA
 * 02111-1307, USA
 * 
 * 
 * @see  <a href='http://star-www.rl.ac.uk/cgi-bin/htxserver/sun211.htx/?xref_SphMap'>AST SphMap</a>  
 */
public class SphMap extends Mapping {
    /** 
     * Creates a SphMap.   
     * @throws  AstException  if an error occurred in the AST library
    */
    public SphMap(  ) {
        construct(  );
    }
    private native void construct(  );

    /**
     * Get 
     * sphMap input vectors lie on a unit sphere.  
     * This is a boolean attribute which indicates whether the
     * 3-dimensional vectors which are supplied as input to a SphMap
     * are known to always have unit length, so that they lie on a unit
     * sphere centred on the origin.
     * <p>
     * If this condition is true (indicated by setting UnitRadius
     * non-zero), it implies that a CmpMap which is composed of a
     * SphMap applied in the forward direction followed by a similar
     * SphMap applied in the inverse direction may be simplified
     * (e.g. by astSimplify) to become a UnitMap. This is because the
     * input and output vectors will both have unit length and will
     * therefore have the same coordinate values.
     * <p>
     * If UnitRadius is zero (the default), then although the output
     * vector produced by the CmpMap (above) will still have unit
     * length, the input vector may not have. This will, in general,
     * change the coordinate values, so it prevents the pair of SphMaps
     * being simplified.
     * <h4>Notes</h4>
     * <br> - This attribute is intended mainly for use when SphMaps are
     * involved in a sequence of Mappings which project (e.g.) a
     * dataset on to the celestial sphere. By regarding the celestial
     * sphere as a unit sphere (and setting UnitRadius to be non-zero)
     * it becomes possible to cancel the SphMaps present, along with
     * associated sky projections, when two datasets are aligned using
     * celestial coordinates. This often considerably improves
     * performance.
     * <br> - Such a situations often arises when interpreting FITS data and
     * is handled automatically by the FitsChan class.
     * <br> - The value of the UnitRadius attribute is used only to control
     * the simplification of Mappings and has no effect on the value of
     * the coordinates transformed by a SphMap. The lengths of the
     * input 3-dimensional Cartesian vectors supplied are always
     * ignored, even if UnitRadius is non-zero.
     * 
     *
     * @return  this object's UnitRadius attribute
     */
    public boolean getUnitRadius() {
        return getB( "UnitRadius" );
    }

    /**
     * Set 
     * sphMap input vectors lie on a unit sphere.  
     * This is a boolean attribute which indicates whether the
     * 3-dimensional vectors which are supplied as input to a SphMap
     * are known to always have unit length, so that they lie on a unit
     * sphere centred on the origin.
     * <p>
     * If this condition is true (indicated by setting UnitRadius
     * non-zero), it implies that a CmpMap which is composed of a
     * SphMap applied in the forward direction followed by a similar
     * SphMap applied in the inverse direction may be simplified
     * (e.g. by astSimplify) to become a UnitMap. This is because the
     * input and output vectors will both have unit length and will
     * therefore have the same coordinate values.
     * <p>
     * If UnitRadius is zero (the default), then although the output
     * vector produced by the CmpMap (above) will still have unit
     * length, the input vector may not have. This will, in general,
     * change the coordinate values, so it prevents the pair of SphMaps
     * being simplified.
     * <h4>Notes</h4>
     * <br> - This attribute is intended mainly for use when SphMaps are
     * involved in a sequence of Mappings which project (e.g.) a
     * dataset on to the celestial sphere. By regarding the celestial
     * sphere as a unit sphere (and setting UnitRadius to be non-zero)
     * it becomes possible to cancel the SphMaps present, along with
     * associated sky projections, when two datasets are aligned using
     * celestial coordinates. This often considerably improves
     * performance.
     * <br> - Such a situations often arises when interpreting FITS data and
     * is handled automatically by the FitsChan class.
     * <br> - The value of the UnitRadius attribute is used only to control
     * the simplification of Mappings and has no effect on the value of
     * the coordinates transformed by a SphMap. The lengths of the
     * input 3-dimensional Cartesian vectors supplied are always
     * ignored, even if UnitRadius is non-zero.
     * 
     *
     * @param  unitRadius   the UnitRadius attribute of this object
     */
    public void setUnitRadius( boolean unitRadius ) {
       setB( "UnitRadius", unitRadius );
    }

    /**
     * Get 
     * the longitude value to assign to either pole.  
     * This attribute holds the longitude value, in radians, to be
     * returned when a Cartesian position corresponding to either the north
     * or south pole is transformed into spherical coordinates. The
     * default value is zero.
     * 
     *
     * @return  this object's PolarLong attribute
     */
    public double getPolarLong() {
        return getD( "PolarLong" );
    }

    /**
     * Set 
     * the longitude value to assign to either pole.  
     * This attribute holds the longitude value, in radians, to be
     * returned when a Cartesian position corresponding to either the north
     * or south pole is transformed into spherical coordinates. The
     * default value is zero.
     * 
     *
     * @param  polarLong   the PolarLong attribute of this object
     */
    public void setPolarLong( double polarLong ) {
       setD( "PolarLong", polarLong );
    }

}
