package org.apache.nifi.processors.edireader;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SplitterTest {
    private static final Logger log = Logger.getLogger(SplitterTest.class.getName());

    @Test
    public void wont_split_simple_file() throws IOException {
        String f = ediFile("/files/pharmacy-5010.835");

        Splitter splitter = new Splitter(new FileInputStream(f));
        List<Map<String, String>> output = splitter.split();

        assertEquals(output.size(), 1);

        assertTrue(output.get(0).containsKey("EMEDNYBAT-ETIN-00501-006000600-HP-EMEDNYBAT-ETIN-20100101-1050-005010X221A1-1740.835"));

    }

    @Test
    public void will_split_double_file() throws IOException {
        String f = ediFile("/files/double-isa-file-5010.835");

        Splitter splitter = new Splitter(new FileInputStream(f));
        List<Map<String, String>> output = splitter.split();

        assertEquals(output.size(), 2);

        assertTrue(output.get(0).containsKey("EMEDNYBAZ-ETIN-00501-006000600-HP-EMEDNYBAT-ETIN-20100101-1050-005010X221A1-1740.835"));
        assertTrue(output.get(1).containsKey("EMEDNYBAT-ETIN-00501-006000600-HP-EMEDNYBAT-ETIN-20100101-1050-005010X221A1-1740.835"));

    }

    private String ediFile(String fileName) {
        return this.getClass().getResource(fileName).getPath();
    }
}

