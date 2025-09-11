# Report Service

- Example implementation of the strategy pattern using Java and Spring
- Generates a report for the specified department
  - Always returns a PDF and a file size, but the content depends on the department

### API
- **URI:** /v1/reports
- **Method:** GET
- **Request Parameters:**
  - client-id: identifier used to specify the type of report to generate. Must be one of the following values:
    - finance
    - marketing
    - information_technology
- **Response:**
  - JSON object with two fields:
    - fileSizeInBytes: long
    - rawPdfFile: byte[]

### Running Locally

Run the application and send a GET request from the terminal to generate the report.
```declarative
>> curl -s "http://localhost:8080/v1/reports?client-id=finance" \
| jq -r '.rawPdfFile' \
| base64 -D > report.pdf
```
Open the report.
```declarative
>> open report.pdf
```