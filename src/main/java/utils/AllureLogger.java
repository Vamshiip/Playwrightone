package utils;

import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class AllureLogger {

    public static void logStep(String message) {
        Allure.step(message);
        System.out.println("[ALLURE STEP] " + message);
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }
}
