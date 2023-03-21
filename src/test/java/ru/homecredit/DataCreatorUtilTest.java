package ru.homecredit;

import ru.homecredit.dto.ThreeNode;
import ru.homecredit.dto.XsdElement;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.EMPTY_LIST;
import static ru.homecredit.util.FileUtil.PROJECT_PATH;
import static ru.homecredit.util.FileUtil.SYSTEM_SEPARATOR;

public abstract class DataCreatorUtilTest {
    public static XsdElement createFirstXsdElement() {
        return new XsdElement("PersonFilter", getCorrectPath("src/test/resources/xsd/Person.xsd"), 1);
    }

    public static Map<XsdElement, List<XsdElement>> createDependentListMap() {
        var key1 = createFirstXsdElement();
        var key2 = new XsdElement("ContractClientFilter", getCorrectPath("src/test/resources/xsd/Contract.xsd"), 2);
        var key3 = new XsdElement("ContractClientFilter", getCorrectPath("src/test/resources/xsd/ContractToCompare.xsd"), 2);
        var key4 = new XsdElement("searchContractsExtendedRequest", getCorrectPath("src/test/resources/xsd/ContractWS.xsd"), 3);

        Map<XsdElement, List<XsdElement>> expectedMap = new LinkedHashMap<>();
        expectedMap.put(key1, Arrays.asList(key2, key3));
        expectedMap.put(key2, List.of(key4));
        expectedMap.put(key3, EMPTY_LIST);
        expectedMap.put(key4, EMPTY_LIST);

        return expectedMap;
    }

    public static ThreeNode<XsdElement> createThree() {
        ThreeNode<XsdElement> three = new ThreeNode<>(createFirstXsdElement());
        ThreeNode<XsdElement> threeNode = new ThreeNode<>(new XsdElement("ContractClientFilter",
                getCorrectPath("src/test/resources/xsd/Contract.xsd"), 2));
        ThreeNode<XsdElement> threeNode2 = new ThreeNode<>(new XsdElement("ContractClientFilter",
                getCorrectPath("src/test/resources/xsd/ContractToCompare.xsd"), 2));
        ThreeNode<XsdElement> threeNode3 = new ThreeNode<>(new XsdElement("searchContractsExtendedRequest",
                getCorrectPath("src/test/resources/xsd/ContractWS.xsd"), 3));
        three.addChild(threeNode);
        three.addChild(threeNode2);
        threeNode.addChild(threeNode3);
        return three;
    }

    public static String getCorrectPath(String path) {
        return PROJECT_PATH + path.replace("/", SYSTEM_SEPARATOR);
    }
}
