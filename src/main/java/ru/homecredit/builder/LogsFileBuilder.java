package ru.homecredit.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.homecredit.dto.XsdElement;
import ru.homecredit.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.homecredit.builder.PumlFileBuilder.END_ELEMENT_NAME;
import static ru.homecredit.builder.PumlFileBuilder.START_ELEMENT_NAME;
import static ru.homecredit.git.GitClient.FORMAT_XSD;
import static ru.homecredit.util.FileUtil.SYSTEM_SEPARATOR;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogsFileBuilder {
    private static final String END_OF_LOG_FILE = "_DependedFiles.txt";
    private static final String FOLDER_FOR_LOGS =  SYSTEM_SEPARATOR + "dependencies";

    public static void build(Map<XsdElement, List<XsdElement>> traverse) {
        XsdElement element = traverse.entrySet().iterator().next().getKey();
        String path = FileUtil.createFolderAndGetPath(FOLDER_FOR_LOGS);
        String logFileName = (START_ELEMENT_NAME + element.elementName() + END_ELEMENT_NAME
                + element.getFileName()).replace(FORMAT_XSD, END_OF_LOG_FILE);
        FileUtil.writeData(path + logFileName, getChainOfChangesForLogs(traverse));
    }

    public static String getChainOfChangesForLogs(Map<XsdElement, List<XsdElement>> dependentList) {
        List<XsdElement> xsdElements = new ArrayList<>();
        dependentList.values().forEach(xsdElements::addAll);
        xsdElements.add(dependentList.entrySet().iterator().next().getKey());

        Map<String, List<String>> xsdWithAffectedElements = xsdElements.stream()
                .collect(Collectors.groupingBy(XsdElement::getRelativeFilePath,
                        Collectors.collectingAndThen(Collectors.toList(),
                                affectedElements -> affectedElements.stream()
                                        .map(XsdElement::elementName)
                                        .toList())));
        return generateTextForLogFile(xsdWithAffectedElements);
    }

    private static String generateTextForLogFile(Map<String, List<String>> xsdWithAffectedElements) {
        StringBuilder chainOfChanges = new StringBuilder();

        chainOfChanges.append("Cписок затронутых xsd файлов и элементов в них: \n");
        for (Map.Entry<String, List<String>> entry: xsdWithAffectedElements.entrySet()) {
            chainOfChanges.append(entry.getKey())
                    .append(" ")
                    .append(entry.getValue())
                    .append("\n");
        }
        return chainOfChanges.toString();
    }
}
