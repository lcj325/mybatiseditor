package org.eclipselabs.mybatiseditor.ui.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipselabs.mybatiseditor.ui.MyBatisEditorUiLogger;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisDomReader;
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
        MyBatisDomReader reader = new MyBatisDomReader();
        IDOMNode sourceNode = reader.findRelatedAttributeNode(linkNode);
        if (sourceNode != null) {
            IResource sourceResource = reader.getResource(sourceNode);
            IRegion include = RegionUtil.getAttributeValueRegion(sourceNode);
            if (include != null) {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                IEditorPart currentEditor = page.getActiveEditor();
                IEditorPart editor;
                try {
                    editor = findTextEditor(IDE.openEditor(page, (IFile) sourceResource, true));
                    page.getNavigationHistory().markLocation(currentEditor);
                    if ((include != null) && (editor instanceof ITextEditor)) {
                        ((ITextEditor) editor).selectAndReveal(include.getOffset(), include.getLength());
                    }
                } catch (PartInitException e) {
                    MyBatisEditorUiLogger.error("Error while opening link", e);
                }
            }
        }
    }
}
