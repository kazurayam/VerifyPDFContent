# Verify PDF Content

This is a small Katalon Studio project for demonstration purpose.
You can download the zip from the Releases page, unzip it, open it in your local Katalon Studio.

This project was developed using KS ver8.3.0, but it should work with any version.

This project was developed to propose a solution to a post in the Katalon Community Forum

- [How to assert on content of downloaded PDF file](https://forum.katalon.com/t/how-to-assert-on-content-of-downloaded-pdf-file/78469)

## Resolving external dependencies

This project uses Gradle to resolve external dependencies. You need Gradle is installed on your PC.

You want to execute:

```
$ cd ${VerifyPDFContent}
$ gradle drivers
```

Then you will find a few jar files are installed in the `Drivers` folder.

## Running the test

Just run `Test Cases/TC1`

## How it works

Read the source of [Test Cases/TC1](Scripts/TC1/Script1663050702597.groovy)

