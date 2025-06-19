package utils;

import java.io.IOException;

public class AllureReportGenerator {

    public static void generateReport() {
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
                    "allure generate allure-results --clean -o allure-report");
            builder.inheritIO();
            Process process = builder.start();
            process.waitFor();
            System.out.println("Allure Report generated.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
