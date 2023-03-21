package ru.homecredit.util;

import com.predic8.schema.Schema;
import com.predic8.schema.SchemaParser;
import org.junit.Test;
import ru.homecredit.DataCreatorUtilTest;
import ru.homecredit.util.XsdUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.homecredit.util.FileUtil.PROJECT_PATH;
import static ru.homecredit.util.FileUtil.SYSTEM_SEPARATOR;

public class XsdUtilTest extends DataCreatorUtilTest {

    @Test
    public void getAffectedXsdTest() {
        List<String> expectedList = new ArrayList<>();
        expectedList.add(getCorrectPath("src/test/resources/xsd/Contract.xsd"));
        expectedList.add(getCorrectPath("src/test/resources/xsd/ContractToCompare.xsd"));
        List<String> actualMap = XsdUtil.getAffectedXsd("Person.xsd", "PersonFilter",
                getCorrectPath("src/test/resources/xsd/Person.xsd"));
        assertEquals(expectedList, actualMap);
    }

    @Test
    public void getDiffsInXsdTest() {
        SchemaParser schemaParser = new SchemaParser();
        List<String> expectedList = new ArrayList<>();
        expectedList.add("Payments");
        expectedList.add("DebtList");

        Schema shema =
                schemaParser.parse(PROJECT_PATH + "src/test/resources/xsd/ContractToCompare.xsd");
        Schema schema1 = schemaParser.parse(PROJECT_PATH + "src/test/resources/xsd/Contract.xsd"
                .replace("/", SYSTEM_SEPARATOR));

        List<String> actualList = XsdUtil.getDiffsInXsd(shema, schema1);

        assertEquals(expectedList, actualList);
    }
}
