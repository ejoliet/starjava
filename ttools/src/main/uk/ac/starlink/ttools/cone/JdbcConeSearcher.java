package uk.ac.starlink.ttools.cone;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.DefaultValueInfo;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.ValueInfo;
import uk.ac.starlink.table.jdbc.SequentialResultSetStarTable;
import uk.ac.starlink.ttools.filter.AddColumnsTable;

/**
 * ConeSearcher implementation using JDBC access to an SQL database.
 *
 * @author   Mark Taylor
 * @since    5 Nov 2007
 */
public class JdbcConeSearcher implements ConeSearcher {

    private final ConeSelector selector_;
    private final String raCol_;
    private final String decCol_;
    private final AngleUnits units_;
    private final String tileCol_;
    private final SkyTiling tiling_;
    private boolean first_ = true;
    private int raIndex_ = -1;
    private int decIndex_ = -1;
    private int raRsetIndex_ = -1;
    private int decRsetIndex_ = -1;
    private int tileRsetIndex_ = -1;

    private static final ValueInfo RADEG_INFO =
        new DefaultValueInfo( "RA_DEGREES", Double.class,
                              "Right ascension in degrees" );
    private static final ValueInfo DECDEG_INFO =
        new DefaultValueInfo( "DEC_DEGREES", Double.class,
                              "Declination in degrees" );
    static {
        ((DefaultValueInfo) RADEG_INFO).setUnitString( "deg" );
        ((DefaultValueInfo) DECDEG_INFO).setUnitString( "deg" );
    }
    private static final Logger logger_ =
        Logger.getLogger( "uk.ac.starlink.ttools.cone" );

    /**
     * Constructor.
     *
     * @param  connection   live connection to database
     * @param  tableName  name of a table in the database to search
     * @param  raCol  name of table column containing right ascension
     * @param  decCol name of table column containing declination
     * @param  units  angular units used by ra and dec columns
     * @param  tileCol column containing a sky tiling index value, or null
     * @param  tiling tiling scheme used by tileCol column
     * @param  cols   list of column names for the SELECT statement
     * @param  where  additional WHERE clause constraints
     * @param  bestOnly  true iff only the closest match is required (hint)
     */
    JdbcConeSearcher( Connection connection, String tableName,
                      String raCol, String decCol, AngleUnits units,
                      String tileCol, SkyTiling tiling,
                      String cols, String where, boolean bestOnly )
            throws SQLException {
        raCol_ = raCol;
        decCol_ = decCol;
        units_ = units;
        tileCol_ = tileCol;
        tiling_ = tiling;
        selector_ = ( tiling != null && tileCol != null )
               ? ConeSelector.createTiledSelector( connection, tableName, raCol,
                                                   decCol, units, cols, where,
                                                   tileCol, tiling, true )
               : ConeSelector.createSelector( connection, tableName, raCol,
                                              decCol, units, cols, where,
                                              true );
    }

    public StarTable performSearch( double ra, double dec, double sr )
            throws IOException {

        /* Execute the statement and turn it into a StarTable. */
        ResultSet rset;
        try {
            rset = selector_.executeQuery( ra, dec, sr );
        }
        catch ( SQLException e ) {
            throw (IOException)
                  new IOException( "Error executing SQL statement: "
                                 + e.getMessage() )
                 .initCause( e );
        }
        StarTable rsetTable;
        try {
            rsetTable = new SequentialResultSetStarTable( rset );
        }
        catch ( SQLException e ) {
            throw (IOException)
                  new IOException( "Error retrieving data from SQL "
                                 + "statement: " + e.getMessage() )
                 .initCause( e );
        }
        int ncolRset = rsetTable.getColumnCount();

        /* Identify the columns containing RA and Dec first time around
         * (it should be the same for every query, so we only do it once). */
        boolean convertAngles = ! AngleUnits.DEGREES.equals( units_ );
        if ( first_ ) {
            first_ = false;
            try {
                raRsetIndex_ = rset.findColumn( raCol_ ) - 1;
                decRsetIndex_ = rset.findColumn( decCol_ ) - 1;
                if ( convertAngles ) {
                    raIndex_ = ncolRset;
                    decIndex_ = ncolRset + 1;
                }
                else {
                    raIndex_ = raRsetIndex_;
                    decIndex_ = decRsetIndex_;
                }
            }
            catch ( SQLException e ) {
                logger_.warning( "Cannot identify ra/dec columns" );
                raRsetIndex_ = -1;
                decRsetIndex_ = -1;
                raIndex_ = -1;
                decIndex_ = -1;
            }
            if ( tileCol_ != null ) {
                try {
                    tileRsetIndex_ = rset.findColumn( tileCol_ ) - 1;
                }
                catch ( SQLException e ) {
                    logger_.warning( "Cannot identify tile column" );
                    tileRsetIndex_ = -1;
                }
            }
        }

        /* Doctor the output table: if the angles are not in degrees as
         * supplied, append columns which are in degrees, since they are
         * required by the interface. */
        int[] inColIndices = new int[ ncolRset ];
        for ( int i = 0; i < ncolRset; i++ ) {
            inColIndices[ i ] = i;
        }
        List outInfoList = new ArrayList();
        final double angleFactor =
            AngleUnits.DEGREES.getCircle() / units_.getCircle();
        final boolean addDegCols =
            convertAngles && raRsetIndex_ >= 0 && decRsetIndex_ >= 0;
        if ( addDegCols ) {
            outInfoList.add( new ColumnInfo( RADEG_INFO ) );
            outInfoList.add( new ColumnInfo( DECDEG_INFO ) );
        }
        ColumnInfo[] outInfos =
            (ColumnInfo[]) outInfoList.toArray( new ColumnInfo[ 0 ] );
        StarTable result = new AddColumnsTable( rsetTable, inColIndices,
                                                outInfos, ncolRset ) {
            protected Object[] calculateValues( Object[] inValues ) {
                List calcValues = new ArrayList();

                /* Work out position in degrees. */
                Object ra = inValues[ raRsetIndex_ ];
                Object dec = inValues[ decRsetIndex_ ];
                double raDeg;
                double decDeg;
                if ( ra instanceof Number && dec instanceof Number ) {
                    raDeg = ((Number) ra).doubleValue() * angleFactor;
                    decDeg = ((Number) dec).doubleValue() * angleFactor;
                }
                else {
                    raDeg = Double.NaN;
                    decDeg = Double.NaN;
                }

                /* If necessary, prepare additional column values containing
                 * position in degrees. */
                if ( addDegCols ) {
                    calcValues.add( new Double( raDeg ) );
                    calcValues.add( new Double( decDeg ) );
                }

                /* If using tiles, do an assertion test on the value of this
                 * one. */
                if ( tileRsetIndex_ >= 0 ) {
                    long gotTile =
                        ((Number) inValues[ tileRsetIndex_ ]).longValue();
                    long calcTile = tiling_.getPositionTile( raDeg, decDeg );
                    if ( gotTile != calcTile ) {
                        logger_.warning( "Tiling equivalence fails: "
                                       + calcTile + " != " + gotTile );
                    }
                }

                /* Return additional column values. */
                return calcValues.toArray();
            }
        };

        /* Return the result table. */
        return result;
    }

    public int getRaIndex( StarTable result ) {
        return raIndex_;
    }

    public int getDecIndex( StarTable result ) {
        return decIndex_;
    }
}
