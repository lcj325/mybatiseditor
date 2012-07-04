package org.eclipselabs.mybatiseditor.ui.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class MyBatisHyperlink implements IHyperlink {

    private final IRegion hyperlinkRegion;

    public MyBatisHyperlink(IRegion hyperlinkRegion) {
        this.hyperlinkRegion = hyperlinkRegion;
    }

    @Override
    public IRegion getHyperlinkRegion() {
        return hyperlinkRegion;
    }

    @Override
    public String getTypeLabel() {
        return null;
    }

    protected ITextEditor findTextEditor(IEditorPart editorPart) {
        if (editorPart instanceof ITextEditor) {
            return (ITextEditor) editorPart;
        }
        if (editorPart instanceof MultiPageEditorPart) {
            MultiPageEditorPart multi = (MultiPageEditorPart) editorPart;
            IEditorPart[] editors = multi.findEditors(editorPart.getEditorInput());
            for (IEditorPart editor : editors) {
                return findTextEditor(editor);
            }
        }
        return null;
    }
}
