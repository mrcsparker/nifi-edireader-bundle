package org.apache.nifi.processors.edireader;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EdiToJSONTest {
    private TestRunner testRunner;

    EdiToJSON ediToJSON;

    @Before
    public void init() {
        ediToJSON = new EdiToJSON();
        testRunner = TestRunners.newTestRunner(ediToJSON);
        testRunner.setValidateExpressionUsage(false);
    }

    @Test
    public void testProcessor() {
        testRunner.assertValid();
    }

    @Test
    public void testOnTriggerJson() throws IOException {
        String f = ediFile("/files/pharmacy-5010.835");

        testRunner.setProperty(EdiToJSON.PRETTY_PRINT, "false");

        testRunner.enqueue(Paths.get(f));
        testRunner.run();

        testRunner.assertQueueEmpty();

        List<MockFlowFile> results = testRunner.getFlowFilesForRelationship(EdiToJSON.REL_SUCCESS);
        assertEquals("1 match", 1, results.size());

        MockFlowFile result = results.get(0);

        // Test attributes and content
        result.assertContentEquals("[{\"authorizationQualifier\":\"00\",\"authorizationInformation\":\"          \",\"securityQualifier\":\"00\",\"securityInformation\":\"          \",\"senderQualifier\":\"ZZ\",\"senderId\":\"EMEDNYBAT      \",\"receiverQualifier\":\"ZZ\",\"receiverId\":\"ETIN           \",\"date\":\"100101\",\"time\":\"1000\",\"standardsId\":null,\"version\":\"00501\",\"interchangeControlNumber\":\"006000600\",\"acknowledgmentRequested\":\"0\",\"testIndicator\":\"T\",\"functionalGroups\":[{\"functionalIdentifierCode\":\"HP\",\"applicationSenderCode\":\"EMEDNYBAT\",\"applicationReceiverCode\":\"ETIN\",\"date\":\"20100101\",\"time\":\"1050\",\"groupControlNumber\":\"6000600\",\"responsibleAgencyCode\":\"X\",\"version\":\"005010X221A1\",\"transactions\":[{\"name\":\"Health Care Claim Payment/Advice\",\"transactionSetIdentifierCode\":\"835\",\"transactionSetControlNumber\":\"1740\",\"implementationConventionReference\":null,\"segments\":[{\"BPR03\":\"C\",\"BPR14\":\"DA\",\"BPR02\":\"219.65\",\"BPR13\":\"111\",\"BPR01\":\"I\",\"BPR12\":\"01\",\"BPR10\":\"1234567890\",\"BPR09\":\"33\",\"BPR08\":\"DA\",\"BPR07\":\"111\",\"BPR06\":\"01\",\"BPR05\":\"CCP\",\"BPR16\":\"20100101\",\"BPR04\":\"ACH\",\"BPR15\":\"22\",\"id\":\"BPR\"},{\"TRN02\":\"10100000000\",\"TRN03\":\"1000000000\",\"TRN01\":\"1\",\"id\":\"TRN\"},{\"REF02\":\"ETIN\",\"REF01\":\"EV\",\"id\":\"REF\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"405\"}],\"loops\":[{\"id\":\"1000\",\"segments\":[{\"N101\":\"PR\",\"N102\":\"NYSDOH\",\"id\":\"N1\"},{\"N301\":\"OFFICE OF HEALTH INSURANCE PROGRAMS\",\"N302\":\"CORNING TOWER, EMPIRE STATE PLAZA\",\"id\":\"N3\"},{\"N401\":\"ALBANY\",\"id\":\"N4\",\"N402\":\"NY\",\"N403\":\"122370080\"},{\"PER06\":\"www.emedny.org\",\"PER05\":\"UR\",\"PER04\":\"8003439000\",\"PER03\":\"TE\",\"PER02\":\"PROVIDER SERVICES\",\"PER01\":\"BL\",\"id\":\"PER\"}]},{\"id\":\"1000\",\"segments\":[{\"N101\":\"PE\",\"N102\":\"PHARMACY CORPORATION\",\"N103\":\"XX\",\"N104\":\"9999999995\",\"id\":\"N1\"},{\"N401\":\"NEW YORK\",\"id\":\"N4\",\"N402\":\"NY\",\"N403\":\"101162253\"},{\"REF02\":\"000000000\",\"REF01\":\"TJ\",\"id\":\"REF\"}]},{\"id\":\"2000\",\"segments\":[{\"id\":\"LX\",\"LX01\":\"1\"},{\"PLB04\":\"3.99\",\"PLB02\":\"20100101\",\"PLB03\":\"\",\"PLB03-2\":\"FCN#201001010000000\",\"PLB03-1\":\"WO\",\"id\":\"PLB\",\"PLB01\":\"9999999995\"}],\"loops\":[{\"id\":\"2100\",\"segments\":[{\"CLP09\":\"0\",\"CLP04\":\"0\",\"CLP03\":\"4\",\"CLP02\":\"1\",\"id\":\"CLP\",\"CLP01\":\"PAT ACCT NUM\",\"CLP08\":\"99\",\"CLP07\":\"1000210000000030\",\"CLP06\":\"MC\"},{\"NM108\":\"MI\",\"NM109\":\"LL99999L\",\"NM104\":\"SUBMITTED FIRST\",\"NM102\":\"1\",\"id\":\"NM1\",\"NM103\":\"SUBMITTED LAST\",\"NM101\":\"QC\"},{\"NM104\":\"CORRECTED FIRST\",\"NM102\":\"1\",\"id\":\"NM1\",\"NM103\":\"CORRECTED LAST\",\"NM101\":\"74\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"232\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"233\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"036\"}],\"loops\":[{\"id\":\"2110\",\"segments\":[{\"SVC01\":\"\",\"SVC01-1\":\"N4\",\"SVC01-2\":\"68382005501\",\"SVC03\":\"0\",\"id\":\"SVC\",\"SVC02\":\"4\",\"SVC05\":\"0\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"472\"},{\"CAS03\":\"4\",\"CAS01\":\"CO\",\"CAS02\":\"27\",\"id\":\"CAS\"},{\"LQ02\":\"65\",\"LQ01\":\"RX\",\"id\":\"LQ\"}]}]},{\"id\":\"2100\",\"segments\":[{\"CLP09\":\"0\",\"CLP04\":\"0\",\"CLP03\":\"9.46\",\"CLP02\":\"1\",\"id\":\"CLP\",\"CLP01\":\"PATIENT ACCOUNT NUMBER\",\"CLP08\":\"01\",\"CLP07\":\"1000220000000030\",\"CLP06\":\"MC\"},{\"NM108\":\"MI\",\"NM109\":\"LL88888L\",\"NM104\":\"SUBMITTED FIRST\",\"NM102\":\"1\",\"id\":\"NM1\",\"NM103\":\"SUBMITTED LAST\",\"NM101\":\"QC\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"232\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"233\"}],\"loops\":[{\"id\":\"2110\",\"segments\":[{\"SVC01\":\"\",\"SVC01-1\":\"N4\",\"SVC01-2\":\"59746011306\",\"SVC03\":\"0\",\"id\":\"SVC\",\"SVC02\":\"9.46\",\"SVC05\":\"0\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"472\"},{\"CAS03\":\"9.46\",\"CAS01\":\"CO\",\"CAS02\":\"16\",\"id\":\"CAS\"},{\"LQ02\":\"77\",\"LQ01\":\"RX\",\"id\":\"LQ\"}]}]},{\"id\":\"2100\",\"segments\":[{\"CLP09\":\"0\",\"CLP04\":\"223.64\",\"CLP03\":\"256.46\",\"CLP02\":\"1\",\"id\":\"CLP\",\"CLP01\":\"PATIENT ACCOUNT NUMBER\",\"CLP08\":\"99\",\"CLP07\":\"1000300000000030\",\"CLP06\":\"MC\"},{\"NM108\":\"MI\",\"NM109\":\"LL77777L\",\"NM104\":\"SUBMITTED FIRST\",\"NM102\":\"1\",\"id\":\"NM1\",\"NM103\":\"SUBMITTED LAST\",\"NM101\":\"QC\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"232\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"233\"},{\"AMT01\":\"AU\",\"AMT02\":\"223.64\",\"id\":\"AMT\"}],\"loops\":[{\"id\":\"2110\",\"segments\":[{\"SVC01\":\"\",\"SVC01-1\":\"N4\",\"SVC01-2\":\"54092051902\",\"SVC03\":\"223.64\",\"id\":\"SVC\",\"SVC02\":\"256.46\",\"SVC05\":\"30\"},{\"DTM02\":\"20100101\",\"id\":\"DTM\",\"DTM01\":\"472\"},{\"CAS05\":\"45\",\"CAS06\":\"36.32\",\"CAS03\":\"-3.5\",\"CAS01\":\"CO\",\"CAS02\":\"91\",\"id\":\"CAS\"},{\"AMT01\":\"B6\",\"AMT02\":\"223.64\",\"id\":\"AMT\"}]}]}]}]}]}]}]");
    }

    @Test
    public void testOnTriggerPrettyJson() throws IOException {
        String f = ediFile("/files/pharmacy-5010.835");
        String output = ediFile("/files/pharmacy-5010.835.pretty.json");

        testRunner.setProperty(EdiToJSON.PRETTY_PRINT, "true");

        testRunner.enqueue(Paths.get(f));
        testRunner.run();

        testRunner.assertQueueEmpty();

        List<MockFlowFile> results = testRunner.getFlowFilesForRelationship(EdiToJSON.REL_SUCCESS);
        assertEquals("1 match", 1, results.size());

        MockFlowFile result = results.get(0);

        byte[] bytes = Files.readAllBytes(Paths.get(output));

        // Test attributes and content
        result.assertContentEquals(bytes);
    }

    @Test
    public void testOnTriggerPrettyJsonDouble() throws IOException {
        String f = ediFile("/files/double-isa-file-5010.835");
        String output = ediFile("/files/double-isa-file-5010.835.pretty.json");

        testRunner.setProperty(EdiToJSON.PRETTY_PRINT, "true");

        testRunner.enqueue(Paths.get(f));
        testRunner.run();

        testRunner.assertQueueEmpty();

        List<MockFlowFile> results = testRunner.getFlowFilesForRelationship(EdiToJSON.REL_SUCCESS);
        assertEquals("1 match", 1, results.size());

        MockFlowFile result = results.get(0);

        byte[] bytes = Files.readAllBytes(Paths.get(output));

        // Test attributes and content
        result.assertContentEquals(bytes);
    }

    private String ediFile(String fileName) {
        return this.getClass().getResource(fileName).getPath();
    }

}
