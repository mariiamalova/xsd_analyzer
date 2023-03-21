package ru.homecredit.git;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.homecredit.util.CommandLineUtil;

import java.util.List;
import java.util.Scanner;

import static ru.homecredit.util.FileUtil.SYSTEM_SEPARATOR;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GitClient {
    private static final String GET_MODIFY_FILES = "git diff-tree --no-commit-id --name-only -r ";
    private static final String GET_GIT_BRANCHES = "git branch --format=%(refname:short) -a";
    private static final String GET_COMMITS_FROM_BRANCH = "git log --oneline --no-merges --format=%s ^master";
    private static final String GET_BRANCH_CONTENT = "git show %s:%s";
    private static final String GET_CURRENT_BRANCH = "git rev-parse --abbrev-ref HEAD";
    public static final String FORMAT_XSD = ".xsd";
    private static final String SEPARATOR_FOR_GIT = "/";
    private static final String SEPARATOR_WIN = "\\";

    public static List<String> getAllModifiedXsdFromCommits() {
        return getCommitsFromCurrentBranch().stream()
                .flatMap(commit -> CommandLineUtil.runCommand(GET_MODIFY_FILES + commit).stream())
                .filter(file -> file.endsWith(FORMAT_XSD))
                .map(file -> file.replace("/", SYSTEM_SEPARATOR))
                .distinct()
                .toList();
    }

    public static String getBranchForComparison() {
        System.out.println("Введите имя ветки для сравнения:");
        Scanner scanner = new Scanner(System.in);
        String branchForComparison = scanner.nextLine();
        List<String> branches = CommandLineUtil.runCommand(GET_GIT_BRANCHES);
        return branches.stream()
                .filter(branch -> branch.equals(branchForComparison))
                .findAny().orElseGet(() -> {
                    System.out.println("Такой ветки не существует.");
                    return getBranchForComparison();
                });
    }

    private static List<String> getCommitsFromCurrentBranch() {
        return CommandLineUtil.runCommand(String.format(GET_COMMITS_FROM_BRANCH, "%H " + getCurrentBranch()));
    }

    public static String getFileBranchContent(String pathForSchemaActual, String branchForComparison) {
        pathForSchemaActual = pathForSchemaActual.replace(SEPARATOR_WIN, SEPARATOR_FOR_GIT);
        return String.join("\n", CommandLineUtil.runCommand(String.format(GET_BRANCH_CONTENT,
                branchForComparison, pathForSchemaActual)));
    }

    private static String getCurrentBranch() {
        return CommandLineUtil.runCommand(GET_CURRENT_BRANCH).stream()
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}