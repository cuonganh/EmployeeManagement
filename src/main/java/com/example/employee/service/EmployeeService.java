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
import com.example.employee.model.exception.ValidationException;
import com.example.employee.model.payload.EmployeeRequest;
import com.example.employee.model.payload.EmployeeResponse;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.repository.ProjectRepository;
import com.example.employee.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
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

    private final String REGEX_EMAIL =
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    private final String REGEX_DATEOFBIRTH = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$";

    private final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeResponse<List<EmployeeBean>> getEmployeeBean(EntityManager entityManager, Long employeeId) throws ResourceNotFoundException {

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
        if(!checkRegex(REGEX_EMAIL, email)) {
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
            throw new ResourceNotFoundException();
        }
        employeeRepository.deleteById(employeeId);
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Deleted employee");
    }


}
