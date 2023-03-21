package ru.homecredit.builder;

import org.junit.Assert;
import org.junit.Test;
import ru.homecredit.DataCreatorUtilTest;

public class PumlFileBuilderTest extends DataCreatorUtilTest {
    private static final String expectedDataPumlFile = "@startwbs \n" +
            "+ Person.xsd \\n PersonFilter\n" +
            "++ ContractToCompare.xsd \\n ContractClientFilter\n" +
            "++ Contract.xsd \\n ContractClientFilter\n" +
            "+++ ContractWS.xsd \\n searchContractsExtendedRequest\n" +
            "@enduwbs \n";

    @Test
    public void buildTest() {
        String actualDataPumlFile = PumlFileBuilder.build(createThree());
        Assert.assertEquals(expectedDataPumlFile, actualDataPumlFile);
    }
}
