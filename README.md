We are investigating a production SQL Server performance incident involving high CPU/resource usage from a reporting query.

I have identified the SQL query involved. It works with data related to:

- attestation_status
- measure_years
- hedis_details / agg_hedis_details

The query also produces values such as:

- toBeReviewedMedicaid
- toBeReviewedMedicare
- toBeReviewedMedicareFlorida
- toBeReviewedMedicaidFlorida

Before analyzing performance, I want to understand where this query comes from in the application.

Please search this repository and identify:

1. The exact file(s) where this query is defined or generated.
2. The class and method responsible for executing it.
3. Whether it is native SQL, JPA/Hibernate-generated SQL, a stored procedure, or constructed another way.
4. The repository/service/API flow that eventually invokes this query.
5. The database tables involved according to the code.

Do not modify any code.
Do not suggest indexes or query optimizations yet.

For now, only trace the query from the application code to the database and explain what you find, with file paths and method names.
