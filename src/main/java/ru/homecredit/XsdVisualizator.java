package ru.homecredit;

import ru.homecredit.builder.LogsFileBuilder;
import ru.homecredit.builder.PumlFileBuilder;
import ru.homecredit.builder.ThreeBuilder;
import ru.homecredit.dto.XsdElement;
import ru.homecredit.git.GitClient;
import ru.homecredit.util.XsdUtil;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import static ru.homecredit.util.FileUtil.PROJECT_PATH;

public class XsdVisualizator {

    public static void main(String[] args) {
        String branchForComparison = GitClient.getBranchForComparison();
        List<String> modifiedFiles = GitClient.getAllModifiedXsdFromCommits();
        analysisOfAllModifiedFiles(branchForComparison, modifiedFiles);
    }

    private static void analysisOfAllModifiedFiles(String branchForComparison, List<String> modifiedFiles) {
        for (String filePath : modifiedFiles) {
            for (String changedElement : XsdUtil.getChangedXsdElements(filePath, branchForComparison)) {
                XsdElement causeOfChange = new XsdElement(changedElement, PROJECT_PATH + filePath, 1);
                Map<XsdElement, List<XsdElement>> dependentList = mapToLinkedNodes(causeOfChange);

                createDiagram(dependentList);
                createLogs(dependentList);
            }
        }
    }

    private static void createDiagram(Map<XsdElement, List<XsdElement>> dependentList) {
        if (!dependentList.isEmpty()) {
            ThreeBuilder.build(dependentList).ifPresent(PumlFileBuilder::build);
        }
    }

    private static void createLogs(Map<XsdElement, List<XsdElement>> dependentList) {
        if (!dependentList.isEmpty()) {
            LogsFileBuilder.build(dependentList);
        }
    }

    public static Map<XsdElement, List<XsdElement>> mapToLinkedNodes(XsdElement root) {
        Queue<XsdElement> queue = new LinkedList<>();
        queue.add(root);
        Map<XsdElement, List<XsdElement>> result = new LinkedHashMap<>();
        while (!queue.isEmpty()) {
            XsdElement current = queue.poll();
            //список файлов затронутых изменениями
            List<XsdElement> affectedElements =
                    XsdUtil.getAffectedXsd(current.getFileName(), current.elementName(), current.filePath())
                            .stream()
                            .flatMap(xsd -> XsdUtil.getXsdWithAffectedElementsNames(xsd, current.elementName())
                                    .stream().map(elementName ->
                                            new XsdElement(elementName, xsd, current.level() + 1)))
                            .collect(Collectors.toList());

            result.merge(current, affectedElements, (oldVal, newVal) -> {
                oldVal.addAll(newVal);
                return oldVal;
            });
            queue.addAll(affectedElements);
        }
        return result;
    }
}

