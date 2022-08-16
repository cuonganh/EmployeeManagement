package com.example.employee.service;

import com.example.employee.common.Constant;
import com.example.employee.common.enumerate.EEmployee;
import com.example.employee.model.dto.EmployeeBean;
import com.example.employee.model.dto.EmployeeDto;
import com.example.employee.model.dto.PageDto;
import com.example.employee.model.dto.ProjectInfo;
import com.example.employee.model.entity.Employees;
import com.example.employee.model.entity.Projects;
import com.example.employee.model.entity.Teams;
import com.example.employee.model.exception.ResourceNotFoundException;
import com.example.employee.model.payload.EmployeeRequest;
import com.example.employee.model.payload.EmployeeResponse;
import com.example.employee.model.payload.repository.EmployeeRepository;
import com.example.employee.model.payload.repository.ProjectRepository;
import com.example.employee.model.payload.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final EntityManager entityManager;

    @Value("${exportFolder}")
    String exportFolder;


    private final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeResponse<List<EmployeeBean>> getEmployee(
            EntityManager entityManager,
            Long employeeId
    ) throws ResourceNotFoundException {

        LOGGER.info(Constant.START);
        LOGGER.info("Get employee by id: " + employeeId);
        List<EmployeeBean> employeeBeen = employeeRepository.getEmployeeBeen(entityManager, employeeId);
        if(employeeBeen.get(0).getEmployeeId() == null) {
            throw new ResourceNotFoundException();
        }
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Found Employee", employeeBeen);

    }

    public PageDto<EmployeeDto> getEmployees(
            Long departmentId,
            Long projectId,
            Integer limit,
            Integer offset,
            String sort,
            List<String> sortBy
    ) {
        LOGGER.info(Constant.START);
        LOGGER.info("Get employees list");

        if(limit == null) limit = 10;
        if(offset == null) offset = 0;
        if(CollectionUtils.isEmpty(sortBy)){
            sortBy = new ArrayList<>();
            sortBy.add("employeeId");
        }
        List<EmployeeBean> employees = employeeRepository.getAllEmployeeBeen(
                entityManager,
                departmentId,
                projectId,
                limit,
                offset,
                sort,
                sortBy
        );
        Long total =employeeRepository.countByCondition(entityManager, departmentId, projectId);
        LOGGER.info(Constant.END);
        return new PageDto<>(200, "Found employees", limit, offset, total, employees);
    }

    @Transactional
    public EmployeeResponse<Employees> createEmployee(EmployeeRequest employeeRequest) {
        LOGGER.info(Constant.START);
        LOGGER.info("Create employee" + employeeRequest);
        String email = employeeRequest.getEmail();
        if(!isValidRegex(email)) {
            return new EmployeeResponse<>(400,"Email is invalid");
        }
        List<ProjectInfo> projects = employeeRequest.getProjects();
        Optional<Employees> employee = employeeRepository.findByEmail(email);
        if(employee.isPresent()) {
            return new EmployeeResponse<>(400,"Bad request. Email is already in use");
        }
        Employees employeeNew = employeeRequest.convertToEmployeeEntity(employeeRequest);
        employeeRepository.save(employeeNew);
        Long employeeId = employeeNew.getEmployeeId();
        if(projects.size() > 0) {
            for(ProjectInfo projectInfo : projects) {
                try{
                    Long projectId = projectInfo.getProjectId();
                    Teams newTeam = new Teams();
                    newTeam.setEmployeeId(employeeId);
                    newTeam.setProjectId(projectId);
                    teamRepository.save(newTeam);
                }catch (Exception e){
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Created employee");
    }

    private boolean isValidRegex(String value) {
        boolean result = false;
        Pattern regexGMT = Pattern.compile(Constant.REGEX_EMAIL);
        Matcher matchFoundGMT = regexGMT.matcher(value);
        // isDate
        if (matchFoundGMT.find()) {
            result = true;
        }
        return result;
    }

    @Transactional
    public EmployeeResponse<Employees> updateEmployee(EmployeeRequest employeeRequest, Long employeeId) {
        LOGGER.info(Constant.START);
        LOGGER.info("Update for employee with employeeId " + employeeId);
        Optional<Employees> employeeOptional = employeeRepository.findById(employeeId);
        if(!employeeOptional.isPresent()) {
            return new EmployeeResponse<>(404,"Employee with employeeId " + employeeId + " does not exist");
        }
        Employees employeeNew = employeeOptional.get().getUpdateEmployee(employeeRequest);
        employeeRepository.save(employeeNew);

        /*
        default keep all projects and only update projects when
        clear all projects and update projects for this employee on team entity
        */
        List<ProjectInfo> projects = employeeRequest.getProjects();
        if(projects != null && projects.size() > 0) {
            teamRepository.deleteByEmployeeId(employeeId);
            for(ProjectInfo projectInfo : projects) {
                Optional<Projects> oldProject = projectRepository.findById(projectInfo.getProjectId());
                if(oldProject.isPresent()) {
                    Teams teamNew = new Teams();
                    teamNew.setEmployeeId(employeeId);
                    teamNew.setProjectId(projectInfo.getProjectId());
                    teamRepository.save(teamNew);
                }else{
                    LOGGER.error("Not found projectId " + projectInfo.getProjectId() + "on team entity");
                }
            }
        }
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Updated employee");
    }

    @Transactional
    public EmployeeResponse<Employees> deleteEmployee(Long employeeId) throws ResourceNotFoundException{
        LOGGER.info(Constant.START);
        LOGGER.info("Delete employee " + employeeId);
        Optional<Employees> employee = employeeRepository.findById(employeeId);
        if(!employee.isPresent()) {
            return new EmployeeResponse<>(404, "Resource not found");
        }
        employeeRepository.deleteById(employeeId);
        teamRepository.deleteByEmployeeId(employeeId);
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Deleted employee");
    }

    public EmployeeResponse<Employees> importEmployees(MultipartFile file) {
        LOGGER.info(Constant.START);
        LOGGER.info("Import employees");
        try {
            List<Employees> employees = csvToEmployees(file.getInputStream());
            for (Employees employee : employees) {
                Optional<Employees> employeeOptional = employeeRepository.findByEmail(employee.getEmail());
                if (employeeOptional.isPresent()) {
                    continue;
                }
                employeeRepository.save(employee);
            }
            //employeeRepository.saveAll(employees);
        } catch (IOException e) {
            throw new RuntimeException("Fail to store csv data: " + e.getMessage());
        }
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Imported employees");
    }

    public boolean hasCSVFormat(MultipartFile file) {
        return Constant.CSV_TYPE.equals(file.getContentType());
    }

    private List<Employees> csvToEmployees(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<Employees> employees = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                try{
                    if(!isCSVRecordValid(csvRecord)){
                        continue;
                    }
                    employees.add(saveValueFromCSVRow(csvRecord));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return employees;
        } catch (IOException e) {
            throw new RuntimeException("Fail to parse CSV file: " + e.getMessage());
        }
    }

    private Employees saveValueFromCSVRow(CSVRecord csvRecord) {
        Employees employee = new Employees();
        employee.setDepartmentId(Long.parseLong(csvRecord.get(EEmployee.DEPARTMENT_ID.getValue()).trim()));
        employee.setFirstName(csvRecord.get(EEmployee.FIRST_NAME.getValue()).trim());
        employee.setLastName(csvRecord.get(EEmployee.LAST_NAME.getValue()).trim());
        employee.setDateOfBirth(Date.valueOf(csvRecord.get(EEmployee.DATE_OF_BIRTH.getValue()).trim()));
        employee.setAddress(csvRecord.get(EEmployee.ADDRESS.getValue()).trim());
        employee.setEmail(csvRecord.get(EEmployee.EMAIL).trim());
        employee.setPhoneNumber(csvRecord.get(EEmployee.PHONE_NUMBER.getValue()).trim());
        return employee;
    }

    private boolean isCSVRecordValid(CSVRecord csvRecord) throws IOException {
        boolean isValid = !csvRecord.get(EEmployee.DEPARTMENT_ID.getValue()).trim().equals("")
                && !csvRecord.get(EEmployee.FIRST_NAME.getValue()).trim().equals("")
                && !csvRecord.get(EEmployee.LAST_NAME.getValue()).trim().equals("")
                && !csvRecord.get(EEmployee.DATE_OF_BIRTH.getValue()).trim().equals("")
                && !csvRecord.get(EEmployee.ADDRESS.getValue()).trim().equals("")
                && !csvRecord.get(EEmployee.EMAIL.getValue()).trim().equals("")
                && isValidRegex(csvRecord.get(EEmployee.EMAIL.getValue()).trim())
                && !csvRecord.get(EEmployee.PHONE_NUMBER.getValue()).trim().equals("");
        return isValid;
    }

    public EmployeeResponse<?> exportEmployees(
            Long departmentId,
            Long projectId,
            String[] exportFields,
            Integer limit,
            Integer offset,
            String sort,
            List<String> sortBy) {

        LOGGER.info(Constant.START);
        LOGGER.info("Export Employees");
        try {
            String directionFile = exportFolder + LocalDate.now() + ".csv";
            PrintWriter csvWriter = new PrintWriter(directionFile);
            StringBuilder stringBuilder = new StringBuilder();
            List<EmployeeBean> employees = employeeRepository.getAllEmployeeBeen(
                    entityManager,
                    departmentId,
                    projectId,
                    limit,
                    offset,
                    sort,
                    sortBy
            );
            if(exportFields != null && exportFields.length > 0) {
                stringBuilder.append(createCSVHeaderOption(exportFields));
                stringBuilder.append("\n");
                stringBuilder.append(exportColumnValueOption(employees, exportFields));
            }else{
                stringBuilder.append(Constant.EMPLOYEE_HEADER_NAME);
                for (EmployeeBean employee : employees) {
                    stringBuilder.append(exportColumnsValue(employee));
                }
            }
            csvWriter.println(stringBuilder);
            csvWriter.close();
        }catch (Exception e) {
            return new EmployeeResponse<>(400, "Error when export Employees");
        }
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Export Employees successfully");
    }

    private boolean isEmployeeColumns(String field){
        Map<String, String> employeeColumns = new HashMap<>();
        employeeColumns.put("employeeId", EEmployee.EMPLOYEE_ID.getValue());
        employeeColumns.put("departmentId", EEmployee.DEPARTMENT_ID.getValue());
        employeeColumns.put("department", EEmployee.DEPARTMENT.getValue());
        employeeColumns.put("firstName", EEmployee.FIRST_NAME.getValue());
        employeeColumns.put("lastName", EEmployee.LAST_NAME.getValue());
        employeeColumns.put("dateOfBirth", EEmployee.DATE_OF_BIRTH.getValue());
        employeeColumns.put("address", EEmployee.ADDRESS.getValue());
        employeeColumns.put("email", EEmployee.EMAIL.getValue());
        employeeColumns.put("phoneNumber", EEmployee.PHONE_NUMBER.getValue());

        return field.equalsIgnoreCase(employeeColumns.get(field));
    }

    private StringBuilder createCSVHeaderOption(String[] exportFields){
        StringBuilder csvHeaderOption = new StringBuilder();
        for (String exportField : exportFields) {
            if (isEmployeeColumns(exportField)) {
                csvHeaderOption.append(exportField);
                csvHeaderOption.append(",");
            }
        }
        csvHeaderOption.deleteCharAt(csvHeaderOption.length() - 1);
        return csvHeaderOption;
    }

    private StringBuilder exportColumnValueOption(List<EmployeeBean> employees, String[] exportFields) {
        StringBuilder csvRowOption = new StringBuilder();
        for (EmployeeBean employee : employees) {
            for (String exportField : exportFields) {
                if (isEmployeeColumns(exportField)) {
                    csvRowOption.append(exportOptionColumnValue(employee, exportField));
                    csvRowOption.append(",");
                }
            }
            csvRowOption.deleteCharAt(csvRowOption.length()-1);
            csvRowOption.append("\n");
        }
        return csvRowOption;
    }

    private StringBuilder exportOptionColumnValue(EmployeeBean employeeBean, String field){
        StringBuilder columnValueOption = new StringBuilder();
        if(field.equalsIgnoreCase(EEmployee.EMPLOYEE_ID.getValue())){
            columnValueOption.append(employeeBean.getEmployeeId());
        }
        if(field.equalsIgnoreCase(EEmployee.DEPARTMENT.getValue())){
            columnValueOption.append(employeeBean.getDepartment());
        }
        if(field.equalsIgnoreCase(EEmployee.FIRST_NAME.getValue())){
            columnValueOption.append(employeeBean.getFirstName());
        }
        if(field.equalsIgnoreCase(EEmployee.LAST_NAME.getValue())){
            columnValueOption.append(employeeBean.getLastName());
        }
        if(field.equalsIgnoreCase(EEmployee.DATE_OF_BIRTH.getValue())){
            columnValueOption.append(employeeBean.getDateOfBirth());
        }
        if(field.equalsIgnoreCase(EEmployee.ADDRESS.getValue())){
            columnValueOption.append(employeeBean.getAddress());
        }
        if(field.equalsIgnoreCase(EEmployee.EMAIL.getValue())){
            columnValueOption.append(employeeBean.getEmail());
        }
        if(field.equalsIgnoreCase(EEmployee.PHONE_NUMBER.getValue())){
            columnValueOption.append(employeeBean.getPhoneNumber());
        }
        return columnValueOption;
    }

    private StringBuilder exportColumnsValue(EmployeeBean employee){

        StringBuilder columnsValue = new StringBuilder();

        columnsValue.append("\n").append(employee.getEmployeeId());
        columnsValue.append(",").append(employee.getDepartment());
        columnsValue.append(",").append(employee.getFirstName());
        columnsValue.append(",").append(employee.getLastName());
        columnsValue.append(",").append(employee.getDateOfBirth());
        columnsValue.append(",").append(employee.getAddress());
        columnsValue.append(",").append(employee.getEmail());
        columnsValue.append(",").append(employee.getPhoneNumber());

        return columnsValue;
    }


}
