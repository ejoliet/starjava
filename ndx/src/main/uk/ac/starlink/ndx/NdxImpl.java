package uk.ac.starlink.ndx;

import java.io.IOException;
import javax.xml.transform.Source;
import uk.ac.starlink.array.NDArray;

/**
 * Interface for the implementation end of the Ndx bridge pattern.
 * If you have an <tt>NdxImpl</tt> you can make an {@link Ndx} out of it.
 * This is the basic interface via which NDX implementations provide
 * services to the {@link BridgeNdx} class.
 * BridgeNdx is intended to be the only client of this class, and it
 * does the necessary validation of arguments before passing them to
 * NdxImpl, so that implementations of this interface can in general
 * assume that the arguments they receive make sense.
 * <p>
 * Note that <tt>BridgeNdx</tt> may cache information from the methods
 * defined here, so objects implementing this interface should be 
 * considered effectively immutable; if an instance of <tt>NdxImpl</tt>
 * changes the return value of <tt>getTitle</tt> at some point 
 * after it has been passed to the <tt>BridgeNdx</tt> constructor 
 * it is not defined which value an invocation of the 
 * <tt>BridgeNdx.getTitle</tt> method will return.
 * For this reason it is not generally worthwhile for implementations
 * of this interface to perform caching for performance reasons
 * except where noted, since most of the <tt>get</tt> methods will
 * be called only once.
 *
 * @author Mark Taylor
 * @author Peter W. Draper
 * @see   Ndx
 * @see   BridgeNdx
 */
public interface NdxImpl {

    /**
     * Returns the bad bits mask used to mask the image/variance arrays
     * against the quality array.  A value of 0 (quality has no effect)
     * should be returned if no other value is available.
     *
     * @return   the bad bits mask
     */
    byte getBadBits();

    /**
     * Indicates whether a title component is available.
     *
     * @return   true if and only if getTitle will return a string
     */
    boolean hasTitle();

    /**
     * Gets the title component.
     * This method will only be called if {@link #hasTitle} returns true.
     *
     * @return  a string containing the Ndx title
     */
    String getTitle();

    /**
     * Indicates whether a WCS component is available.
     *
     * @return   true if and only if getWCS will return a representation of
     *           the world coordinate system of this Ndx
     */
    boolean hasWCS();

    /**
     * Gets an object representing the world coordinate systems of this Ndx.
     * This may be returned in one of a number of forms; currently
     * <ul>
     * <li> {@link uk.ac.starlink.ast.FrameSet} an AST FrameSet
     * <li> {@link javax.xml.transform.Source} a &lt;wcs&gt; element holding 
     *      an XML representation of an AST FrameSet
     * </ul>
     * This method will only be called if {@link #hasWCS} returns true.
     *
     * @return   a FrameSet or Element object representing the WCS
     */
    Object getWCS();

    /**
     * Indicates whether an extensions DOM is available.
     *
     * @return  true if and only if getEtc will return an XML Source giving 
     *          extension information for this Ndx
     */
    boolean hasEtc();

    /**
     * Gets an XML Source holding the extension information.
     * This method will only be called if {@link #hasEtc} returns true.
     * The result must represent an element, or a document with a root 
     * element, whose tagname is "etc".  This method may be called more
     * than once by <tt>BridgeNdx</tt>, so it must not return a source
     * which may have been exhausted by a previous call (for instance
     * an old <tt>StreamSource</tt>).
     *
     * @return  the extension information in XML form
     */
    Source getEtc();

    /**
     * Gets an object responsible for providing the bulk data (image,
     * variance, quality) of this Ndx.
     *
     * @return  a BulkDataImpl which provides the array data
     */
    BulkDataImpl getBulkData();
}
