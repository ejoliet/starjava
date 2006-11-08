/*
 * Copyright (C) 2003 Central Laboratory of the Research Councils
 *
 *  History:
 *     27-FEB-2003 (Peter W. Draper):
 *       Original version.
 */
package uk.ac.starlink.splat.iface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import uk.ac.starlink.splat.util.Utilities;
import uk.ac.starlink.splat.iface.images.ImageHolder;

/**
 * Frame for displaying a CoordinateGenerator implementation.
 *
 * @author Peter W. Draper
 * @version $Id$
 */
public class CoordinateGeneratorFrame
    extends JFrame
{
    /**
     * The CoordinateGenerator that we're hosting.
     */
    protected CoordinateGenerator coordinateGenerator = null;

    /**
     * Menubar and various menus and items that it contains.
     */
    protected JMenuBar menuBar = new JMenuBar();
    
    /**
     * Create an instance.
     *
     * @param coordinateGenerator the CoordinateGenerator instance.
     */
    public CoordinateGeneratorFrame( CoordinateGenerator coordinateGenerator )
    {
        this.coordinateGenerator = coordinateGenerator;
        initUI();
        // XXX next time do help for this window.
        //HelpFrame.createHelpMenu( "coordinate-generator", "Help on window",
        //                          menuBar, null );
        setSize( new Dimension( 450, 400 ) );
        setTitle( Utilities.getTitle( "Modify/generate coordinates" ) );
        setVisible( true );
    }

    /**
     * Initialise the user interface.
     */
    protected void initUI()
    {
        // Add an action to close the window (appears in File menu
        // and action bar).
        ImageIcon image =
            new ImageIcon( ImageHolder.class.getResource( "close.gif" ) );
        CloseAction closeAction = new CloseAction( "Close", image,
                                                   "Close window" );
        JButton closeButton = new JButton( closeAction );

        // Add an action to apply the current generator.
        image = new ImageIcon( ImageHolder.class.getResource( "accept.gif" ) );
        ApplyAction applyAction = new ApplyAction( "Apply", image,
                                                   "Apply current generator" );
        JButton applyButton = new JButton( applyAction );

        JPanel windowActionBar = new JPanel();
        windowActionBar.setLayout( new BoxLayout( windowActionBar,
                                                  BoxLayout.X_AXIS ) );
        windowActionBar.setBorder( BorderFactory.createEmptyBorder(3,3,3,3) );
        windowActionBar.add( Box.createGlue() );
        windowActionBar.add( applyButton );
        windowActionBar.add( Box.createGlue() );
        windowActionBar.add( closeButton );
        windowActionBar.add( Box.createGlue() );

        // Set the the menuBar.
        setJMenuBar( menuBar );

        // Create and populate the File menu.
        JMenu fileMenu = new JMenu( "File" );
        fileMenu.setMnemonic( KeyEvent.VK_F );
        menuBar.add( fileMenu );
        fileMenu.add( closeAction );

        //  Menu of useful pre-defined functions.
        JMenu functions = new JMenu( "Functions" );
        functions.setMnemonic( KeyEvent.VK_U );
        menuBar.add( functions );
        coordinateGenerator.addPreDefined( functions );

        //  Finally add components to main window.
        getContentPane().add( coordinateGenerator, BorderLayout.CENTER );
        getContentPane().add( windowActionBar, BorderLayout.SOUTH );
    }

    /**
     *  Close the window.
     */
    protected void closeWindow()
    {
        this.dispose();
    }

    /**
     * Inner class defining Action for closing a window.
     */
    protected class CloseAction extends AbstractAction
    {
        public CloseAction( String name, Icon icon, String shortHelp )
        {
            super( name, icon  );
            putValue( SHORT_DESCRIPTION, shortHelp );
            putValue( MNEMONIC_KEY, new Integer( KeyEvent.VK_C ) );
            putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( "control W" ) );
        }

        /**
         * Respond to actions from the buttons.
         */
        public void actionPerformed( ActionEvent ae )
        {
            closeWindow();
        }
    }

    /**
     * Inner class defining Action for applying the current generator,
     */
    protected class ApplyAction extends AbstractAction
    {
        public ApplyAction( String name, Icon icon, String shortHelp )
        {
            super( name, icon  );
            putValue( SHORT_DESCRIPTION, shortHelp );
        }

        /**
         * Respond to actions from the buttons.
         */
        public void actionPerformed( ActionEvent ae )
        {
            coordinateGenerator.generate();
        }
    }
}
