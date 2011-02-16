package uk.ac.starlink.ttools.task;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableFactory;
import uk.ac.starlink.task.BooleanParameter;
import uk.ac.starlink.task.Environment;
import uk.ac.starlink.task.IntegerParameter;
import uk.ac.starlink.task.Parameter;
import uk.ac.starlink.task.ParameterValueException;
import uk.ac.starlink.task.TaskException;
import uk.ac.starlink.vo.TapQuery;

/**
 * Performs a TAP query.
 *
 * @author   Mark Taylor
 * @since    16 Feb 2011
 */
public class TapQuerier extends ConsumerTask {

    private final Parameter urlParam_;
    private final Parameter adqlParam_;
    private final IntegerParameter pollParam_;
    private final BooleanParameter deleteParam_;
    private final Logger logger_ =
        Logger.getLogger( "uk.ac.starlink.ttools.task" );

    /**
     * Constructor.
     */
    public TapQuerier() {
        super( "Queries a Table Access Protocol server", new ChoiceMode(),
               true );
        List paramList = new ArrayList();

        urlParam_ = new Parameter( "tapurl" );
        urlParam_.setPrompt( "Base URL of TAP service" );
        urlParam_.setDescription( new String[] {
            "<p>The base URL of a Table Access Protocol service.",
            "This is the bare URL without a trailing \"/async\".",
            "</p>",
        } );
        paramList.add( urlParam_ );

        adqlParam_ = new Parameter( "adql" );
        adqlParam_.setPrompt( "ADQL query text" );
        adqlParam_.setDescription( new String[] {
            "<p>Astronomical Data Query Language string specifying the",
            "TAP query to execute.",
            "ADQL/S resembles SQL, so this string will likely start with",
            "\"SELECT\".",
            "</p>",
        } );
        paramList.add( adqlParam_ );

        pollParam_ = new IntegerParameter( "pollmillis" );
        pollParam_.setPrompt( "Polling interval in milliseconds" );
        int minPoll = 50;
        pollParam_.setMinimum( minPoll );
        pollParam_.setDescription( new String[] {
            "<p>Interval to wait between polling attempts, in milliseconds.",
            "Asynchronous TAP queries can only find out when they are",
            "complete by repeatedly polling the server to find out the",
            "job's status.  This parameter allows you to set how often",
            "that happens.",
            "Attempts to set it too low (&lt;" + minPoll + ")",
            "will be rejected on the assumption that you're thinking in",
            "seconds.",
            "</p>",
        } );
        pollParam_.setMinimum( 50 );
        pollParam_.setDefault( "5000" );
        paramList.add( pollParam_ );

        deleteParam_ = new BooleanParameter( "delete" );
        deleteParam_.setPrompt( "Delete job when complete?" );
        deleteParam_.setDescription( new String[] {
            "<p>If true, the UWS job is deleted when complete.",
            "If false, the job is left on the server, and it can be",
            "access via the normal UWS REST endpoints after the completion",
            "of this command.",
            "</p>",
        } );
        deleteParam_.setDefault( "true" );
        paramList.add( deleteParam_ );

        getParameterList().addAll( 0, paramList );
    }

    public TableProducer createProducer( Environment env )
            throws TaskException {
        String urlText = urlParam_.stringValue( env );
        final URL url;
        try {
            url = new URL( urlText );
        }
        catch ( MalformedURLException e ) {
            throw new ParameterValueException( urlParam_, "Bad URL: " + urlText,
                                               e );
        }
        final String adql = adqlParam_.stringValue( env );
        final int pollMillis = pollParam_.intValue( env );
        final boolean delete = deleteParam_.booleanValue( env );
        final StarTableFactory tfact =
            LineTableEnvironment.getTableFactory( env );
        return new TableProducer() {
            public StarTable getTable() throws IOException {
                TapQuery query = TapQuery.createAdqlQuery( url, adql );
                try {
                    return query.execute( tfact, pollMillis, delete );
                }
                catch ( InterruptedException e ) {
                    throw (IOException)
                          new InterruptedIOException( "Interrupted" )
                         .initCause( e );
                }
                finally {
                    if ( ! delete ) {
                        logger_.warning( "UWS job "
                                       + query.getUwsJob().getJobUrl()
                                       + " not deleted" );
                    }
                }
            }
        };
    }
}
