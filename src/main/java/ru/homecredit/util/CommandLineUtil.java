package ru.homecredit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandLineUtil {

    @SneakyThrows
    public static List<String> runCommand(String command) {
        var process = Runtime.getRuntime().exec(command);
        return new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .filter(line -> !line.isEmpty())
                .toList();
    }
}

