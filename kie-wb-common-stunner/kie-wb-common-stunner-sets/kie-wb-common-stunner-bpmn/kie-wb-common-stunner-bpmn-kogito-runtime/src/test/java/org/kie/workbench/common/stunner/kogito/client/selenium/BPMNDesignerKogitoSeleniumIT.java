/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.kogito.client.selenium;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.assertj.XmlAssert;

import static org.apache.commons.io.FileUtils.copyFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class BPMNDesignerKogitoSeleniumIT {

    private static final Logger LOG = LoggerFactory.getLogger(BPMNDesignerKogitoSeleniumIT.class);

    private static final String SET_CONTENT_TEMPLATE =
            "gwtEditorBeans.get(\"BPMNDiagramEditor\").get().setContent(\"\", '%s')";
    private static final String GET_CONTENT_TEMPLATE =
            "return gwtEditorBeans.get(\"BPMNDiagramEditor\").get().getContent()";


    private static final String INDEX_HTML = "target/kie-wb-common-stunner-bpmn-kogito-runtime/index.html";
    private static final String INDEX_HTML_PATH = "file:///" + new File(INDEX_HTML).getAbsolutePath();



    private static final String NOT_PRESENT_IN_NAVIGATOR = "' was not present in the process navigator";
    private static final String PROPERTIES_PANEL = "qe-docks-item-E-DiagramEditorPropertiesScreen";
    private static final String DIAGRAM_EXPLORER = "qe-docks-item-E-ProjectDiagramExplorerScreen";
    private static final String DIAGRAM_EXPLORER_EXPANDED = "qe-docks-bar-expanded-E";
    private static final String DIAGRAM_PANEL = "qe-static-workbench-panel-view";
    private static final String ACE_EDITOR = "//div[@class='ace_content']";
    private static final String PALETTE = "//div[@data-field=\"kie-palette\"]";
    private static final String PALETTE_START_EVENTS_CATEGORY_BUTTON = "//button[@data-field='categoryItem', @title=`Start Events`]";
    private static final String ERROR_MODAL_DIALOG = "//div[@class='modal-dialog']";
    private static final String ERROR_MODAL_BODY = "//div[@class='modal-body']";

    private static final String CREATE_NEW_DIAGRAM_BUTTON = "//input[@value=\"Create new Diagram\"]";

    private static final String PROCESS_NODE = "//div[@data-field='explorerPanelBody']//a[text()='%s']";

    private static final Boolean HEADLESS = Boolean.valueOf(System.getProperty("org.kie.bpmn.kogito.browser.headless"));
    private static final String SCREENSHOTS_DIR = System.getProperty("org.kie.bpmn.kogito.screenshots.dir");

    /**
     * Selenium web driver
     */
    private WebDriver driver;

    /**
     * Properties panel of BPMN Designer
     */
    private WebElement propertiesPanel;

    /**
     * Start events category button in Palette
     */
    private WebElement paletteStartEventCategory;

    /**
     * Explore diagram panel of BPMN Designer
     */
    private WebElement bpmnDesignerExplorerButton;


    @BeforeClass
    public static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
    }

    @Before
    public void openBPMNDesigner() {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setHeadless(HEADLESS);
        driver = new FirefoxDriver(firefoxOptions);
        driver.manage().window().maximize();

        driver.get(INDEX_HTML_PATH);

        // init diagram to initial state to see if we can load properties panel and diagram explorer
        setContent("");
       ((JavascriptExecutor) driver).executeScript(String.format(SET_CONTENT_TEMPLATE, ""));

        propertiesPanel = waitOperation()
                .until(visibilityOfElementLocated(className(PROPERTIES_PANEL)));
        assertThat(propertiesPanel)
                .as("Presence of properties panel expand button is prerequisite for all tests")
                .isNotNull();

        bpmnDesignerExplorerButton = waitOperation()
                .until(visibilityOfElementLocated(className(DIAGRAM_EXPLORER)));
        assertThat(bpmnDesignerExplorerButton)
                .as("Presence of Explorer Diagram is prerequisite for all tests")
                .isNotNull();
    }

    private final File screenshotDirectory = initScreenshotDirectory();

    @Rule
    public TestWatcher takeScreenShotAndCleanUp = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            final File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            final String testClassName = description.getTestClass().getSimpleName();
            final String testMethodName = description.getMethodName();
            final String filename = testClassName + "_" + testMethodName;
            try {
                copyFile(screenshotFile, new File(screenshotDirectory, filename + ".png"));
            } catch (IOException ioe) {
                LOG.error("Unable to take screenshot", ioe);
            }
        }

        @Override
        protected void finished(Description description) {
            if (driver != null) {
                driver.quit();
            }
        }
    };


    @Test
    public void testHandlingInvalidContent() {
        setContent("<!!!invalid!!!>");

        // Check that there is error modal dialog shown to user
        final WebElement errorDialogModal = waitOperation().until(element(ERROR_MODAL_DIALOG));
        assertThat(errorDialogModal)
                .as("If invalid dmn is loaded, error dialog modal should be shown.")
                .isNotNull();
        // Check that there is a message explaining what happened
        assertThat(errorDialogModal.findElement(By.xpath(ERROR_MODAL_BODY)).getText())
                .as("If error dialog is shown, it show show proper message.")
                .contains("Invalid BPMN file. Opening default text editor instead.");
        errorDialogModal.click();

        // Verify ACE editor (default text editor) is in place and shown to user
        final WebElement aceEditor = waitOperation().until(element(ACE_EDITOR));
        assertThat(aceEditor)
                .as("If invalid dmn is loaded, ace editor needs to be shown")
                .isNotNull();
    }

    @Test
    public void testNewDiagram() throws Exception {
        final String expected = loadResource("new-diagram.bpmn2");
        setContent("");

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        // Skip, id, name and namespace in the comparison - they are dynamic
        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .withAttributeFilter(
                        attr -> !(Objects.equals(attr.getName(), "id")
                                || Objects.equals(attr.getName(), "name")
                                || Objects.equals(attr.getName(), "namespace")))
                .withNodeFilter(
                        node -> !(Objects.equals(node.getNodeName(), "bpmn2:source")
                                || Objects.equals(node.getNodeName(), "bpmn2:target")))
                .areIdentical();
    }

    @Test
    public void testBasicModel() throws Exception {
        final String expected = loadResource("basic-process.bpmn2");
        setContent(expected);


        assertDiagramNodeIsPresentInProcessNavigator("Start");
        assertDiagramNodeIsPresentInProcessNavigator("Add user to database");
        assertDiagramNodeIsPresentInProcessNavigator("End");

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    private void assertDiagramNodeIsPresentInProcessNavigator(final String nodeName) {
        expandBpmnNavigatorDock();
        final WebElement node = waitOperation().until(element(PROCESS_NODE, nodeName));
        assertThat(node)
                .as("Node '" + nodeName + NOT_PRESENT_IN_NAVIGATOR)
                .isNotNull();
        collapseBpmnNavigatorDock();
    }

    private void expandBpmnNavigatorDock() {
        bpmnDesignerExplorerButton.click();
    }

    private void collapseBpmnNavigatorDock() {
        final WebElement expandedDiagramNavigator = waitOperation()
                .until(visibilityOfElementLocated(className(DIAGRAM_EXPLORER_EXPANDED)));
        assertThat(expandedDiagramNavigator)
                .as("Unable to locate expanded decision navigator dock")
                .isNotNull();

        expandedDiagramNavigator.findElement(className("fa")).click();
    }


    private File initScreenshotDirectory() {
        if (SCREENSHOTS_DIR == null) {
            throw new IllegalStateException(
                    "Property org.kie.dmn.kogito.screenshots.dir (where screenshot taken by WebDriver will be put) was null");
        }
        File scd = new File(SCREENSHOTS_DIR);
        if (!scd.exists()) {
            boolean mkdirSuccess = scd.mkdir();
            if (!mkdirSuccess) {
                throw new IllegalStateException("Creation of screenshots dir failed " + scd);
            }
        }
        if (!scd.canWrite()) {
            throw new IllegalStateException("The screenshotDir must be writable" + scd);
        }
        return scd;
    }

    private void setContent(final String xml) {
        try {
            ((JavascriptExecutor) driver).executeScript(String.format(SET_CONTENT_TEMPLATE, xml));
        } catch (Exception e) {
            LOG.error("Exception during JS execution. Ex: {}", e.getMessage());
        }
        final WebElement designer = waitOperation()
                .until(visibilityOfElementLocated(className(DIAGRAM_PANEL)));
        assertThat(designer)
                .as("Designer was not loaded")
                .isNotNull();
    }

    private String getContent() {
        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_CONTENT_TEMPLATE));
        assertThat(result).isInstanceOf(String.class);
        return (String) result;
    }
    /**
     * Use this for loading BPMN model placed in src/test/resources
     * @param filename
     * @return Text content of the file
     * @throws IOException
     */
    private String loadResource(final String filename) throws IOException {
        return IOUtils.readLines(this.getClass().getResourceAsStream(filename), StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.joining(""));
    }


    private ExpectedCondition<WebElement> element(final String xpathLocator, final String... parameters) {
        return visibilityOfElementLocated(xpath(String.format(xpathLocator, parameters)));
    }

    private WebDriverWait waitOperation() {
        return new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    }
}
