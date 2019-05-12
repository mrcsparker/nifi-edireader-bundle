package org.apache.nifi.processors.edireader;

import org.apache.commons.io.FileUtils;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EdiToXMLTest {

    private TestRunner testRunner;

    @Before
    public void init() {
        testRunner = TestRunners.newTestRunner(new EdiToXML());
        testRunner.setValidateExpressionUsage(false);
    }

    @Test
    public void testProcessor() {
        testRunner.assertValid();
    }

    @Test
    public void testOnTriggerSuccess() throws IOException {
        String f = ediFile("/files/pharmacy-5010.835");

        testRunner.enqueue(Paths.get(f));
        testRunner.run();

        testRunner.assertQueueEmpty();

        List<MockFlowFile> results = testRunner.getFlowFilesForRelationship(EdiToXML.REL_SUCCESS);
        assertEquals("1 match", 1, results.size());

        MockFlowFile result = results.get(0);

        // Test attributes and content
        result.assertContentEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ediroot><interchange Standard=\"ANSI X.12\" AuthorizationQual=\"00\" Authorization=\"          \" SecurityQual=\"00\" Security=\"          \" Date=\"100101\" Time=\"1000\" Version=\"00501\" Control=\"006000600\" AckRequest=\"0\" TestIndicator=\"T\"><sender><address Id=\"EMEDNYBAT      \" Qual=\"ZZ\"/></sender><receiver><address Id=\"ETIN           \" Qual=\"ZZ\"/></receiver><group GroupType=\"HP\" ApplSender=\"EMEDNYBAT\" ApplReceiver=\"ETIN\" Date=\"20100101\" Time=\"1050\" Control=\"6000600\" StandardCode=\"X\" StandardVersion=\"005010X221A1\"><transaction DocType=\"835\" Name=\"Health Care Claim Payment/Advice\" Control=\"1740\"><segment Id=\"BPR\"><element Id=\"BPR01\">I</element><element Id=\"BPR02\">219.65</element><element Id=\"BPR03\">C</element><element Id=\"BPR04\">ACH</element><element Id=\"BPR05\">CCP</element><element Id=\"BPR06\">01</element><element Id=\"BPR07\">111</element><element Id=\"BPR08\">DA</element><element Id=\"BPR09\">33</element><element Id=\"BPR10\">1234567890</element><element Id=\"BPR12\">01</element><element Id=\"BPR13\">111</element><element Id=\"BPR14\">DA</element><element Id=\"BPR15\">22</element><element Id=\"BPR16\">20100101</element></segment><segment Id=\"TRN\"><element Id=\"TRN01\">1</element><element Id=\"TRN02\">10100000000</element><element Id=\"TRN03\">1000000000</element></segment><segment Id=\"REF\"><element Id=\"REF01\">EV</element><element Id=\"REF02\">ETIN</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">405</element><element Id=\"DTM02\">20100101</element></segment><loop Id=\"1000\"><segment Id=\"N1\"><element Id=\"N101\">PR</element><element Id=\"N102\">NYSDOH</element></segment><segment Id=\"N3\"><element Id=\"N301\">OFFICE OF HEALTH INSURANCE PROGRAMS</element><element Id=\"N302\">CORNING TOWER, EMPIRE STATE PLAZA</element></segment><segment Id=\"N4\"><element Id=\"N401\">ALBANY</element><element Id=\"N402\">NY</element><element Id=\"N403\">122370080</element></segment><segment Id=\"PER\"><element Id=\"PER01\">BL</element><element Id=\"PER02\">PROVIDER SERVICES</element><element Id=\"PER03\">TE</element><element Id=\"PER04\">8003439000</element><element Id=\"PER05\">UR</element><element Id=\"PER06\">www.emedny.org</element></segment></loop><loop Id=\"1000\"><segment Id=\"N1\"><element Id=\"N101\">PE</element><element Id=\"N102\">PHARMACY CORPORATION</element><element Id=\"N103\">XX</element><element Id=\"N104\">9999999995</element></segment><segment Id=\"N4\"><element Id=\"N401\">NEW YORK</element><element Id=\"N402\">NY</element><element Id=\"N403\">101162253</element></segment><segment Id=\"REF\"><element Id=\"REF01\">TJ</element><element Id=\"REF02\">000000000</element></segment></loop><loop Id=\"2000\"><segment Id=\"LX\"><element Id=\"LX01\">1</element></segment><loop Id=\"2100\"><segment Id=\"CLP\"><element Id=\"CLP01\">PAT ACCT NUM</element><element Id=\"CLP02\">1</element><element Id=\"CLP03\">4</element><element Id=\"CLP04\">0</element><element Id=\"CLP06\">MC</element><element Id=\"CLP07\">1000210000000030</element><element Id=\"CLP08\">99</element><element Id=\"CLP09\">0</element></segment><segment Id=\"NM1\"><element Id=\"NM101\">QC</element><element Id=\"NM102\">1</element><element Id=\"NM103\">SUBMITTED LAST</element><element Id=\"NM104\">SUBMITTED FIRST</element><element Id=\"NM108\">MI</element><element Id=\"NM109\">LL99999L</element></segment><segment Id=\"NM1\"><element Id=\"NM101\">74</element><element Id=\"NM102\">1</element><element Id=\"NM103\">CORRECTED LAST</element><element Id=\"NM104\">CORRECTED FIRST</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">232</element><element Id=\"DTM02\">20100101</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">233</element><element Id=\"DTM02\">20100101</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">036</element><element Id=\"DTM02\">20100101</element></segment><loop Id=\"2110\"><segment Id=\"SVC\"><element Id=\"SVC01\" Composite=\"yes\"><subelement Sequence=\"1\">N4</subelement><subelement Sequence=\"2\">68382005501</subelement></element><element Id=\"SVC02\">4</element><element Id=\"SVC03\">0</element><element Id=\"SVC05\">0</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">472</element><element Id=\"DTM02\">20100101</element></segment><segment Id=\"CAS\"><element Id=\"CAS01\">CO</element><element Id=\"CAS02\">27</element><element Id=\"CAS03\">4</element></segment><segment Id=\"LQ\"><element Id=\"LQ01\">RX</element><element Id=\"LQ02\">65</element></segment></loop></loop><loop Id=\"2100\"><segment Id=\"CLP\"><element Id=\"CLP01\">PATIENT ACCOUNT NUMBER</element><element Id=\"CLP02\">1</element><element Id=\"CLP03\">9.46</element><element Id=\"CLP04\">0</element><element Id=\"CLP06\">MC</element><element Id=\"CLP07\">1000220000000030</element><element Id=\"CLP08\">01</element><element Id=\"CLP09\">0</element></segment><segment Id=\"NM1\"><element Id=\"NM101\">QC</element><element Id=\"NM102\">1</element><element Id=\"NM103\">SUBMITTED LAST</element><element Id=\"NM104\">SUBMITTED FIRST</element><element Id=\"NM108\">MI</element><element Id=\"NM109\">LL88888L</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">232</element><element Id=\"DTM02\">20100101</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">233</element><element Id=\"DTM02\">20100101</element></segment><loop Id=\"2110\"><segment Id=\"SVC\"><element Id=\"SVC01\" Composite=\"yes\"><subelement Sequence=\"1\">N4</subelement><subelement Sequence=\"2\">59746011306</subelement></element><element Id=\"SVC02\">9.46</element><element Id=\"SVC03\">0</element><element Id=\"SVC05\">0</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">472</element><element Id=\"DTM02\">20100101</element></segment><segment Id=\"CAS\"><element Id=\"CAS01\">CO</element><element Id=\"CAS02\">16</element><element Id=\"CAS03\">9.46</element></segment><segment Id=\"LQ\"><element Id=\"LQ01\">RX</element><element Id=\"LQ02\">77</element></segment></loop></loop><loop Id=\"2100\"><segment Id=\"CLP\"><element Id=\"CLP01\">PATIENT ACCOUNT NUMBER</element><element Id=\"CLP02\">1</element><element Id=\"CLP03\">256.46</element><element Id=\"CLP04\">223.64</element><element Id=\"CLP06\">MC</element><element Id=\"CLP07\">1000300000000030</element><element Id=\"CLP08\">99</element><element Id=\"CLP09\">0</element></segment><segment Id=\"NM1\"><element Id=\"NM101\">QC</element><element Id=\"NM102\">1</element><element Id=\"NM103\">SUBMITTED LAST</element><element Id=\"NM104\">SUBMITTED FIRST</element><element Id=\"NM108\">MI</element><element Id=\"NM109\">LL77777L</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">232</element><element Id=\"DTM02\">20100101</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">233</element><element Id=\"DTM02\">20100101</element></segment><segment Id=\"AMT\"><element Id=\"AMT01\">AU</element><element Id=\"AMT02\">223.64</element></segment><loop Id=\"2110\"><segment Id=\"SVC\"><element Id=\"SVC01\" Composite=\"yes\"><subelement Sequence=\"1\">N4</subelement><subelement Sequence=\"2\">54092051902</subelement></element><element Id=\"SVC02\">256.46</element><element Id=\"SVC03\">223.64</element><element Id=\"SVC05\">30</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">472</element><element Id=\"DTM02\">20100101</element></segment><segment Id=\"CAS\"><element Id=\"CAS01\">CO</element><element Id=\"CAS02\">91</element><element Id=\"CAS03\">-3.5</element><element Id=\"CAS05\">45</element><element Id=\"CAS06\">36.32</element></segment><segment Id=\"AMT\"><element Id=\"AMT01\">B6</element><element Id=\"AMT02\">223.64</element></segment></loop></loop></loop><segment Id=\"PLB\"><element Id=\"PLB01\">9999999995</element><element Id=\"PLB02\">20100101</element><element Id=\"PLB03\" Composite=\"yes\"><subelement Sequence=\"1\">WO</subelement><subelement Sequence=\"2\">FCN#201001010000000</subelement></element><element Id=\"PLB04\">3.99</element></segment></transaction></group></interchange></ediroot>");
    }

    @Test
    public void testOnTriggerError() throws IOException {
        String f = ediFile("/files/pharmacy-5010-bad.835");

        testRunner.enqueue(Paths.get(f));
        testRunner.run();

        testRunner.assertQueueEmpty();

        List<MockFlowFile> results = testRunner.getFlowFilesForRelationship(EdiToXML.REL_FAILURE);
        assertEquals("1 match", 1, results.size());

        MockFlowFile result = results.get(0);
        File resultData = FileUtils.getFile(f);
        result.assertContentEquals(FileUtils.readFileToString(resultData, Charset.defaultCharset()));
    }

    private String ediFile(String fileName) {
        return this.getClass().getResource(fileName).getPath();
    }
}
