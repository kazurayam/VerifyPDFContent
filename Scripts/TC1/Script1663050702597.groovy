import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

import com.kazurayam.materialstore.filesystem.FileType
import com.kazurayam.materialstore.filesystem.JobName
import com.kazurayam.materialstore.filesystem.JobTimestamp
import com.kazurayam.materialstore.filesystem.Material
import com.kazurayam.materialstore.filesystem.MaterialList
import com.kazurayam.materialstore.filesystem.Metadata
import com.kazurayam.materialstore.filesystem.QueryOnMetadata
import com.kazurayam.materialstore.filesystem.Store
import com.kazurayam.materialstore.filesystem.Stores
import com.kazurayam.materialstore.map.MappedResultSerializer
import com.kazurayam.materialstore.mapper.PDF2HTMLMapper
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.testobject.ConditionType

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

TestObject makeTestObject(String xpath) {
	TestObject tObj = new TestObject(xpath)
	tObj.addProperty("xpath", ConditionType.EQUALS, xpath)
	return tObj
}
/**
 * This script is a Test Case in Katalon Studio that does the following:
 * 1. download a PDF file from a URL (https://www.fsa.go.jp/policy/nisa/20170614-2/24.pdf)
 * 2. verify if the PDF contains a string text "PayPay" and print message "found" or "not found"
 * 
 * This scripts converts a PDF file into a equivalent HTML.
 * The conversion is done by calling the Materialstore library (>=v0.9.5).
 * The Materialstore uses the PDFBox library internally.
 * Then it opens the HTML in browser and verifies using WebUI.* keywords of Katalon Studio.
 */
// declare the given environment
Path projectDir = Paths.get(RunConfiguration.getProjectDir())

// create the store directory
Path root = projectDir.resolve("store")
Store store = Stores.newInstance(root)

// download the PDF, store it into a temporary file
URL url = new URL("https://www.fsa.go.jp/policy/nisa/20170614-2/24.pdf")
Path tempFile = Files.createTempFile(projectDir, "temp", ".pdf") 
//Path tempFile = projectDir.resolve("24.pdf")
Files.copy(url.openStream(), tempFile, StandardCopyOption.REPLACE_EXISTING)

// put the PDF into the materialstore
Metadata metadata = Metadata.builder(url).build()
JobName jobName = new JobName("NISA")
JobTimestamp ts1 = JobTimestamp.now()
store.write(jobName, ts1, FileType.PDF, metadata, tempFile)

// grasp the PDF in the materialstore
MaterialList materialList = store.select(jobName, ts1, FileType.PDF,
		QueryOnMetadata.builder(metadata).build())
assert 1 == materialList.size()

// convert the PDF into HTML
PDF2HTMLMapper mapper = new PDF2HTMLMapper()
mapper.setStore(store)
MappedResultSerializer serializer = 
		new MappedResultSerializer(store, jobName, ts1)
mapper.setMappingListener(serializer)
mapper.map(materialList.get(0))

// make sure that an HTML file has been created
MaterialList result = 
		store.select(jobName, ts1, FileType.HTML)
assert 1 == result.size()

// verify the HTML using WebDriver
Material html = result.get(0)
File htmlFile = html.toFile(store)
WebUI.openBrowser('')
WebUI.navigateToUrl(htmlFile.toURI().toURL().toExternalForm())
String xpath = "//body/div[@id='page_0']/div[contains(text(), 'PayPay')]"
WebUI.verifyElementPresent(makeTestObject(xpath), 5)
WebUI.closeBrowser()
