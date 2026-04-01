package com.springboot.springbootCodeql.service;

import com.springboot.springbootCodeql.model.Employee;
import com.springboot.springbootCodeql.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;

    @Autowired
    private EntityManager entityManager;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee create(Employee employee) {
        return repository.save(employee);
    }

    public Employee get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Employee not found: " + id));
    }

    public List<Employee> list() {
        return repository.findAll();
    }

    public Employee update(Long id, Employee updated) {
        Employee existing = get(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // VULNERABLE 1: SQL Injection
    @SuppressWarnings("unchecked")
    public List<Employee> searchByName(String name) {
        String query = "SELECT * FROM employees WHERE first_name = '" + name + "'";
        return entityManager.createNativeQuery(query, Employee.class).getResultList();
    }

    // VULNERABLE 2: Command Injection
    public String runSystemCommand(String userInput) throws IOException {
        Process process = Runtime.getRuntime().exec("ping -c 1 " + userInput);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }

    // VULNERABLE 3: Path Traversal
    public String readFile(String filename) throws IOException {
        Path path = Paths.get("/uploads/" + filename);
        return new String(Files.readAllBytes(path));
    }

    // VULNERABLE 4: Server-Side Request Forgery (SSRF)
    public String fetchUrl(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }
    
}