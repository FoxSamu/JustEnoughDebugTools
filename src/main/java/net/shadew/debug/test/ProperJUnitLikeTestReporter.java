package net.shadew.debug.test;

import com.google.common.base.Stopwatch;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestReporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class ProperJUnitLikeTestReporter implements TestReporter {
   private final Document document;
   private final Element testSuite;
   private final Stopwatch stopwatch;
   private final File destination;
   private int failures;
   private int skips;
   private int successes;

   public ProperJUnitLikeTestReporter(File file) throws ParserConfigurationException {
      this.destination = file;
      this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      this.testSuite = this.document.createElement("testsuite");
      Element element = this.document.createElement("testsuites");
      element.appendChild(this.testSuite);
      this.document.appendChild(element);
      this.testSuite.setAttribute("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
      this.stopwatch = Stopwatch.createStarted();
   }

   private Element createTestCase(GameTestInfo gameTestInfo, String string) {
      Element element = this.document.createElement("testcase");
      element.setAttribute("name", string);
      element.setAttribute("classname", gameTestInfo.getStructureName());
      element.setAttribute("time", String.valueOf((double) gameTestInfo.getRunTime() / 1000.0D));
      this.testSuite.appendChild(element);
      return element;
   }

   @SuppressWarnings("ConstantConditions")
   @Override
   public void onTestFailed(GameTestInfo gameTestInfo) {
      String string = gameTestInfo.getTestName();
      String string2 = gameTestInfo.getError().getMessage();
      Element failureElement;
      if (gameTestInfo.isRequired()) {
         failures++;
         failureElement = this.document.createElement("failure");
      } else {
         skips++;
         failureElement = this.document.createElement("skipped");
      }
      failureElement.setAttribute("message", string2);

      Element element3 = this.createTestCase(gameTestInfo, string);
      element3.appendChild(failureElement);
   }

   @Override
   public void onTestSuccess(GameTestInfo gameTestInfo) {
      successes++;
      String string = gameTestInfo.getTestName();
      this.createTestCase(gameTestInfo, string);
   }

   @Override
   public void finish() {
      this.stopwatch.stop();
      this.testSuite.setAttribute("tests", "" + (successes + failures + skips));
      this.testSuite.setAttribute("name", "root");
      this.testSuite.setAttribute("failures", "" + failures);
      this.testSuite.setAttribute("skipped", "" + skips);
      this.testSuite.setAttribute("time", String.valueOf((double) this.stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0D));

      try {
         this.save(this.destination);
      } catch (TransformerException var2) {
         throw new Error("Couldn't save test report", var2);
      }
   }

   public void save(File file) throws TransformerException {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource dOMSource = new DOMSource(this.document);
      StreamResult streamResult = new StreamResult(file);
      transformer.transform(dOMSource, streamResult);
   }
}
