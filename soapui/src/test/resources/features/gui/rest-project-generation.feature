Feature: REST project generation

  Scenario Outline: : Generate a project structure from a URI
    Given Calvin is creating a new REST project
      And inputs a <valid REST request URI>
    When he clicks the OK button
    Then a project named REST Project is created in the navigation tree

    Examples:
    | valid REST request URI |
    | http://service.com/api/1.2/json/search/search?title=Kill%20me |