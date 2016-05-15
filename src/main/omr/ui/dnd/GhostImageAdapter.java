//----------------------------------------------------------------------------//
//                                                                            //
//                     G h o s t I m a g e A d a p t e r                      //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright © Hervé Bitteur and others 2000-2013. All rights reserved.      //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.ui.dnd;

import java.awt.image.BufferedImage;

/**
 * Class {@code GhostImageAdapter}is a {@link GhostDropAdapter} with
 * a provided image.
 *
 * @param <A> The precise type of action carried by the drop
 *
 * @author Hervé Bitteur (from Romain Guy's demo)
 */
public class GhostImageAdapter<A>
        extends GhostDropAdapter<A>
{
    //~ Constructors -----------------------------------------------------------

    /**
     * Create a new GhostImageAdapter object
     *
     * @param glassPane the related glasspane
     * @param action    the carried action
     * @param image     the provided image
     */
    //-------------------//
    // GhostImageAdapter //
    //-------------------//
    public GhostImageAdapter (GhostGlassPane glassPane,
                              A action,
                              BufferedImage image)
    {
        super(glassPane, action);

        this.image = image;
    }
}
