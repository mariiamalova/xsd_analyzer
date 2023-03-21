package ru.homecredit.util;

import com.predic8.schema.Schema;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.SchemaParser;
import com.predic8.schema.diff.SchemaDiffGenerator;
import com.predic8.soamodel.Difference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import ru.homecredit.git.GitClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.homecredit.git.GitClient.FORMAT_XSD;
import static ru.homecredit.util.FileUtil.PROJECT_DIR;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XsdUtil {
    public static final SchemaParser PARSER = new SchemaParser();
    private static final String POINTER_FOR_TARGETNAMESPACE = "=";
    private static final String COLON = ":";
    private static final String END_OF_TEMP_FILE = "Temp.xsd";
    private static final String ADDED_DESCRIPTION_SUFFIX = "added.";
    private static final String REMOVED_DESCRIPTION_SUFFIX = "removed.";
    private static final String ELEMENT_TYPE = "element";
    private static final String COMPLEX_TYPE = "complexType";
    private static final String SIMPLE_TYPE = "simpleType";
    private static final String SPACE = " ";
    private static final String POINTER_FOR_ELEMENT = "with";
    private static final String POINTER_FOR_SIMPLE_TYPE = "}";
    private static final String COMA = ",";

    public static List<String> getAffectedXsd(String fileImportName, String changedElement, String filePath) {
        String targetNamespace = XsdUtil.getTargetNamespace(filePath);
        return FileUtil.searchInDirectory(new File(PROJECT_DIR), fileImportName, new ArrayList<>())
                .stream()
                .filter(path -> !path.endsWith(END_OF_TEMP_FILE))
                .map(xsdFile -> getPrefixTargetNamespace(xsdFile, targetNamespace)
                        .map(prefix -> getXsdWithChangedElement(xsdFile, changedElement, prefix)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private static String getXsdWithChangedElement(String pathXsdWithImport, String changedElement,
                                                   String prefix) {
        return FileUtil.searchInFile(new File(pathXsdWithImport), prefix + COLON + changedElement)
                .orElse(null);
    }

    public static String getTargetNamespace(String pathXsd) {
        Schema xsd = PARSER.parse(pathXsd);
        return xsd.getTargetNamespace();
    }

    public static List<String> getChangedXsdElements(String pathForSchemaActual, String branchForComparison) {
        String fileBranchContent = GitClient.getFileBranchContent(pathForSchemaActual, branchForComparison);
        String pathToNewFile = pathForSchemaActual.substring(0, pathForSchemaActual.length()
                - FORMAT_XSD.length()) + END_OF_TEMP_FILE;
        FileUtil.writeData(pathToNewFile, fileBranchContent);

        List<String> diffsInSchema = getDiffsInXsd(PARSER.parse(pathForSchemaActual), PARSER.parse(pathToNewFile));
        FileUtil.deletePath(pathToNewFile);
        return diffsInSchema;
    }

    public static List<String> getDiffsInXsd(Schema modifiedFileSchema, Schema masterFileSchema) {
        List<Difference> differences = new SchemaDiffGenerator(modifiedFileSchema, masterFileSchema).compare();
        return differences.stream().map(diff -> {
            String description = diff.getDescription();
            int startPos = description.indexOf(SPACE) + 1;
            int endPos = description.lastIndexOf(COLON);
            if (diff.getDescription().endsWith(ADDED_DESCRIPTION_SUFFIX)
                    || diff.getDescription().endsWith(REMOVED_DESCRIPTION_SUFFIX)) {
                switch (diff.getType()) {
                    case ELEMENT_TYPE -> endPos = description.lastIndexOf(POINTER_FOR_ELEMENT) - 1;
                    case COMPLEX_TYPE -> endPos = description.lastIndexOf(SPACE);
                    case SIMPLE_TYPE -> {
                        startPos = description.indexOf(POINTER_FOR_SIMPLE_TYPE) + 1;
                        endPos = description.lastIndexOf(SPACE);
                    }
                    default -> throw new RuntimeException("Нeизвестный тип элемента");
                }
            }
            return description.substring(startPos, endPos);
        }).toList();
    }

    @SneakyThrows
    private static Optional<String> getPrefixTargetNamespace(String xsd, String targetNamespace) {
        try (var reader = new BufferedReader(new FileReader(xsd))) {
            return reader.lines().filter(line -> isLineHasTargetNamespace(line, targetNamespace))
                    .map(line ->  line.split(POINTER_FOR_TARGETNAMESPACE)[0])
                    .map(str -> str.split(COLON)[1]).findAny();
        }
    }

    private static boolean isLineHasTargetNamespace(String line, String targetNamespace) {
        return line.contains("xmlns:") && line.contains("=\"" + targetNamespace + "\"");
    }

    public static List<String> getXsdWithAffectedElementsNames(String pathXsd, String changedElement) {
        Schema xsd = PARSER.parse(pathXsd);
        List<String> elements = getAffectedElementsNames(xsd.getElements().stream(), changedElement);
        List<String> complexTypes = getAffectedElementsNames(xsd.getComplexTypes().stream(), changedElement);
        List<String> simpleTypes = getAffectedElementsNames(xsd.getSimpleTypes().stream(), changedElement);
        elements.addAll(complexTypes);
        elements.addAll(simpleTypes);
        return elements;
    }

    private static List<String> getAffectedElementsNames(Stream<? extends SchemaComponent> stream,
                                                         String changedElement) {
        return stream
                .filter(Objects::nonNull)
                .filter(element -> element.toString()
                        .contains(POINTER_FOR_SIMPLE_TYPE + changedElement + COMA))
                .map(SchemaComponent::getName)
                .collect(Collectors.toList());
    }
}

