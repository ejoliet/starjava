package uk.ac.starlink.table.join;

import uk.ac.starlink.table.DefaultValueInfo;
import uk.ac.starlink.table.ValueInfo;

/**
 * Match engine which works with tuples representing RA, Dec and range.
 * Each tuple must be a 3-element array of {@link java.lang.Number} objects:
 * first element is Right Ascension in radians,
 * second element is Declination in radians,
 * third element is range (units are arbitrary, but will be the same as
 * the error supplied in the constructor).
 *
 * @author   Mark Taylor (Starlink)
 */
public class SphericalPolarMatchEngine extends IsotropicCartesianMatchEngine {

    private Double[] work0_ = new Double[ 3 ];
    private Double[] work1_ = new Double[ 3 ];
    private Double[] work2_ = new Double[ 3 ];

    private static final DefaultValueInfo RA_INFO =
        new DefaultValueInfo( "RA", Number.class, "Right Ascension" );
    private static final DefaultValueInfo DEC_INFO =
        new DefaultValueInfo( "Dec", Number.class, "Declination" );
    private static final DefaultValueInfo R_INFO =
        new DefaultValueInfo( "Radius", Number.class, "Distance from Origin" );
    static {
        RA_INFO.setUnitString( "radians" );
        DEC_INFO.setUnitString( "radians" );
        RA_INFO.setNullable( false );
        DEC_INFO.setNullable( false );
        R_INFO.setNullable( false );
        RA_INFO.setUCD( "POS_EQ_RA" );
        DEC_INFO.setUCD( "POS_EQ_DEC" );
    }

    /**
     * Constructs a new match engine which will match on differences
     * not greater than a given number <tt>err</tt>, in the same units 
     * that the range part of the tuples is specified.
     * 
     * @param   err  maximum separation for a match
     */
    public SphericalPolarMatchEngine( double err ) {
        super( 3, err, false );
        ((DefaultValueInfo) errorParam_.getInfo())
                           .setUnitString( "Units of radius" );
    }

    public double matchScore( Object[] tuple1, Object[] tuple2 ) {
        polarToCartesian( tuple1, work1_ );
        polarToCartesian( tuple2, work2_ );
        return super.matchScore( work1_, work2_ );
    }

    public Object[] getBins( Object[] tuple ) {
        if ( tuple[ 0 ] instanceof Number &&
             tuple[ 1 ] instanceof Number &&
             tuple[ 2 ] instanceof Number ) {
            polarToCartesian( tuple, work0_ );
            return super.getBins( work0_ );
        }
        else {
            return NO_BINS;
        }
    }

    public ValueInfo[] getTupleInfos() {
        return new ValueInfo[] { RA_INFO, DEC_INFO, R_INFO };
    }

    public String toString() {
        return "Spherical Polar";
    }

    /**
     * Converts spherical polar coordinates to Cartesian ones.
     *
     * @param  polar  array of Numbers specified as input: ra, dec, range
     * @param  cartesian  array filled with Doubles as output: x, y, z
     */
    private static void polarToCartesian( Object[] polar, Object[] cartesian ) {
        double ra = ((Number) polar[ 0 ]).doubleValue();
        double dec = ((Number) polar[ 1 ]).doubleValue();
        double r = ((Number) polar[ 2 ]).doubleValue();

        double cd = Math.cos( dec );
        double sd = Math.sin( dec );
        double cr = Math.cos( ra );
        double sr = Math.sin( ra );

        double x = r * cr * cd;
        double y = r * sr * cd;
        double z = r * sd;

        cartesian[ 0 ] = new Double( x );
        cartesian[ 1 ] = new Double( y );
        cartesian[ 2 ] = new Double( z );
    }
}
