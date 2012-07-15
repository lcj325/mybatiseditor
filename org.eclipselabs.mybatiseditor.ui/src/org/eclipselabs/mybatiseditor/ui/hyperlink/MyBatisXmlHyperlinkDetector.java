package org.eclipselabs.mybatiseditor.ui.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipselabs.mybatiseditor.ui.MyBatisEditorUiLogger;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisDomReader;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisJavaUtil;
import org.eclipselabs.mybatiseditor.ui.reader.RegionUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@SuppressWarnings("restriction")
public class MyBatisXmlHyperlinkDetector extends AbstractHyperlinkDetector {

    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
        if (region == null || textViewer == null) {
            // Note that Eclipse API requires either a non-empty array or null,
            // zero-length array is not allowed...
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
            linkable = isLinkableAttribute(attr);
        }
        return linkable;
    }

    private boolean isLinkableAttribute(IDOMAttr attr) {
        String name = attr.getName();
        if ("refid".equals(name) || "resultMap".equals(name) || "parameterMap".equals(name) || "extends".equals(name)) {
            return true;
        }
        if ("id".equals(name)) {
            return isLinkableJavaQuery(attr);
        }
        return false;
    }

    private boolean isLinkableJavaQuery(IDOMAttr attr) {
        Element parentNode = attr.getOwnerElement();
        if (parentNode == null) {
            return false;
        }
        String parentName = parentNode.getNodeName();

        if (("select".equals(parentName) || "insert".equals(parentName) || "update".equals(parentName) || "delete"
                .equals(parentName))) {

            Element documentElement = parentNode.getOwnerDocument().getDocumentElement();
            if (documentElement != null) {
                String namespace = documentElement.getAttribute("namespace");
                if (namespace != null) {
                    IFile resource = new MyBatisDomReader().getResource(attr);
                    if (resource != null) {
                        try {
                            return MyBatisJavaUtil.findJavaType(resource.getProject(), namespace) != null;
                        } catch (CoreException e) {
                            MyBatisEditorUiLogger.error("Error while looking for Java type: " + namespace, e);
                        }
                    }
                }
            }
        }
        return false;
    }

    protected IRegion getHyperlinkRegion(IDOMNode node) {
        if (node instanceof IDOMAttr) {
            return RegionUtil.getAttributeValueRegion((IDOMAttr) node);
        }
        return null;
    }
}