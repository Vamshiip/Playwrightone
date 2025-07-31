package utils;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.Status;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AllureLogger {
    public static void safeStep(String message) {
        try {
            String stepUUID = UUID.randomUUID().toString();
            AllureLifecycle lifecycle = Allure.getLifecycle();
            lifecycle.startStep(stepUUID, new StepResult().setName(message).setStatus(Status.PASSED));
            System.out.println("[ALLURE STEP] " + message);
            lifecycle.stopStep(stepUUID);
        } catch (Exception e) {
            System.err.println("❌ Allure step failed: " + e.getMessage());
        }
    }

    public static void attachText(String name, String content) {
        try {
            Allure.addAttachment(name, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.err.println("❌ Failed to attach to Allure: " + e.getMessage());
        }
    }
}


