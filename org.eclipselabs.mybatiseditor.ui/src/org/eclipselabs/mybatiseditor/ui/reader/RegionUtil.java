package org.eclipselabs.mybatiseditor.ui.reader;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;

@SuppressWarnings("restriction")
public final class RegionUtil {

    private RegionUtil() {
        // No need to instantiate
    }

    public static IRegion getAttributeValueRegion(IDOMAttr att) {
        if (att == null) {
            return null;
        }
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
