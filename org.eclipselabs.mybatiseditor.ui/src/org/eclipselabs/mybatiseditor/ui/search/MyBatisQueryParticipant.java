package org.eclipselabs.mybatiseditor.ui.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.search.ui.text.Match;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisDomReader;

@SuppressWarnings("restriction")
public class MyBatisQueryParticipant implements IQueryParticipant {

    @Override
    public void search(ISearchRequestor requestor, QuerySpecification querySpecification, IProgressMonitor monitor)
            throws CoreException {
        if (!(querySpecification instanceof ElementQuerySpecification)) {
            return;
        }
        ElementQuerySpecification elementQspec = (ElementQuerySpecification) querySpecification;
        int limitTo = elementQspec.getLimitTo();
        if (limitTo == IJavaSearchConstants.ALL_OCCURRENCES || limitTo == IJavaSearchConstants.REFERENCES
                || limitTo == IJavaSearchConstants.IMPLEMENTORS) {
            IJavaElement element = elementQspec.getElement();
            MyBatisDomReader reader = new MyBatisDomReader();
            IFile file = reader.getRelatedMyBatisMapperFile(element);
            if (file != null) {
                IDOMNode statement = reader.findSqlStatement(file, element.getElementName());
                if (statement != null) {
                    requestor.reportMatch(new Match(file, statement.getStartOffset(), statement.getEndOffset()
                            - statement.getStartOffset()));
                }
            }
        }
    }

    @Override
    public int estimateTicks(QuerySpecification specification) {
        return 0;
    }

    @Override
    public IMatchPresentation getUIParticipant() {
        // Only report to files, so null is enough
        return null;
    }
}
