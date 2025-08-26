package com.jobapp.companyms.company;

import java.util.List;

public interface CompanyService {
    List<Company> getALlCompanies();
    boolean updateCompany(Company company, Long id);
    void createCompany(Company company);
    boolean deleteCompanyById(Long id);
    Company getCompanyById(Long id);
}
