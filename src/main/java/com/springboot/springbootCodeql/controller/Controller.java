package com.springboot.springbootCodeql.controller;

import com.springboot.springbootCodeql.model.Employee;
import com.springboot.springbootCodeql.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class Controller {

    private final EmployeeService employeeService;

    public Controller(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping 
    public List<Employee> list() {
        return employeeService.list();
    }

    @GetMapping("/{id}")
    public Employee get(@PathVariable Long id) {
        return employeeService.get(id);
    }

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        return employeeService.create(employee);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @RequestBody Employee employee) {
        return employeeService.update(id, employee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // VULNERABLE 1: SQL Injection - user input goes straight into raw SQL
    @GetMapping("/search")
    public List<Employee> search(@RequestParam String name) {
        return employeeService.searchByName(name);
    }

    // VULNERABLE 2: Command Injection - user input goes straight into OS command
    @GetMapping("/ping")
    public String ping(@RequestParam String host) throws IOException {
        return employeeService.runSystemCommand(host);
    }

    // VULNERABLE 3: Path Traversal - user input used directly in file path
    @GetMapping("/file")
    public String readFile(@RequestParam String filename) throws IOException {
        return employeeService.readFile(filename);
    }

    // VULNERABLE 4: SSRF - user controls the URL the server requests
    @GetMapping("/fetch")
    public String fetch(@RequestParam String url) {
        return employeeService.fetchUrl(url);
    }
}