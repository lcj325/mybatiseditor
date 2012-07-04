package org.eclipselabs.mybatiseditor.ui.assist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.contentassist.ICompletionProposalComputer;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipselabs.mybatiseditor.ui.MyBatisEditorUiActivator;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisDomReader;
import org.eclipselabs.mybatiseditor.ui.reader.RegionUtil;
import org.w3c.dom.Node;

@SuppressWarnings("restriction")
public class MyBatisXmlCompletionProposalComputer implements ICompletionProposalComputer {

    private enum ProposalType {
        RESULTMAP
    }

    private static final class ProposalTarget {

        private final ProposalType type;

        private final IRegion region;

        public ProposalTarget(ProposalType type, IRegion region) {
            super();
            this.type = type;
            this.region = region;
        }

        public ProposalType getType() {
            return type;
        }

        public IRegion getRegion() {
            return region;
        }
    }

    @Override
    public void sessionStarted() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<CompletionProposal> computeCompletionProposals(CompletionProposalInvocationContext context, IProgressMonitor monitor) {
        ITextViewer textViewer = context.getViewer();
        int offset = context.getInvocationOffset();

        MyBatisDomReader reader = new MyBatisDomReader();
        IDOMNode node = reader.getCurrentMyBatisNode(textViewer.getDocument(), offset);

        ProposalTarget proposalTarget = determineProposalType(node, offset);
        if (proposalTarget != null) {
            List<String> proposals;
            switch (proposalTarget.getType()) {
            case RESULTMAP:
                proposals = reader.findDeclarations(node.getModel().getDocument(), "resultMap");
                break;
            default:
                throw new IllegalStateException();
            }
            return createProposals(proposals, proposalTarget.getRegion());
        }
        return Collections.emptyList();
    }

    private List<CompletionProposal> createProposals(List<String> proposals, IRegion region) {
        if (proposals.isEmpty()) {
            return Collections.emptyList();
        }
        List<CompletionProposal> result = new ArrayList<CompletionProposal>();
        Image logo = MyBatisEditorUiActivator.getInstance().getLogo();
        for (String proposalText : proposals) {
            CompletionProposal proposal = new CompletionProposal(proposalText, region.getOffset(), region.getLength(),
                    proposalText.length(), logo, null, null, null);
            result.add(proposal);
        }
        return result;
    }

    private ProposalTarget determineProposalType(IDOMNode node, int offset) {
        if (node == null) {
            return null;
        }

        ProposalTarget result = null;
        short nodeType = node.getNodeType();
        if (nodeType == Node.ATTRIBUTE_NODE) {
            IDOMAttr attr = (IDOMAttr) node;
            String name = attr.getName();
            if ("resultMap".equals(name)) {
                IRegion valueRegion = RegionUtil.getAttributeValueRegion(node);
                if (offsetInRegion(offset, valueRegion)) {
                    result = new ProposalTarget(ProposalType.RESULTMAP, valueRegion);
                }
            }
        }
        return result;
    }

    private boolean offsetInRegion(int offset, IRegion region) {
        if (region == null) {
            return false;
        }
        int regionOffset = region.getOffset();
        if (regionOffset > offset) {
            return false;
        }
        if (offset > regionOffset + region.getLength()) {
            return false;
        }
        return true;
    }

    @Override
    public List<IContextInformation> computeContextInformation(CompletionProposalInvocationContext context, IProgressMonitor monitor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getErrorMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sessionEnded() {
        // TODO Auto-generated method stub
    }
}
