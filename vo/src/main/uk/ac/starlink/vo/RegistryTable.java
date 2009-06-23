package uk.ac.starlink.vo;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import uk.ac.starlink.table.gui.StarJTable;

/**
 * Specialised JTable for displaying the results of a registry query 
 * (<tt>RegResource</tt> elements).
 * It installs specialised <tt>TableModel</tt> and <tt>TableColumnModel</tt>,
 * so these should not be reset.
 * It provides a number of convenience features for making sure that
 * the column widths are and stay reasonably sensible.
 *
 * @author   Mark Taylor (Starlink)
 * @since    21 Dec 2004
 */
public class RegistryTable extends JTable {

    private final ResourceTableModel tModel_;
    private final MetaColumnModel colModel_;
    private static String[] DEFAULT_COLUMNS = new String[] {
        "shortName",
        "title",
    };
  
    /**
     * Constructs a new table with a default table model.
     */
    public RegistryTable() {
        this( new ResourceTableModel() );
    }

    /**
     * Constructs a new table with a supplied table model.
     *
     * @param  tModel  table model for display of registry resources
     */
    public RegistryTable( ResourceTableModel tModel ) {
        tModel_ = tModel;
        setModel( tModel_ );

        /* Set the TableColumnModel to one which allows for columns to
         * be added and removed, and arrange for the columns to be
         * resized when new ones are added. */
        colModel_ = makeMetaColumnModel( getColumnModel(), tModel_,
                                         DEFAULT_COLUMNS );
        colModel_.addColumnModelListener( new TableColumnModelListener() {
            public void columnAdded( TableColumnModelEvent evt ) {
                StarJTable.configureColumnWidth( RegistryTable.this, 1000,
                                                 1000, evt.getToIndex() );
                reconfigureResizeMode();
                final int from = evt.getToIndex();
                final int to = colModel_.getColumnCount() - 1;
            }
            public void columnRemoved( TableColumnModelEvent evt ) {
                reconfigureResizeMode();
            }
            public void columnMarginChanged( ChangeEvent evt ) {}
            public void columnMoved( TableColumnModelEvent evt ) {}
            public void columnSelectionChanged( ListSelectionEvent evt ) {}
        } );
        setColumnModel( colModel_ );
    }

    /**
     * Sets the list of resources displayed by this table.
     *
     * @param  data table data
     */
    public void setData( RegResource[] data ) {
        tModel_.setResources( data );
        colModel_.purgeEmptyColumns();
        StarJTable.configureColumnWidths( this, 1000, 10000 );
    }

    /**
     * Returns the list of resources displayed by this table.
     *
     * @return  table data
     */
    public RegResource[] getData() {
        return tModel_.getResources();
    }

    /**
     * Returns the MetaColumnModel used for this table.  This allows
     * column reinsertion as well as deletion.
     *
     * @return  column model
     */
    public MetaColumnModel getMetaColumnModel() {
        return colModel_;
    }

    /**
     * Invoked when a new column is added; ensures that the resize mode
     * is sensible for the current set of columns.
     */
    private void reconfigureResizeMode() {
        Component holder = getParent();
        if ( holder instanceof JViewport ) {
            int cwidth = 0;
            TableColumnModel cmodel = getColumnModel();
            for ( int i = 0; i < cmodel.getColumnCount(); i++ ) {
                cwidth += cmodel.getColumn( i ).getPreferredWidth();
            }
            setAutoResizeMode( cwidth <= holder.getSize().width
                                   ? JTable.AUTO_RESIZE_ALL_COLUMNS
                                   : JTable.AUTO_RESIZE_OFF );
        }
    }

    /**
     * Creates a suitable column model.
     *
     * @param  cmodel  original table column model
     * @param  tmodel  table data model
     * @param  initcols   ordered list of column headers for the columns 
     *         which are to be visible initially
     * @return  custom column model
     */
    private static MetaColumnModel makeMetaColumnModel( TableColumnModel cmodel,
                                                        TableModel tmodel,
                                                        Object[] initcols ) {

        /* In order, move the initially visible columns to the start of the
         * list. */
        int nfix = 0;
        for ( int iv = 0; iv < initcols.length; iv++ ) {
            for ( int ic = nfix; ic < cmodel.getColumnCount(); ic++ ) {
                TableColumn col = cmodel.getColumn( ic );
                if ( initcols[ iv ].equals( col.getHeaderValue() ) ) {
                    cmodel.moveColumn( ic, nfix++ );
                    break;
                }
            }
        }

        /* Create a metacolumnmodel from this reordered model. */
        MetaColumnModel mcmodel = new MetaColumnModel( cmodel, tmodel );

        /* Hide all the columns apart from the initially visible ones. */
        for ( int ic = mcmodel.getColumnCount() - 1; ic >= nfix; ic-- ) {
            mcmodel.removeColumn( mcmodel.getColumn( ic ) );
        }
        return mcmodel;
    }
}
