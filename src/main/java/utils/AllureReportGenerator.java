//package utils;
//
//import java.io.IOException;
//
//public class AllureReportGenerator {
//
//    public static void generateReport() {
//        try {
//            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
//                    "allure generate allure-results --clean -o allure-report");
//            builder.inheritIO();
//            Process process = builder.start();
//            process.waitFor();
//            System.out.println("Allure Report generated.");
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}

//=============================================================================================
// Final Working Fix for Allure Reporting with Optional Auto Open and Safe Emailing

package utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import java.util.stream.Stream;

public class AllureReportGenerator {

    // 👇 Change if you use different thread naming
    private static final String RESULT_DIR_PREFIX = "allure-results-";
    private static final String MERGED_RESULTS_DIR = "allure-results-merged";
    private static final String ALLURE_REPORT_DIR = "allure-report";
    private static final String ALLURE_BINARY = "C:\\Tools\\allure-2.34.1\\bin\\allure.bat"; // Update if needed

    public static void generateReport() {
        try {
            cleanOldReport();
            mergeResults();
            generateAllureReport();
        } catch (Exception e) {
            System.out.println("⚠ Failed to generate Allure report: " + e.getMessage());
            createFallbackSummary();
        }
        
        //To Open allure report everytime after Execution
//        public static void generateReport() {
//            try {
//                cleanOldReport();          // Step 1: Delete old allure-report folder
//                mergeResults();            // Step 2: Merge per-thread result folders (if applicable)
//                generateAllureReport();    // Step 3: Run allure.bat generate command
//
//                // Step 4: Try to open report in browser (localhost server)
//                try {
//                    String cmd = "cmd /c start \"\" \"C:\\Tools\\allure-2.34.1\\bin\\allure.bat\" open allure-report";
//                    Runtime.getRuntime().exec(cmd);
//                    System.out.println("🌐 Opening Allure report in browser...");
//                } catch (IOException e) {
//                    System.out.println("⚠ Could not auto-open Allure report: " + e.getMessage());
//                }
//
//            } catch (Exception e) {
//                System.out.println("⚠ Failed to generate Allure report: " + e.getMessage());
//                createFallbackSummary(); // Only used if real report generation fails
//            }
//        }

        
    }

    private static void cleanOldReport() {
        try {
            deleteDirectory(Paths.get(ALLURE_REPORT_DIR));
            deleteDirectory(Paths.get(MERGED_RESULTS_DIR));
            System.out.println("🧹 Cleaned old report and merged results folder.");
        } catch (IOException e) {
            System.out.println("⚠ Error cleaning folders: " + e.getMessage());
        }
    }

    private static void mergeResults() throws IOException {
        Files.createDirectories(Paths.get(MERGED_RESULTS_DIR));

        List<Path> resultDirs = Files.list(Paths.get("."))
            .filter(path -> Files.isDirectory(path) && path.getFileName().toString().startsWith(RESULT_DIR_PREFIX))
            .collect(Collectors.toList());

        boolean anyResults = false;

        for (Path dir : resultDirs) {
            try (Stream<Path> files = Files.walk(dir)) {
                for (Path file : files.filter(Files::isRegularFile).toList()) {
                    Path dest = Paths.get(MERGED_RESULTS_DIR, file.getFileName().toString());
                    Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING);
                    anyResults = true;
                }
            }
        }

        if (anyResults) {
            System.out.println("✅ Merged results from: " + resultDirs.size() + " folders → " + MERGED_RESULTS_DIR);
        } else {
            System.out.println("⚠ No Allure result files found in any folder.");
        }
    }

    private static void generateAllureReport() throws IOException, InterruptedException {
        File merged = new File(MERGED_RESULTS_DIR);
        if (!merged.exists() || merged.listFiles() == null || merged.listFiles().length == 0) {
            throw new IOException("❌ Merged results folder is empty: " + MERGED_RESULTS_DIR);
        }

        ProcessBuilder builder = new ProcessBuilder(
            ALLURE_BINARY, "generate", MERGED_RESULTS_DIR, "-o", ALLURE_REPORT_DIR, "--clean");
        builder.inheritIO();

        Process process = builder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("✅ Allure report generated successfully: " + ALLURE_REPORT_DIR + "\\index.html");
        } else {
            System.out.println("❌ Allure report generation failed. Exit code: " + exitCode);
            createFallbackSummary();
        }
    }

    private static void deleteDirectory(Path dirPath) throws IOException {
        if (Files.exists(dirPath)) {
            Files.walk(dirPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    private static void createFallbackSummary() {
        try {
            Path summaryPath = Paths.get(ALLURE_REPORT_DIR, "widgets", "summary.json");
            Files.createDirectories(summaryPath.getParent());

            String dummyJson = """
                {
                  "total": 1,
                  "items": [
                    {
                      "uid": "FAILED",
                      "name": "Allure Report Generation Failed",
                      "statistic": {
                        "failed": 1,
                        "passed": 0,
                        "skipped": 0,
                        "total": 1
                      }
                    }
                  ]
                }
                """;

            Files.writeString(summaryPath, dummyJson);
            System.out.println("✅ Dummy summary.json created for fallback email.");
        } catch (IOException e) {
            System.out.println("❌ Failed to create fallback summary.json: " + e.getMessage());
        }
    }
    
}


