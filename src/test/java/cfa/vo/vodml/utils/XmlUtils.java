/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Smithsonian Astrophysical Observatory nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package cfa.vo.vodml.utils;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;
import org.xmlunit.util.Predicate;

public class XmlUtils {

    public static void testXml(String control, String actual) {
        Diff diff = baseBuilder(control, actual).build();
        doTest(diff);
    }

    public static void testVotable(String control, String actual) {
        ElementSelector allLeaves = ElementSelectors.byXPath(".//*[not(*)]",
                ElementSelectors.and(ElementSelectors.byNameAndText, ElementSelectors.byNameAndAllAttributes));

        ElementSelector selector = ElementSelectors.conditionalBuilder()
                .whenElementIsNamed("FIELD")
                .thenUse(ElementSelectors.byNameAndAttributes("ID"))
                .whenElementIsNamed("ATTRIBUTE")
                .thenUse(ElementSelectors.and(ElementSelectors.byNameAndAttributes("dmrole"), allLeaves))
                .whenElementIsNamed("INSTANCE")
                .thenUse(allLeaves)
                .whenElementIsNamed("RESOURCE")
                .thenUse(ElementSelectors.Default)
                .whenElementIsNamed("TABLE")
                .thenUse(ElementSelectors.and(ElementSelectors.byNameAndAttributes("ID"), allLeaves))
                .whenElementIsNamed("TR")
                .thenUse(allLeaves)
                .whenElementIsNamed("TD")
                .thenUse(ElementSelectors.byNameAndText)
                .build();

        Diff diff = baseBuilder(control, actual)
                .withNodeMatcher(new DefaultNodeMatcher(selector, ElementSelectors.Default))
                .withDifferenceEvaluator(DifferenceEvaluators.chain(
                        DifferenceEvaluators.Default,
                        new IgnoreAttributeDifferenceEvaluator("arraysize"),
                        new FieldDifferenceEvaluator()
                ))
                .build();

        doTest(diff);
    }

    private static void doTest(Diff diff) {
        if (diff.hasDifferences()) {
            throw new AssertionError(diff.toString());
        }
    }

    private static DiffBuilder baseBuilder(String control, String actual) {
        return DiffBuilder.compare(control)
                .withTest(actual)
                .checkForSimilar()
                .ignoreWhitespace()
                .ignoreComments()
                .withNodeFilter(new Predicate<Node>() {
                    @Override
                    public boolean test(org.w3c.dom.Node node) {
                        return !"lastModified".equals(node.getNodeName()) &&
                                !"DESCRIPTION".equals(node.getNodeName());
                    }
                })
                .normalizeWhitespace();
    }

    private static class FieldDifferenceEvaluator implements DifferenceEvaluator {

        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            if (outcome == ComparisonResult.EQUAL){
                return outcome; // only evaluate differences.
            }

            final Node controlNode = comparison.getControlDetails().getTarget();
            final Node testNode = comparison.getTestDetails().getTarget();

            if (controlNode != null) {
                if (StringUtils.equals(controlNode.getLocalName(), "FIELD")) {

                    Node controlIdNode = controlNode.getAttributes().getNamedItem("ID");
                    Node testIdNode = testNode.getAttributes().getNamedItem("ID");

                    if (controlIdNode != null && testIdNode != null) {
                        String controlId = controlIdNode.getNodeValue();
                        String testId = testIdNode.getNodeValue();
                        if (!StringUtils.equals(controlId, testId)) {
                            return ComparisonResult.DIFFERENT;
                        }
                    }

                    Node controlDataTypeNode = controlNode.getAttributes().getNamedItem("datatype");
                    Node testDataTypeNode = testNode.getAttributes().getNamedItem("datatype");

                    if (controlDataTypeNode != null && testDataTypeNode != null) {
                        String control = controlDataTypeNode.getNodeValue();
                        String test = testDataTypeNode.getNodeValue();
                        if (!StringUtils.equals(control, test)) {
                            return ComparisonResult.DIFFERENT;
                        }

                        Node controlArraySizeNode = controlNode.getAttributes().getNamedItem("arraysize");
                        Node testArraySizeNode = testNode.getAttributes().getNamedItem("arraysize");

                        if (controlArraySizeNode != null && testArraySizeNode != null) {
                            String controlAS = controlArraySizeNode.getNodeValue();
                            String testAS = testArraySizeNode.getNodeValue();
                            if (!(
                                    (StringUtils.equals(controlAS, testAS)) ||
                                    ("*".equals(controlAS) && Integer.valueOf(testAS)>0) ||
                                    ("*".equals(testAS) && Integer.valueOf(controlAS)>0)
                                  )
                            ) {
                                return ComparisonResult.DIFFERENT;
                            }
                        }
                    }

                    return ComparisonResult.SIMILAR;

                }
            }
            return outcome;
        }
    }

    private static class IgnoreAttributeDifferenceEvaluator implements DifferenceEvaluator {

        private String attributeName;

        public IgnoreAttributeDifferenceEvaluator(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            if (outcome == ComparisonResult.EQUAL) return outcome; // only evaluate differences.
            final Node controlNode = comparison.getControlDetails().getTarget();
            if (controlNode instanceof Attr) {
                Attr attr = (Attr) controlNode;
                if (attr.getName().equals(attributeName)) {
                    return ComparisonResult.SIMILAR; // will evaluate this difference as similar
                }
            }
            return outcome;
        }
    }
}
