package ru.homecredit.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.homecredit.dto.ThreeNode;
import ru.homecredit.dto.XsdElement;
import ru.homecredit.util.FileUtil;

import static ru.homecredit.util.FileUtil.SYSTEM_SEPARATOR;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PumlFileBuilder {
    public static final String FOLDER_FOR_DIAGRAMS = SYSTEM_SEPARATOR + "diagrams";
    private static final String START_BUILD = "@startwbs ";
    private static final String END_BUILD = "@enduwbs ";
    private static final String FORMAT_XSD = ".xsd";
    private static final String FORMAT_PUML = ".puml";
    private static final String CAPTION_DELIMITER = " \\n ";
    private static final String EOF = "\n";
    private static final String SPACE = " ";
    private static final String PLUS = "+";
    public static final String START_ELEMENT_NAME = "[";
    public static final String END_ELEMENT_NAME = "]";

    private static void addLevelForLineInDiagram(int level, StringBuilder diagram) {
        diagram.append(PLUS.repeat(level));
    }

    private static void addElementInDiagram(String xsd, String element, StringBuilder diagram) {
        diagram.append(SPACE)
                .append(xsd)
                .append(CAPTION_DELIMITER)
                .append(element)
                .append(EOF);
    }

    private static String getPumlFileName(String elementName, String fileName) {
        return START_ELEMENT_NAME + elementName + END_ELEMENT_NAME + fileName;
    }

    public static String build(ThreeNode<XsdElement> three) {
        XsdElement mainChange = three.value();
        String pumlFileName = PumlFileBuilder.getPumlFileName(mainChange.elementName(), mainChange.getFileName());

        String dataPumlFile = START_BUILD + EOF
                + processAddElements(three)
                + END_BUILD + EOF;

        createPumlFile(pumlFileName, dataPumlFile);
        return dataPumlFile;
    }

    private static void createPumlFile(String pumlFileName, String dataPumlFile) {
        String path = FileUtil.createFolderAndGetPath(FOLDER_FOR_DIAGRAMS);
        FileUtil.writeData(path + pumlFileName.replace(FORMAT_XSD, FORMAT_PUML), dataPumlFile);
    }

    private static StringBuilder processAddElements(ThreeNode<XsdElement> three) {
        StringBuilder mainPartOfPumlFile = new StringBuilder();
        ThreeBuilder.getDescendants(three).forEach(node -> createRecordInDiagram(node, mainPartOfPumlFile));

        return mainPartOfPumlFile;
    }

    private static void createRecordInDiagram(ThreeNode<XsdElement> threeNode, StringBuilder diagram) {
        var xsdElement = threeNode.value();
        int level = xsdElement.level();
        addLevelForLineInDiagram(level, diagram);
        addElementInDiagram(xsdElement.getFileName(), xsdElement.elementName(), diagram);
    }
}
