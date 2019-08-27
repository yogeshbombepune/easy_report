# Report Automation

Report Automation is a Java library for dealing with generate date deriven report .

## Installation

Need Apache PdfBox and FontBox Library.

```bash
<dependency>
            <groupId>org.apache.pdfbox</groupId>
            <artifactId>pdfbox</artifactId>
            <version>2.0.16</version>
</dependency>
<dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi</artifactId>
            <version>3.8</version>
</dependency>
<dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>3.8</version>
</dependency>
```

## Usage

```
Just Run the Driver class main method
```

## Features
1. Table rendering on pdf with fix column (eg. date is fix for all pages).
2. Automatic table spliting. 
3. Configuration of your own font.
4. Configuration of your logo image.
5. Automated page number rendering.
6. Rending of images or graph images on pdf.
7. Render multiple table in single pdf file.
8. Table header multi line support.
9. Rendering CSV file.
10.Rendering Excel file.
11.Rendering Xml File.
12.Rendering Json File.


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
Apache PdfBox
