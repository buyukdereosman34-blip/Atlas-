package com.atlas.app.improvement;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JSONL (JSON Lines) dosyalarını bozuk satırlara karşı güvenli okur.
 *
 * Project Memory (docs/history/events.jsonl) için kullanılır.
 * Uygulama çökerse son satır yarım kalabilir; bu reader
 * bozuk satırı atlar, geçerli satırları döndürür.
 */
public final class SafeJsonlReader {

    private SafeJsonlReader() {
    }

    public static List<JSONObject> readValidEvents(File jsonlFile) {
        List<JSONObject> validEvents = new ArrayList<>();

        if (jsonlFile == null
                || !jsonlFile.exists()
                || !jsonlFile.isFile()) {
            return validEvents;
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(jsonlFile))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();

                if (trimmed.isEmpty()) {
                    continue;
                }

                try {
                    validEvents.add(new JSONObject(trimmed));
                } catch (Exception e) {
                    System.err.println(
                            "[SafeJsonlReader] Bozuk satir atlandi ("
                                    + lineNumber
                                    + "): "
                                    + trimmed
                    );
                }
            }

        } catch (IOException e) {
            System.err.println(
                    "[SafeJsonlReader] Dosya okunamadi: "
                            + e.getMessage()
            );
        }

        return validEvents;
    }

    public static int countValidEvents(File jsonlFile) {
        return readValidEvents(jsonlFile).size();
    }
}
