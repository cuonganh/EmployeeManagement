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
import com.example.employee.model.exception.ValidationException;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
            String employeeId
    ) throws ResourceNotFoundException, ValidationException {

        LOGGER.info(Constant.START);
        LOGGER.info("Get employee by id: " + employeeId);

        try{
            Long.valueOf(employeeId);
        }catch (NumberFormatException numberFormatException) {
            throw new ValidationException(Collections.singletonList("EmployeeId is in valid"));
        }

        List<EmployeeBean> employeeBeen = employeeRepository.getEmployee(entityManager, Long.valueOf(employeeId));
        if(employeeBeen.get(0).getEmployeeId() == null) {
            throw new ResourceNotFoundException();
        }

        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Found Employee", employeeBeen);

    }

    public PageDto<EmployeeDto> getEmployees(
            String departmentId,
            String projectId,
            String limit,
            String offset,
            String sort,
            List<String> sortBy
    ) throws ValidationException {
        LOGGER.info(Constant.START);
        LOGGER.info("Get employees list");

        if(isValidNumberGetEmployeesRequest(departmentId, projectId, limit, offset)){
            throw new ValidationException(Collections.singletonList("Bad request - number format"));
        }

        if(CollectionUtils.isEmpty(sortBy)){
            sortBy = new ArrayList<>();
            sortBy.add("employeeId");
        }else if(sortBy.size() == 0){
            sortBy.add("employeeId");
        }else if(sortBy.size() == 1 && sortBy.get(0).trim().equals("")){
            sortBy.add("employeeId");
        }else{
            if(!isValidSortByRequest(sortBy)){
                throw new ValidationException(Collections.singletonList("Bad request - sort fields is valid"));
            }
        }

        if(limit == null) limit = "10";
        if(offset == null) offset = "0";

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
        return new PageDto<>(
                200,
                "Found employees",
                Integer.parseInt(limit),
                Integer.parseInt(offset),
                total,
                employees
        );

    }

    private boolean isValidNumberGetEmployeesRequest(
            String departmentId,
            String projectId,
            String limit,
            String offset
    ) {
        try {
            if(departmentId != null) Long.valueOf(departmentId);
            if(projectId != null) Long.valueOf(projectId);
            if(limit != null) Long.valueOf(limit);
            if(offset!= null) Long.valueOf(offset);
        }catch(NumberFormatException nfe) {
            return true;
        }

        return false;
    }

    private boolean isValidSortByRequest(List<String> sortList){
        boolean isValid = true;
        for(String sortBy : sortList) {
            if(!isEmployeeColumns(sortBy) || sortBy.equalsIgnoreCase(EEmployee.DEPARTMENT_ID.getValue())){
                isValid = false;
            }
        }
        return isValid;
    }

    @Transactional
    public EmployeeResponse<Employees> createEmployee(EmployeeRequest employeeRequest) throws ValidationException {
        LOGGER.info(Constant.START);
        LOGGER.info("Create employee" + employeeRequest);

        if(!employeeRequest.isValidateCreateEmployeeRequest(employeeRequest)){
            throw new ValidationException(Collections.singletonList("Bad request"));
        }

        Optional<Employees> employee = employeeRepository.findByEmail(employeeRequest.getEmail());
        if(employee.isPresent()) {
            throw new ValidationException(Collections.singletonList("Email was used"));
        }
        Employees employeeNew = employeeRequest.convertToEmployeeEntity(employeeRequest);
        employeeRepository.save(employeeNew);

        Long employeeId = employeeNew.getEmployeeId();
        List<ProjectInfo> projects = employeeRequest.getProjects();
        addProjectsForEmployee(projects, employeeId);

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

    private void addProjectsForEmployee(List<ProjectInfo> projects, long employeeId){
        if(projects != null && projects.size() > 0) {
            for(ProjectInfo projectInfo : projects) {
                Long projectId = Long.valueOf(projectInfo.getProjectId());
                Teams newTeam = new Teams();
                newTeam.setEmployeeId(employeeId);
                newTeam.setProjectId(projectId);
                teamRepository.save(newTeam);
            }
        }
    }

    @Transactional
    public EmployeeResponse<Employees> updateEmployee(EmployeeRequest employeeRequest, String employeeId) throws ResourceNotFoundException, ValidationException {
        LOGGER.info(Constant.START);
        LOGGER.info("Update for employee with employeeId " + employeeId);
        try{
            Long.valueOf(employeeId);
        }catch (NumberFormatException numberFormatException) {
            throw new ValidationException(Collections.singletonList("EmployeeId is in valid"));
        }
        if(!employeeRequest.isValidateUpdateEmployee(employeeRequest)){
            throw new ValidationException(Collections.singletonList("Bad request"));
        }
        Optional<Employees> employeeOptional = employeeRepository.findById(Long.valueOf(employeeId));
        if(!employeeOptional.isPresent()) {
            throw new ResourceNotFoundException("EmployeeId " + employeeId + " does not exist");
        }
        if(!canUpdateThisEmail(employeeRequest, Long.valueOf(employeeId))){
            throw new ValidationException(Collections.singletonList("Email was used"));
        }

        Employees employeeNew = employeeOptional.get().getUpdateEmployee(employeeRequest);
        employeeRepository.save(employeeNew);

        updateProjects(employeeRequest, Long.parseLong(employeeId));

        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Updated employee");
    }

    private boolean canUpdateThisEmail(EmployeeRequest employeeRequest, Long employeeId) {
        boolean updated = true;
        Optional<Employees> employeeOptionalEmail = employeeRepository.findByEmail(employeeRequest.getEmail());
        if(employeeOptionalEmail.isPresent()) {
            long employeeIdNew = employeeOptionalEmail.get().getEmployeeId();
            if(employeeId != employeeIdNew) {
                updated = false;
            }
        }
        return updated;
    }

    private void updateProjects(EmployeeRequest employeeRequest, long employeeId){
        /*
        default keep all projects and only update projects when
        clear all projects and update projects for this employee on team entity
        */
        List<ProjectInfo> projects = employeeRequest.getProjects();
        if(projects != null && projects.size() > 0) {
            teamRepository.deleteByEmployeeId(employeeId);
            for(ProjectInfo projectInfo : projects) {
                Optional<Projects> oldProject = projectRepository.findById(Long.valueOf(projectInfo.getProjectId()));
                if(oldProject.isPresent()) {
                    Teams teamNew = new Teams();
                    teamNew.setEmployeeId(employeeId);
                    teamNew.setProjectId(Long.valueOf(projectInfo.getProjectId()));
                    teamRepository.save(teamNew);
                }else{
                    LOGGER.error("ProjectId " + projectInfo.getProjectId() + " does not exist");
                }
            }
        }
    }

    @Transactional
    public EmployeeResponse<Employees> deleteEmployee(String employeeId) throws ResourceNotFoundException, ValidationException {
        LOGGER.info(Constant.START);
        LOGGER.info("Delete employee " + employeeId);
        try{
            Long.valueOf(employeeId);
        }catch (NumberFormatException numberFormatException) {
            throw new ValidationException(Collections.singletonList("EmployeeId is in valid"));
        }
        Optional<Employees> employee = employeeRepository.findById(Long.valueOf(employeeId));
        if(!employee.isPresent()) {
            throw new ResourceNotFoundException("Resource not found");
        }
        employeeRepository.deleteById(Long.valueOf(employeeId));
        teamRepository.deleteByEmployeeId(Long.valueOf(employeeId));
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
        employee.setDateOfBirth(LocalDate.parse((csvRecord.get(EEmployee.DATE_OF_BIRTH.getValue()).trim())));
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
            String departmentId,
            String projectId,
            String[] exportFields,
            String limit,
            String offset,
            String sort,
            List<String> sortBy) throws FileNotFoundException, ValidationException {

        LOGGER.info(Constant.START);
        LOGGER.info("Export Employees");
        if(isValidNumberGetEmployeesRequest(departmentId, projectId, limit, offset)){
            throw new ValidationException(Collections.singletonList("Bad request - number format"));
        }
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
        }catch (IOException e) {
            throw new FileNotFoundException("Error when export Employees");
        }
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Export Employees successfully");
    }

    private boolean isEmployeeColumns(String field){
        return EEmployee.getByValue(field) != null;
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
