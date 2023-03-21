package ru.homecredit;

import org.junit.Test;
import ru.homecredit.dto.XsdElement;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class XsdVisualizatorTest extends DataCreatorUtilTest {

    @Test
    public void mapToLinkedNodesTest() {
        Map<XsdElement, List<XsdElement>> mapExpected = createDependentListMap();
        Map<XsdElement, List<XsdElement>> mapActual = XsdVisualizator.mapToLinkedNodes(createFirstXsdElement());
        assertEquals(mapExpected, mapActual);
    }
}
