package com.example.employee.helper;

import com.example.employee.model.entity.Employees;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {
    public static String TYPE = "text/csv";
    static String[] HEADERs = {
            "address",
            "dateOfBirth",
            "departmentId",
            "email",
            "firstName",
            "lastName",
            "phoneNumber"
    };

    public static boolean hasCSVFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public static List<Employees> csvToEmployees(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<Employees> employees = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                Employees employee = new Employees(
                        Long.parseLong(csvRecord.get("departmentId")),
                        csvRecord.get("firstName"),
                        csvRecord.get("lastName"),
                        Date.valueOf(csvRecord.get("dateOfBirth")),
                        csvRecord.get("address"),
                        csvRecord.get("email"),
                        csvRecord.get("phoneNumber")
                );

                //Fix for records of csv field
                try{

                }catch (Exception e){

                }
                employees.add(employee);
            }
            return employees;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

}
