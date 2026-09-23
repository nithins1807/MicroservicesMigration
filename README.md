I am working on the existing branch:

AB#9506162/feature-branch-de-ui-spring-boot-4-upgrade

This branch contains the Spring Boot 4 / Spring Security upgrade work. The current PR is failing the Sonar quality gate because coverage on new code is 57.1%, while the requirement is >= 80%.

Sonar currently shows these two files at 0% coverage on new code:

1. src/main/java/com/humana/dataentitlement/security/AzureADWebSecurityConfigurerAdapter.java
2. src/main/java/com/humana/dataentitlement/security/DevWebSecurityConfigurerAdapter.java

There is already an existing test:

src/test/java/com/humana/dataentitlement/security/AzureADWebSecurityConfigurerAdapterTest.java

That test currently covers basic class instantiation and CsrfCookieFilter processing, but it does not appear to cover the new Spring Security configuration logic.

For AzureADWebSecurityConfigurerAdapter, Sonar shows approximately:
- 4 new lines to cover
- 4 uncovered lines
- 4 conditions to cover
- 0% coverage on new code

One area that appears responsible is this request matcher in the filterChain configuration:

request -> request.getServletPath().startsWith("/api/")
        || request.getServletPath().startsWith("/actuator/")

Please do the following:

1. Inspect the current production classes and existing tests first. Do not assume the code exactly matches this description.
2. Identify exactly which new lines/branches in AzureADWebSecurityConfigurerAdapter and DevWebSecurityConfigurerAdapter are currently not being exercised.
3. Make the smallest reasonable changes needed to get Sonar new-code coverage above 80%.
4. Prefer adding meaningful unit tests over changing production code solely for coverage.
5. If the inline RequestMatcher makes the Azure logic difficult to unit test, it is acceptable to extract it into a package-private static final RequestMatcher inside AzureADWebSecurityConfigurerAdapter, as long as behavior remains exactly the same.
6. If extracted, test at least these cases:
   - /api/entities -> matches
   - /actuator/health -> matches
   - /home -> does not match
   This should exercise both sides of the OR condition.
7. Review DevWebSecurityConfigurerAdapter and add only the minimum tests needed for its uncovered new lines.
8. Reuse the project's existing JUnit 5, Mockito, AssertJ, and Spring Security testing patterns. Do not introduce new dependencies.
9. Do not rewrite or broadly refactor the Spring Boot 4 upgrade.
10. Do not change application behavior, security rules, URLs, authorization behavior, headers, CSRF behavior, or Azure authentication configuration.
11. Do not modify unrelated files.
12. Preserve the existing code style.

After making the changes:

- Run the targeted security tests first.
- Then run the complete test suite with the Gradle wrapper.
- Report:
  a. exactly which files you changed
  b. what tests you added
  c. why each test improves Sonar coverage
  d. test results
  e. any remaining likely Sonar coverage or code-quality issues

Do not commit or push anything. I want to review the diff first.

Before editing, show me your proposed minimal change plan based on the actual code you find.
