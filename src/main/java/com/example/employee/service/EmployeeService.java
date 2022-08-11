package com.example.employee.service;

import com.example.employee.common.Constant;
import com.example.employee.common.converter.EmployeeDtoConverter;
import com.example.employee.model.dto.EmployeeBean;
import com.example.employee.model.dto.EmployeeDto;
import com.example.employee.model.dto.PageDto;
import com.example.employee.model.dto.ProjectInfo;
import com.example.employee.model.entity.Employees;
import com.example.employee.model.entity.Teams;
import com.example.employee.model.exception.ResourceNotFoundException;
import com.example.employee.model.payload.EmployeeRequest;
import com.example.employee.model.payload.EmployeeResponse;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.repository.ProjectRepository;
import com.example.employee.repository.TeamRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    EmployeeDtoConverter employeeDtoConverter;


    private final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeResponse<List<EmployeeBean>> getEmployeeBean(
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
        Sort.Direction direction = Sort.Direction.DESC;

        if(limit == null){
            limit = 10;
        }
        if(offset == null){
            offset = 0;
        }
        if(sort != null){
            if(sort.equalsIgnoreCase("asc")){
                direction = Sort.Direction.ASC;
            }
        }
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
        if(!checkRegex(Constant.REGEX_EMAIL, email)) {
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

    private boolean checkRegex(String regex, String value) {
        boolean result = false;
        Pattern regexGMT = Pattern.compile(regex);
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
        LOGGER.info("Update employee " + employeeId);
        Optional<Employees> employeeOptional = employeeRepository.findById(employeeId);
        if(!employeeOptional.isPresent()) {
            return new EmployeeResponse<>(404,"Employee with id " + employeeId + " does not exist");
        }
        Employees employeeNew = employeeOptional.get().getUpdateEmployee(employeeRequest, employeeId);
        employeeRepository.save(employeeNew);

        List<ProjectInfo> projects = employeeRequest.getProjects();
        if(projects.size() > 0) {
            for(ProjectInfo projectInfo : projects) {
                Long projectId = projectInfo.getProjectId();
                Optional<Teams> teamOld = teamRepository.findByEmployeeIdAndProjectId(employeeId, projectId);
                if(!teamOld.isPresent()) {
                    Teams teamNew = new Teams();
                    teamNew.setEmployeeId(employeeId);
                    teamNew.setProjectId(projectId);
                    teamRepository.save(teamNew);
                }
            }
        }
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Updated employee");
    }

    public EmployeeResponse<Employees> deleteEmployee(Long employeeId) throws ResourceNotFoundException{
        LOGGER.info(Constant.START);
        LOGGER.info("Delete employee " + employeeId);
        Optional<Employees> employee = employeeRepository.findById(employeeId);
        if(!employee.isPresent()) {
            return new EmployeeResponse<>(404, "Resource not found");
        }
        employeeRepository.deleteById(employeeId);
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Deleted employee");
    }


    public EmployeeResponse<Employees> importEmployees(MultipartFile file) {

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
        if (!Constant.CSV_TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    private List<Employees> csvToEmployees(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<Employees> employees = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                try{
                    Employees employee = new Employees();
                    //Skip with record missing value
                    if(csvRecord.get("departmentId").trim().equals("")
                            || csvRecord.get("firstName").trim().equals("")
                            || csvRecord.get("lastName").trim().equals("")
                            || csvRecord.get("dateOfBirth").trim().equals("")
                            || csvRecord.get("address").trim().equals("")
                            || csvRecord.get("email").trim().equals("")
                            || !checkRegex(Constant.REGEX_EMAIL, csvRecord.get("email").trim())
                            || csvRecord.get("phoneNumber").trim().equals("")){
                        continue;
                    }
                    employee.setDepartmentId(Long.parseLong(csvRecord.get("departmentId").trim()));
                    employee.setFirstName(csvRecord.get("firstName").trim());
                    employee.setLastName(csvRecord.get("lastName").trim());
                    employee.setDateOfBirth(Date.valueOf(csvRecord.get("dateOfBirth").trim()));
                    employee.setAddress(csvRecord.get("address").trim());
                    employee.setEmail(csvRecord.get("email").trim());
                    employee.setPhoneNumber(csvRecord.get("phoneNumber").trim());
                    employees.add(employee);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return employees;
        } catch (IOException e) {
            throw new RuntimeException("Fail to parse CSV file: " + e.getMessage());
        }
    }


    private List<Employees> getAllEmployees() {
        return employeeRepository.findAll();
    }


    public EmployeeResponse<?> exportEmployees(Long departmentId, Long projectId, Integer limit, Integer offset, String sort, List<String> sortBy) {

        List<EmployeeBean> employees = employeeRepository.getAllEmployeeBeen(
                entityManager,
                departmentId,
                projectId,
                limit,
                offset,
                sort,
                sortBy
        );
        ByteArrayInputStream byteArrayInputStream = employeesToCSV(employees);

        InputStreamResource inputStreamResource = new InputStreamResource(byteArrayInputStream);

        return new EmployeeResponse<>(200, "Export Employees successfully");
    }

    private ByteArrayInputStream employeesToCSV(List<EmployeeBean> employees) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), Constant.CSV_FORMAT)
        ) {
            for (EmployeeBean employee : employees) {
                List<String> data = Arrays.asList(
                        employee.getEmployeeId(),
                        employee.getDepartment(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getDateOfBirth().toString(),
                        employee.getAddress(),
                        employee.getEmail(),
                        employee.getPhoneNumber()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Fail to import data to CSV file: " + e.getMessage());
        }

    }


}
