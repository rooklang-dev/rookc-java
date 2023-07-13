/*
MIT License

Copyright (c) 2023 rooklang-dev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package rookc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

final class Report {
    public static void error(String message) {
        printReport(message, ReportType.ERROR);
    }

    public static void warning(String message) {
        printReport(message, ReportType.WARNING);
    }

    public static void error(String message, String filePath, int line, int column) {
        printReport(message, ReportType.ERROR, filePath, line, column);
    }

    public static void warning(String message, String filePath, int line, int column) {
        printReport(message, ReportType.WARNING, filePath, line, column);
    }

    public static void info(String message) {
        printReport(message, ReportType.INFO);
    }

    private static void printReport(String message, ReportType type) {
        System.out.println(type.toString() + ": " + message);
    }

    private static void printReport(String message, ReportType type, String filePath, int line, int column) {
        try {
            printFileContent(filePath, line, column);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println(type.toString() + ": " + message);
    }

    private static void printFileContent(String filePath, int line, int column) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String lineContent;
        int currentLine = 1;

        while ((lineContent = reader.readLine()) != null) {
            if (currentLine == line) {
                System.out.println(lineContent);
                highlightColumn(column);
                break;
            }

            currentLine++;
        }

        reader.close();
    }

    private static void highlightColumn(int column) {
        for (int i = 0; i < column - 1; i++) {
            System.out.print(" ");
        }

        System.out.println("^");
    }

    public enum ReportType {
        ERROR,
        WARNING,
        INFO
    }
}