package org.eclipselabs.mybatiseditor.ui.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisDomReader;
import org.eclipselabs.mybatiseditor.ui.reader.RegionUtil;
import org.w3c.dom.Node;

@SuppressWarnings("restriction")
public class MyBatisXmlHyperlinkDetector extends AbstractHyperlinkDetector {

    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
        if (region == null || textViewer == null) {
            return null;
        }
        IDOMNode node = new MyBatisDomReader().getCurrentMyBatisNode(textViewer.getDocument(), region.getOffset());

        if (!isLinkable(node)) {
            return null;
        }
        IRegion hyperlinkRegion = getHyperlinkRegion(node);
        IHyperlink hyperlink = new MyBatisXmlHyperlink(hyperlinkRegion, (IDOMAttr) node);
        return new IHyperlink[] { hyperlink };
    }

    private boolean isLinkable(IDOMNode node) {
        if (node == null) {
            return false;
        }

        short nodeType = node.getNodeType();
        boolean linkable = false;
        if (nodeType == Node.ATTRIBUTE_NODE) {
            IDOMAttr attr = (IDOMAttr) node;
            String name = attr.getName();
            linkable = isLinkableAttribute(name);
        }
        return linkable;
    }

    private boolean isLinkableAttribute(String name) {
        return "refid".equals(name) || "resultMap".equals(name) || "parameterMap".equals(name)
                || "extends".equals(name);
    }

    protected IRegion getHyperlinkRegion(IDOMNode node) {
        if (node == null) {
            return null;
        }
        if (Node.ATTRIBUTE_NODE == node.getNodeType()) {
            return RegionUtil.getAttributeValueRegion(node);
        }
        return null;
    }
}