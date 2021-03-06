#!/usr/bin/perl -w 

#+
#  Name:
#     UinterpCalculator.pl

#  Purpose:
#     Generate java source code for the UinterpCalculator class.

#  Authors:
#     MBT: Mark Taylor (Starlink)

#  History:
#     21-SEP-2001 (MBT):
#        Original version.
#-

use strict;

print <<'__EOT__';

/* ******************************************************************
 *                             DO NOT EDIT!                         *
 *     This file automatically generated by UinterpCalculator.pl    *
 *                  and built under Ant control                     *
 ********************************************************************/

package uk.ac.starlink.ast;

/**
 * Abstract class for user-provided general interpolation functions.
 * An object extending this class and implementing one or more of
 * the (type-specific) <code>uinterp*</code> methods is required in
 * order to perform custom interpolation in the <code>resample*</code>
 * methods of the {@link Mapping} class.  If it is known that
 * some types will not be processed, the corresponding methods do
 * not need to be implemented.
 *
 * @author   Mark Taylor (Starlink)
 */
public abstract class UinterpCalculator {

__EOT__

my $typeset;
foreach $typeset (
   [ "D", "double", "double precision" ],
   [ "F", "float", "floating point" ],
   [ "L", "long", "long integer" ],
   [ "I", "int", "integer" ],
   [ "S", "short", "short integer" ],
   [ "B", "byte", "byte" ],
) {
   my ( $Xletter, $Xtype, $Xcomm ) = @{$typeset};
   print <<"__EOT__";
    /**
     * Interpolates an input grid of ${Xcomm} data at a specified set
     * of points. 
     *
     * \@param  ndim_in  number of dimensions in the input grid.  This will
     *                   be at least one.
     * \@param  lbnd_in  an array of integers of size <code>ndim_in</code>,
     *                   containing the coordinates of the centre of the
     *                   first pixel in the input grid along each dimension.
     * \@param  ubnd_in  an array of integers of size <code>ndim_in</code>,
     *                   containing the coordinates of the centre of the
     *                   last pixel in the grid along each dimension.
     *                   <p>
     *                   Note that <code>lbnd_in</code> and <code>ubnd_in</code>
     *                   together define the shape, size and coordinate 
     *                   system of the input grid in the same way as they 
     *                   do in <code>resample$Xletter</code>.
     * \@param  in       An array with one element for each pixel in the
     *                   input grid, containing the input data.
     *                   This will be the same array as was passed to 
     *                   <code>resample$Xletter</code> via the 
     *                   <code>in</code> parameter.
     * \@param  in_var   Optionally, an array with the same size as 
     *                   <code>in</code>.  If given, this will contain the
     *                   set of variance values associated with the input
     *                   data and will be the same array as was passed to
     *                   <code>resample$Xletter</code> via the 
     *                   <code>in_var</code> parameter.  If no variance
     *                   calculations are required it will be <code>null</code>.
     * \@param  npoint   The number of points at which the input grid is 
     *                   to be interpolated. This will be at least one. 
     * \@param  offset   An array of integers with <code>npoint</code>
     *                   elements. or each interpolation point, this will
     *                   contain the zero-based index in the <code>out</code> 
     *                   (and <code>out_var</code>) array(s) at which the 
     *                   interpolated value
     *                   (and its variance, if required) should be stored. For
     *                   example, the interpolated value for point number
     *                   <code>point</code> should be stored in 
     *                   <code>out\[offset\[point]]</code>.
     * \@param  coords   An array of <code>ndim_in</code> arrays of doubles.
     *                   Element <code>coords\[coord]</code> will point at the
     *                   first element of an array of double 
     *                   (with <code>npoint</code>
     *                   elements) which contains the values of coordinate
     *                   number <code>coord</code> for each interpolation 
     *                   point. The
     *                   value of coordinate number <code>coord</code> 
     *                   for interpolation
     *                   point number <code>point</code> is therefore given by
     *                   <code>coords\[coord]\[point]</code>.
     *                   <p>
     *                   If any interpolation point has any of its coordinates
     *                   equal to the value AstObject.AST__BAD 
     *                   then the corresponding output
     *                   data (and variance) should be set to the value given
     *                   by <code>badval</code> (see below). 
     * \@param  flags    flag object giving more details about resampling
     *                   procedure
     * \@param  badval   This will be the same value as was given via the
     *                   <code>badval</code> parameter of 
     *                   <code>resample$Xletter</code>, and will
     *                   have the same numerical type as the data being
     *                   processed (as elements of the <code>in</code> array).
     *                   It should be used to test for bad pixels in 
     *                   the input grid (but only if <code>flags.usebad</code>
     *                   is true) and for identifying bad output
     *                   values in the <code>out</code> 
     *                   (and <code>out_var</code>) array(s). 
     * \@param  out      An array into which interpolated data values should
     *                   be returned.  Note that details of the storage
     *                   order and number of dimensions of this array are not
     *                   required, since the <code>offset</code> 
     *                   array contains all
     *                   necessary information about where each returned
     *                   value should be stored. 
     *                   <p>
     *                   In general, not all elements of this array (or the
     *                   <code>out_var</code> array below)
     *                   may be used in any particular
     *                   invocation of the function. Those which are not used
     *                   should be returned unchanged. 
     * \@param  out_var  An optional array with the same size as
     *                   the <code>out</code> array, into which variance
     *                   estimates for the resampled values should be
     *                   returned. This array will only be given if the
     *                   <code>in_var</code> array has also been given. 
     *                   <p>
     *                   If given, it is addressed in exactly 
     *                   the same way (via the <code>offset</code> array)
     *                   as the <code>out</code> array. The values
     *                   returned should be estimates of the statistical
     *                   variance of the corresponding values in the
     *                   <code>out</code> array, on the assumption 
     *                   that all errors in input data
     *                   values are statistically independent and that their
     *                   variance estimates may simply be summed (with
     *                   appropriate weighting factors). 
     *                   <p>
     *                   If no output variance estimates are required,
     *                   <code>null</code> will be given. 
     *
     * \@return   the number of pixels which were set to the BAD value
     *            by the resampling operation.
     * \@throws   Exception  The method may throw an exception if any
     *                       error occurs during the calculation.
     *                       In this case, the resampling will terminate
     *                       with an exception.
     */
    public int uinterp$Xletter( 
        int ndim_in, int[] lbnd_in, int[] ubnd_in, 
        $Xtype\[] in, $Xtype\[] in_var, int npoint, int[] offset, 
        double[][] coords, ResampleFlags flags, ${Xtype} badval, 
        $Xtype\[] out, $Xtype\[] out_var 
    ) throws Exception {
        throw new UnsupportedOperationException(
           "Class does not support ${Xcomm} interpolation" );
    }

__EOT__
}


print "}\n";


# $Id$
