package org.eclipselabs.mybatiseditor.ui.view;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisDomReader;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisSqlParser;
import org.w3c.dom.Element;

@SuppressWarnings("restriction")
public class MyBatisSqlView extends ViewPart {

    private final class MyBatisSqlViewSelectionListener implements ISelectionListener {

        @Override
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {
            if (!selection.isEmpty() && (selection instanceof IStructuredSelection)) {
                IStructuredSelection sel = (IStructuredSelection) selection;
                if (sel.size() == 1) {
                    Object firstElement = sel.getFirstElement();
                    MyBatisDomReader reader = new MyBatisDomReader();
                    if ((firstElement instanceof ElementImpl) && (selection instanceof ITextSelection)) {
                        ElementImpl selectedElement = (ElementImpl) firstElement;
                        ITextSelection textSel = (ITextSelection) selection;
                        firstElement =
                                reader.getCurrentMyBatisNode(selectedElement.getStructuredDocument(),
                                        textSel.getOffset());
                    }
                    if (firstElement instanceof AttrImpl) {
                        AttrImpl attr = (AttrImpl) firstElement;
                        Element ownerElement = attr.getOwnerElement();
                        if (ownerElement instanceof ElementImpl) {
                            ElementImpl element = (ElementImpl) ownerElement;
                            String newText =
                                    new MyBatisSqlParser().getSqlText(element.getStructuredDocument(), element.getLocalName(),
                                            attr.getNodeValue());
                            if ((newText != null) && !newText.equals(text.getText())) {
                                text.setText(newText);
                            }
                        }
                    }
                }
            }
        }
    }

    private class FontPropertyChangeListener implements IPropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (JFaceResources.TEXT_FONT.equals(event.getProperty())) {
                setTextFont();
            }
        }
    }

    private FontPropertyChangeListener fontListener;

    private MyBatisSqlViewSelectionListener selectionListener;

    protected Text text;

    @Override
    public void createPartControl(Composite parent) {
        selectionListener = new MyBatisSqlViewSelectionListener();
        getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(selectionListener);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new FillLayout());
        text = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
        text.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        setTextFont();

        fontListener = new FontPropertyChangeListener();
        JFaceResources.getFontRegistry().addListener(fontListener);
    }

    protected void setTextFont() {
        text.setFont(JFaceResources.getTextFont());
    }

    @Override
    public void dispose() {
        if (selectionListener != null) {
            getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(selectionListener);
            selectionListener = null;
        }
        if (fontListener != null) {
            JFaceResources.getFontRegistry().addListener(fontListener);
            fontListener = null;
        }
        super.dispose();
    }

    @Override
    public void setFocus() {
    }

}
