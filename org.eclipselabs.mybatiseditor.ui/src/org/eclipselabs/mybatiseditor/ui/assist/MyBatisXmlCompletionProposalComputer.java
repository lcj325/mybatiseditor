package org.eclipselabs.mybatiseditor.ui.assist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.contentassist.ICompletionProposalComputer;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipselabs.mybatiseditor.ui.MyBatisEditorUiActivator;
import org.eclipselabs.mybatiseditor.ui.MyBatisEditorUiLogger;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisDomReader;
import org.eclipselabs.mybatiseditor.ui.reader.MyBatisJavaUtil;
import org.eclipselabs.mybatiseditor.ui.reader.RegionUtil;
import org.eclipselabs.mybatiseditor.ui.reader.XmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@SuppressWarnings("restriction")
public class MyBatisXmlCompletionProposalComputer implements ICompletionProposalComputer {

    private enum ProposalType {
        RESULTMAP, INCLUDE, PARAMMAP, JAVAPROPERTY, JAVATYPE
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
    public List<ICompletionProposal> computeCompletionProposals(CompletionProposalInvocationContext context,
            IProgressMonitor monitor) {
        int offset = context.getInvocationOffset();

        MyBatisDomReader reader = new MyBatisDomReader();
        IDOMNode node = reader.getCurrentMyBatisNode(context.getDocument(), offset);

        ProposalTarget proposalTarget = determineProposalTarget(node, offset);
        if (proposalTarget != null) {
            try {
                if (proposalTarget.getType().equals(ProposalType.JAVATYPE)) {
                    return createJavaTypeProposals(node);
                }
                List<String> proposals;
                if (proposalTarget.getType().equals(ProposalType.JAVAPROPERTY)) {
                    proposals = findJavaElementProposals(node);
                } else {
                    String searchElement;
                    switch (proposalTarget.getType()) {
                    case RESULTMAP:
                        searchElement = "resultMap";
                        break;
                    case INCLUDE:
                        searchElement = "sql";
                        break;
                    case PARAMMAP:
                        searchElement = "parameterMap";
                        break;
                    default:
                        throw new IllegalStateException("Undefined enum value");
                    }
                    proposals = reader.findDeclarations(node.getModel().getDocument(), searchElement);
                }
                return createProposals(filterProposals(node, proposals), proposalTarget.getRegion());
            } catch (CoreException e) {
                MyBatisEditorUiLogger.error("Error while inspecting " + node.getNodeValue(), e);
            }
        }
        return Collections.emptyList();
    }

    private List<String> filterProposals(IDOMNode node, List<String> proposals) {
        String filterText = node.getNodeValue().trim();
        if (filterText.trim().isEmpty()) {
            return proposals;
        }
        List<String> filtered = new ArrayList<String>(proposals.size());
        int filterLength = filterText.length();
        for (String proposal : proposals) {
            if ((proposal.length() >= filterLength) && proposal.substring(0, filterLength).equalsIgnoreCase(filterText)) {
                filtered.add(proposal);
            }
        }
        return filtered;
    }

    private List<ICompletionProposal> createJavaTypeProposals(IDOMNode node) {
        return Collections.emptyList();
    }

    private List<String> findJavaElementProposals(IDOMNode node) throws CoreException {
        List<String> result = Collections.emptyList();
        if ((node != null) && (node.getNodeType() == Node.ATTRIBUTE_NODE)) {
            IDOMAttr attr = (IDOMAttr) node;
            Element ownerElement = attr.getOwnerElement();
            if (ownerElement != null) {
                String ownerName = ownerElement.getNodeName();
                if ("id".equals(ownerName) || "result".equals(ownerName) || "collection".equals(ownerName)
                        || "association".equals(ownerName)) {
                    Node resultMapNode = ownerElement.getParentNode();
                    if (resultMapNode != null) {
                        String javaTypeName = XmlUtil.getAttributeValue(resultMapNode, "type");
                        if (javaTypeName == null) {
                            // iBatis 2 fallback
                            javaTypeName = XmlUtil.getAttributeValue(resultMapNode, "class");
                        }
                        if (javaTypeName != null) {
                            MyBatisDomReader reader = new MyBatisDomReader();
                            IFile xmlResource = reader.getResource(node);
                            IType javaType = MyBatisJavaUtil.findJavaType(xmlResource.getProject(), javaTypeName);
                            if (javaType != null) {
                                result = findJavaElementProposals(javaType);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<String> findJavaElementProposals(IType javaType) throws JavaModelException {
        List<String> result = new ArrayList<String>();
        for (IMethod method : javaType.getMethods()) {
            if (isSetter(method)) {
                result.add(toPropertyName(method.getElementName()));
            }
        }
        return result;
    }

    private String toPropertyName(String elementName) {
        return Character.toLowerCase(elementName.charAt(3)) + elementName.substring(4, elementName.length());
    }

    private boolean isSetter(IMethod method) throws JavaModelException {
        return Flags.isPublic(method.getFlags()) && "V".equals(method.getReturnType()) && (method.getParameters().length == 1)
                && method.getElementName().startsWith("set");
    }

    private List<ICompletionProposal> createProposals(List<String> proposals, IRegion region) {
        if (proposals.isEmpty()) {
            return Collections.emptyList();
        }
        List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
        Image logo = MyBatisEditorUiActivator.getInstance().getLogo();
        for (String proposalText : proposals) {
            CompletionProposal proposal = new CompletionProposal(proposalText, region.getOffset(), region.getLength(),
                    proposalText.length(), logo, null, null, null);
            result.add(proposal);
        }
        return result;
    }

    private ProposalTarget determineProposalTarget(IDOMNode node, int offset) {
        ProposalTarget result = null;
        if ((node != null) && (node.getNodeType() == Node.ATTRIBUTE_NODE)) {
            IDOMAttr attr = (IDOMAttr) node;
            ProposalType type = determineProposalType(attr.getName());
            if (type != null) {
                IRegion valueRegion = RegionUtil.getAttributeValueRegion(node);
                if (offsetInRegion(offset, valueRegion)) {
                    result = new ProposalTarget(type, valueRegion);
                }
            }
        }
        return result;
    }

    private ProposalType determineProposalType(String attrName) {
        if ("resultMap".equals(attrName)) {
            return ProposalType.RESULTMAP;
        }
        if ("refid".equals(attrName)) {
            return ProposalType.INCLUDE;
        }
        if ("parameterMap".equals(attrName)) {
            return ProposalType.PARAMMAP;
        }
        if ("property".equals(attrName)) {
            return ProposalType.JAVAPROPERTY;
        }
        if ("type".equals(attrName) || "class".equals(attrName) || "parameterClass".equals(attrName)
                || "resultClass".equals(attrName) || "parameterType".equals(attrName) || "resultType".equals(attrName)) {
            return ProposalType.JAVATYPE;
        }
        return null;
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
        return null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public void sessionStarted() {
        // Do nothing
    }

    @Override
    public void sessionEnded() {
        // Do nothing
    }
}
