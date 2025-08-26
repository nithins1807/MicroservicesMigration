package com.jobapp.companyms.company;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    private CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies(){
        return new ResponseEntity<>(companyService.getALlCompanies(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCompany(@PathVariable Long id, @RequestBody Company company){
        boolean updated = companyService.updateCompany(company, id);
        if(updated) {
            return new ResponseEntity<>("Company Updated Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Company not found", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createCompany(@RequestBody Company company){
        companyService.createCompany(company);
        return new ResponseEntity<>("Company added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable Long id){
        boolean deleted = companyService.deleteCompanyById(id);
        if(deleted){
            return new ResponseEntity<>("Company deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Company not found", HttpStatus.NOT_FOUND);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id){
        Company company = companyService.getCompanyById(id);
        if(company!=null){
            return new ResponseEntity<>(company, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
