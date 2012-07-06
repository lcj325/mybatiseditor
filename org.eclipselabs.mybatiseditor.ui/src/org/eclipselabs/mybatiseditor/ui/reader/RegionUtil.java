package org.eclipselabs.mybatiseditor.ui.reader;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.w3c.dom.Node;

@SuppressWarnings("restriction")
public final class RegionUtil {

    private RegionUtil() {
        //
    }

    public static IRegion getAttributeValueRegion(Node node) {
        if (node == null) {
            return null;
        }
        IDOMAttr att = (IDOMAttr) node;

        int regOffset = att.getValueRegionStartOffset();

        // there is no alternative method for the deprecated one...
        @SuppressWarnings("deprecation")
        ITextRegion valueRegion = att.getValueRegion();
        if (valueRegion != null) {
            int regLength = valueRegion.getTextLength();
            String attValue = att.getValueRegionText();

            if (StringUtils.isQuoted(attValue)) {
                regLength = regLength - 2;
                regOffset++;
            }
            return new Region(regOffset, regLength);
        }
        return null;
    }
}
