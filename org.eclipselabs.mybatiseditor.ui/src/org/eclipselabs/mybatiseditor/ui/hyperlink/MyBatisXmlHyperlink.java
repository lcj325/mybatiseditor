package org.eclipselabs.mybatiseditor.ui.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipselabs.mybatiseditor.ui.MyBatisEditorUiLogger;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisDomReader;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisJavaUtil;
import org.eclipselabs.mybatiseditor.ui.reader.RegionUtil;

@SuppressWarnings("restriction")
public class MyBatisXmlHyperlink extends MyBatisHyperlink {

    private final IDOMAttr linkNode;

    public MyBatisXmlHyperlink(IRegion hyperlinkRegion, IDOMAttr linkNode) {
        super(hyperlinkRegion);
        this.linkNode = linkNode;
    }

    @Override
    public String getHyperlinkText() {
        return "Go to implementation of " + linkNode;
    }

    @Override
    public void open() {
        if (isJavaReference()) {
            openJavaReference();
        } else {
            openXmlReference();
        }
    }

    private void openJavaReference() {
        MyBatisDomReader reader = new MyBatisDomReader();
        IFile xmlResource = reader.getResource(linkNode);
        String namespace = linkNode.getOwnerDocument().getDocumentElement().getAttribute("namespace");

        IJavaElement javaElement;
        try {
            javaElement = MyBatisJavaUtil.findJavaElement(xmlResource.getProject(), namespace, linkNode.getNodeValue());
            if (javaElement != null) {
                JavaUI.openInEditor(javaElement);
            }
        } catch (CoreException e) {
            MyBatisEditorUiLogger.error("Error while opening link", e);
        }
    }

    private void openXmlReference() {
        MyBatisDomReader reader = new MyBatisDomReader();
        IDOMAttr sourceNode = reader.findRelatedAttributeNode(linkNode);
        if (sourceNode != null) {
            IRegion include = RegionUtil.getAttributeValueRegion(sourceNode);
            if (include != null) {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    ITextEditor editor = findTextEditor(IDE.openEditor(page, reader.getResource(sourceNode), true));
                    if (editor != null) {
                        ((ITextEditor) editor).selectAndReveal(include.getOffset(), include.getLength());
                    }
                } catch (PartInitException e) {
                    MyBatisEditorUiLogger.error("Error while opening link", e);
                }
            }
        }
    }

    private boolean isJavaReference() {
        return "id".equals(linkNode.getName());
    }
}
