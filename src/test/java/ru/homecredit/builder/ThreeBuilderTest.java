package ru.homecredit.builder;

import org.junit.Test;
import ru.homecredit.DataCreatorUtilTest;
import ru.homecredit.dto.ThreeNode;
import ru.homecredit.dto.XsdElement;

import static org.junit.Assert.assertEquals;

public class ThreeBuilderTest extends DataCreatorUtilTest {
    @Test
    public void buildTest() {
        ThreeNode<XsdElement> expectedThree = createThree();
        ThreeNode<XsdElement> actualThree = ThreeBuilder.build(createDependentListMap()).get();
        assertEquals(expectedThree, actualThree);
    }
}
