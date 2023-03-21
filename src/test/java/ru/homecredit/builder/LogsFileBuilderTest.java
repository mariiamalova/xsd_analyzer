package ru.homecredit.builder;

import org.junit.Test;
import ru.homecredit.DataCreatorUtilTest;
import ru.homecredit.util.FileUtil;

import static org.junit.Assert.assertEquals;

public class LogsFileBuilderTest extends DataCreatorUtilTest {
   private static final String expectedTextForLogFile = ("Cписок затронутых xsd файлов и элементов в них: \n" +
                "src\\test\\resources\\xsd\\ContractWS.xsd [searchContractsExtendedRequest]\n" +
                "src\\test\\resources\\xsd\\Person.xsd [PersonFilter]\n" +
                "src\\test\\resources\\xsd\\ContractToCompare.xsd [ContractClientFilter]\n" +
                "src\\test\\resources\\xsd\\Contract.xsd [ContractClientFilter]\n")
                        .replace("\\", FileUtil.SYSTEM_SEPARATOR);

    @Test
    public void getChainOfChangesForLogsTest() {
        String actualTextForLogFile = LogsFileBuilder.getChainOfChangesForLogs(DataCreatorUtilTest.createDependentListMap());
        assertEquals(expectedTextForLogFile, actualTextForLogFile);
    }
}
