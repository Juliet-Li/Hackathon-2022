package org.nrg.xnat.dicomtonifti;

import org.nrg.framework.annotations.XnatPlugin;
import org.springframework.context.annotation.ComponentScan;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@XnatPlugin(value = "nrg_plugin_dicomtonifti", name = "XNAT 1.7 Dicom to Nifti Plugin", description = "This is the XNAT 1.7 Dicom to Nifti Plugin.")
@ComponentScan({"org.nrg.xnat.workflow.listeners"})
public class DicomToNiftiPlugin {
    
    public static void main(String[] args) {
        String scanName = "J-Test";
        // We can get a list of providerIds from https://api.notion.com/v1/databases/:id - Provider object
        String providerId = "30b9c9f1-b5c5-4f2c-b8aa-2fea36dac7e3";
        Unirest.config().enableCookieManagement(false);
        HttpResponse<String> response = Unirest.post("https://api.notion.com/v1/pages/")
                .header("Authorization", "Bearer secret_Dhr0z7rk7E0bKQyLYQAZuHIT5J0QDfasSwAJufrtfU3")
                .header("Content-Type", "application/json")
                .header("Notion-Version", "2022-06-28")
                .body(
                        "{\n    \"parent\": {\n        \"type\": \"database_id\",\n        \"database_id\": \"b96b9b82-6d67-4dc1-834b-1d769ad82199\"\n    },\n    \"properties\": {\n        \"Project\": {\n            \"id\": \"%3CKJu\",\n            \"type\": \"multi_select\",\n            \"multi_select\": [\n                {\n                    \"id\": \"59517789-83d6-41b2-bb0c-f791743eccd9\",\n                    \"name\": \"Research\",\n                    \"color\": \"pink\"\n                }\n            ]\n        },\n        \"Status\": {\n            \"id\": \"%3DoTJ\",\n            \"type\": \"select\",\n            \"select\": {\n                \"id\": \"56ada63c-2395-496f-a3ba-b3ea3d950507\",\n                \"name\": \"XNAT_Uploaded\",\n                \"color\": \"default\"\n            }\n        },\n        \"Provider\": {\n            \"id\": \"Ev%7Cr\",\n            \"type\": \"multi_select\",\n            \"multi_select\": [\n                {\n                    \"id\": \"" + providerId + "\"\n                    }\n            ]\n        },\n        \"Due Date\": {\n            \"id\": \"niUe\",\n            \"type\": \"date\",\n            \"date\": null\n        },\n        \"Team Member Responsible\": {\n            \"id\": \"sY%7CM\",\n            \"type\": \"people\",\n            \"people\": [\n                {\n                    \"object\": \"user\",\n                    \"id\": \"a7d837a5-8bb8-411d-af3b-aa8c24e1e2c6\"\n                }\n            ]\n        },\n        \"Attachment\": {\n            \"id\": \"xzGC\",\n            \"type\": \"files\",\n            \"files\": []\n        },\n        \"PatientID_ScanDate\": {\n            \"id\": \"title\",\n            \"type\": \"title\",\n            \"title\": [\n                {\n                    \"type\": \"text\",\n                    \"text\": {\n                        \"content\": \"" + scanName + "\",\n                        \"link\": null\n                    },\n                    \"annotations\": {\n                        \"bold\": false,\n                        \"italic\": false,\n                        \"strikethrough\": false,\n                        \"underline\": false,\n                        \"code\": false,\n                        \"color\": \"default\"\n                    },\n                    \"plain_text\": \"" + scanName + "\",\n                    \"href\": null\n                }\n            ]\n        }\n    }\n}")
                .asString();
        // System.out.println(response.getBody());
	    // System.out.println("Hello, World");
    }
}
