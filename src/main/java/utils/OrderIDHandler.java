package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class OrderIDHandler {

    private static final String EXCEL_PATH = "./OrderIDs.xlsx";
    private static final String CSV_PATH = "./OrderIDs.csv";

    // Extracts order IDs from raw text using regex (e.g., QIX-846)
    public static List<String> extractOrderIDs(String text) {
        List<String> ids = new ArrayList<>();
        Matcher matcher = Pattern.compile("QIX-\\d+").matcher(text);
        while (matcher.find()) {
            ids.add(matcher.group());
        }
        return ids;
    }

    // Saves order IDs to Excel and CSV
    public static void storeOrderIDs(List<String> orderIDs) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("OrderIDs");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Order ID");

            for (int i = 0; i < orderIDs.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(orderIDs.get(i));
            }

            try (FileOutputStream out = new FileOutputStream(EXCEL_PATH)) {
                workbook.write(out);
            }

            // Also save to CSV
            Files.write(Paths.get(CSV_PATH), ("Order ID\n" + String.join("\n", orderIDs)).getBytes());

            System.out.println("✅ Order IDs stored to Excel and CSV.");
        } catch (IOException e) {
            System.out.println("❌ Error writing Order IDs: " + e.getMessage());
        }
    }

    // Reads order IDs from Excel
    public static List<String> retrieveOrderIDs() {
        List<String> ids = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(EXCEL_PATH);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheet("OrderIDs");
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                ids.add(row.getCell(0).getStringCellValue());
            }
            return ids;
        } catch (IOException e) {
            System.out.println("❌ Error reading Order IDs: " + e.getMessage());
        }
        return ids;
    }

    // Extract and store from full raw string like: "Payment for Order: QIX-846,QIX-847"
    public static void captureAndStoreOrderIDsFromText(String rawText) {
        if (rawText != null && rawText.contains("Order:")) {
            String afterOrder = rawText.split("Order:")[1].trim();
            List<String> orderIDs = extractOrderIDs(afterOrder);
            storeOrderIDs(orderIDs);
        } else {
            System.out.println("⚠ No valid 'Order:' text found.");
        }
    }
   
 // ✅ Return first Order ID from the sheet
    public static String getFirstOrderID() {
        List<String> ids = retrieveOrderIDs();
        return ids.isEmpty() ? "" : ids.get(0);
    }
//    // Return most recent (last) Order ID for reuse
    public static String getLatestOrderID() {
        List<String> ids = retrieveOrderIDs();
        return ids.isEmpty() ? "" : ids.get(ids.size() - 1);
    }
}
