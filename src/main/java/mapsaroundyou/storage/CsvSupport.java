package mapsaroundyou.storage;

import mapsaroundyou.common.DataLoadException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

final class CsvSupport {
    private CsvSupport() {
    }

    static ReaderSupplier classpathReader(String resourcePath) {
        return () -> {
            InputStream inputStream = CsvSupport.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                throw new DataLoadException("Missing resource: " + resourcePath);
            }
            return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        };
    }

    static ReaderSupplier fileReader(Path filePath) {
        return () -> Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
    }

    static CSVParser openParser(ReaderSupplier readerSupplier, String sourceName, String... requiredHeaders) {
        Reader reader = null;
        try {
            reader = readerSupplier.open();
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .get();
            CSVParser parser = format.parse(reader);
            validateHeaders(parser.getHeaderMap(), sourceName, requiredHeaders);
            return parser;
        } catch (IOException exception) {
            closeQuietly(reader);
            throw new DataLoadException("Failed to read dataset: " + sourceName, exception);
        }
    }

    static String requireValue(CSVRecord record, String header, String sourceName) {
        String value = record.get(header).trim();
        if (value.isEmpty()) {
            throw new DataLoadException("Blank value for '" + header + "' in " + sourceName
                    + " at row " + record.getRecordNumber());
        }
        return value;
    }

    static int parseRequiredInt(CSVRecord record, String header, String sourceName) {
        String value = requireValue(record, header, sourceName);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new DataLoadException("Invalid integer for '" + header + "' in " + sourceName
                    + " at row " + record.getRecordNumber() + ": " + value, exception);
        }
    }

    static double parseRequiredDouble(CSVRecord record, String header, String sourceName) {
        String value = requireValue(record, header, sourceName);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new DataLoadException("Invalid decimal for '" + header + "' in " + sourceName
                    + " at row " + record.getRecordNumber() + ": " + value, exception);
        }
    }

    static boolean parseRequiredBoolean(CSVRecord record, String header, String sourceName) {
        String value = requireValue(record, header, sourceName);
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new DataLoadException("Invalid boolean for '" + header + "' in " + sourceName
                + " at row " + record.getRecordNumber() + ": " + value);
    }

    private static void validateHeaders(Map<String, Integer> headerMap, String sourceName, String... requiredHeaders) {
        for (String header : requiredHeaders) {
            if (!headerMap.containsKey(header)) {
                throw new DataLoadException("Missing required column '" + header + "' in " + sourceName
                        + ". Present columns: " + Arrays.toString(headerMap.keySet().toArray()));
            }
        }
    }

    private static void closeQuietly(Reader reader) {
        if (reader == null) {
            return;
        }
        try {
            reader.close();
        } catch (IOException ignored) {
            // Best-effort cleanup after parser construction fails.
        }
    }
}
