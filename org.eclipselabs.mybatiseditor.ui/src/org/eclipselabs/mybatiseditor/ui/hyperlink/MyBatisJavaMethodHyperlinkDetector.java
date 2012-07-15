package org.eclipselabs.mybatiseditor.ui.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipselabs.mybatiseditor.ui.MyBatisEditorUiLogger;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisDomReader;

@SuppressWarnings("restriction")
public class MyBatisJavaMethodHyperlinkDetector extends AbstractHyperlinkDetector {

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
        ITextEditor textEditor = (ITextEditor) getAdapter(ITextEditor.class);
        try {
            IJavaElement element = getSelectedJavaElement(region, textEditor);
            MyBatisDomReader reader = new MyBatisDomReader();
            IFile file = reader.getRelatedMyBatisMapperFile(element);
            if (file != null) {
                IDOMNode statement = reader.findSqlStatement(file, element.getElementName());
                if (statement != null) {
                    IRegion wordRegion = JavaWordFinder.findWord(textViewer.getDocument(), region.getOffset());
                    return new IHyperlink[] { new MyBatisStatementHyperlink(wordRegion, file, element.getElementName(), statement) };
                }
            }
        } catch (JavaModelException e) {
            MyBatisEditorUiLogger.error("Error while inspecting Java model", e);
        }
        // Note that Eclipse API requires either a non-empty array or null,
        // zero-length array is not allowed...
        return null;
    }

    private IJavaElement getSelectedJavaElement(IRegion region, ITextEditor textEditor) throws JavaModelException {
        if (textEditor == null) {
            return null;
        }
        ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(textEditor.getEditorInput());
        IJavaElement[] select = typeRoot.codeSelect(region.getOffset(), region.getLength());
        IJavaElement element;
        if ((select != null) && (select.length == 1)) {
            element = select[0];
        } else {
            element = null;
        }
        return element;
    }

}
