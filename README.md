# Java Client Library
Before the development of the superclient, the three required microservices were written in Java. In order to encourage code re-use, a shared client library was created that would handle the interactions between the services and the event bus, it contained the following:

  - Provides sane API for Java-based services to communicate with the event bus, including registration, consistency, receipt handling, event production and processing.
  - Fully unit tested with 100% coverage.

**Deprecated:** This repository is no longer in use, please review the `project-and-dissertation` project for currently in-use repositories.

## How to test
This repository is configured using Maven, ensure Maven is installed and then run `mvn test`.
