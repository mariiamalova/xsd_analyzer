package ru.homecredit.dto;

import java.nio.file.Paths;

import static ru.homecredit.util.FileUtil.PROJECT_DIR;
import static ru.homecredit.util.FileUtil.SYSTEM_SEPARATOR;

public record XsdElement(String elementName, String filePath, int level) {
    public String getFileName() {
        return filePath.substring(filePath.lastIndexOf(SYSTEM_SEPARATOR) + 1);
    }

    public String getRelativeFilePath() {
        return Paths.get(PROJECT_DIR).relativize(Paths.get(filePath)).toString();
    }
}
