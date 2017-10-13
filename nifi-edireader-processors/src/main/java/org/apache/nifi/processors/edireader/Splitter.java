package org.apache.nifi.processors.edireader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * EDI X12 file splitter
 * [INFO] Ugly code alert.  Just getting it working right now.
 *
 * @author <a href="mailto:mrcsparker@gmail.com">mrcsparker@gmail.com</a>
 */
class Splitter {
    private static final int HEADER_LENGTH = 106;
    private static final int SEGMENT_POSITION = 105;
    private static final int ELEMENT_POSITION = 3;
    private final BufferedReader inputReader;

    private Character segmentSeparator;
    private Character elementSeparator;

    public Splitter(InputStream inputStream) {
        this.inputReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public static List<Map<String, String>> splitFile(InputStream inputStream) throws IOException {
        Splitter splitter = new Splitter(inputStream);
        return splitter.split();
    }

    public final List<Map<String, String>> split() throws IOException {

        List<Map<String, String>> results = new ArrayList<>();

        String isa = parseHeader();
        Interchange interchange = new Interchange(segmentSeparator, elementSeparator);
        interchange.isa = isa;

        Scanner scanner = new Scanner(inputReader);
        scanner.useDelimiter(Pattern.quote(segmentSeparator.toString()));

        while(scanner.hasNext()) {
            String segment = scanner.next().trim();
            String[] elements = segment.split(Pattern.quote(elementSeparator.toString()));
            switch (elements[0]) {
                case "ISA":
                    interchange = new Interchange(segmentSeparator, elementSeparator);
                    interchange.isa = segment;
                    break;
                case "GS":
                    interchange.gs = segment;
                    break;
                case "GE":
                    interchange.ge = segment;
                    break;
                case "IEA":
                    interchange.iea = segment;
                    results.add(interchange.writer());
                    break;
                case "ST":
                    interchange.transactions.add(new Transaction(segment));
                    interchange.transactions.get(interchange.transactions.size() - 1).segments.add(segment);
                    break;
                case "SE":
                    interchange.transactions.get(interchange.transactions.size() - 1).segments.add(segment);
                    break;
                default:
                    interchange.transactions.get(interchange.transactions.size() - 1).segments.add(segment);
                    break;

            }
        }

        return results;
    }

    private String parseHeader() throws IOException {
        char[] buf = new char[HEADER_LENGTH];

        int size = inputReader.read(buf);
        if (size != HEADER_LENGTH) {
            throw new IOException("Size is not " + HEADER_LENGTH + " but " + size);
        }

        segmentSeparator = buf[SEGMENT_POSITION];
        elementSeparator = buf[ELEMENT_POSITION];

        return testIsaSegment(buf);
    }

    private String testIsaSegment(char[] buf) throws IOException {
        List<String> l = new ArrayList<>();

        String isaSegment = new String(buf);

        Scanner scanner = new Scanner(isaSegment.substring(0, SEGMENT_POSITION));
        scanner.useDelimiter(Pattern.quote(elementSeparator.toString()));
        while (scanner.hasNext()) {
            l.add(scanner.next());
        }
        scanner.close();
        if (!l.get(0).equals("ISA")) {
            throw new IOException("Not valid file format. Got " + l.get(0) + " instead of ISA. " + elementSeparator.toString());
        }
        return l.stream().collect(Collectors.joining(elementSeparator.toString()));
    }
}
