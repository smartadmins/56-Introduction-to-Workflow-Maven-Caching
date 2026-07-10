# 56-Introduction-to-Workflow-Caching

# GitHub Actions Workflow Caching Project (Java + Maven)

This project demonstrates how to use **GitHub Actions Cache** to speed up Maven builds by caching dependencies from the local Maven repository (`~/.m2/repository`).

---

# Project Goal

Without Cache:

```text
Developer Push Code
        ↓
GitHub Actions Starts
        ↓
Download Maven Dependencies
        ↓
Compile Code
        ↓
Run Tests
        ↓
Build JAR
```

Every workflow run downloads dependencies again.

---

With Cache:

```text
First Run
---------
Download Dependencies
Store in Cache

Second Run
----------
Restore Cache
Skip Dependency Download
Build Faster
```

Benefits:

* Faster CI/CD Pipeline
* Less Internet Usage
* Reduced Build Time
* Better Developer Experience

---

# Project Architecture

```text
GitHub Repository
       │
       ▼
GitHub Actions Workflow
       │
       ├── Checkout Code
       │
       ├── Setup Java
       │
       ├── Restore Maven Cache
       │
       ├── Maven Build
       │
       ├── Run Tests
       │
       ├── Package Jar
       │
       └── Save Updated Cache
```

---

# Step 1: Create Java Maven Project

Folder Structure

```text
java-maven-cache-demo/
│
├── .github/
│   └── workflows/
│       └── maven-cache.yml
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── cloudtechnet/
│   │               └── App.java
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── cloudtechnet/
│                   └── AppTest.java
│
├── pom.xml
│
└── README.md
```

---

# Step 2: Create Java Application

## App.java

```java
package com.cloudtechnet;

public class App {

    public static void main(String[] args) {
        System.out.println("Welcome to GitHub Actions Cache Demo");
    }

    public static int add(int a, int b) {
        return a + b;
    }
}
```

---

# Step 3: Create Unit Test

## AppTest.java

```java
package com.cloudtechnet;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {

    @Test
    void testAdd() {
        assertEquals(10, App.add(5,5));
    }
}
```

---

# Step 4: Create Maven pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.cloudtechnet</groupId>
    <artifactId>java-maven-cache-demo</artifactId>
    <version>1.0</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.1</version>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.3</version>
            </plugin>

        </plugins>
    </build>

</project>
```

---

# Step 5: Create GitHub Actions Workflow

## maven-cache.yml

```yaml
name: Java Maven Cache Demo

on:
  push:
    branches:
      - main

jobs:

  build:

    runs-on: ubuntu-latest

    steps:

      - name: Checkout Source Code
        uses: actions/checkout@v4

      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - name: Cache Maven Dependencies
        uses: actions/cache@v4
        with:
          path: ~/.m2/repository

          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}

          restore-keys: |
            ${{ runner.os }}-maven-

      - name: Build Application
        run: mvn clean package

      - name: Upload Artifact
        uses: actions/upload-artifact@v4
        with:
          name: java-application
          path: target/*.jar
```

---

# Step 6: Push Code

```bash
git init

git add .

git commit -m "Initial Commit"

git branch -M main

git remote add origin <repo-url>

git push -u origin main
```

---

# First Workflow Execution

GitHub Actions Output:

```text
Checkout Code

Setup Java

Cache Maven Dependencies
Cache not found

Downloading Dependencies

Running Tests

Building Jar

Saving Cache
```

Build Time:

```text
2 Minutes
```

---

# Second Workflow Execution

Make Small Change

```java
System.out.println("Cache Demo");
```

Commit Again

```bash
git add .
git commit -m "Second Commit"
git push
```

---

GitHub Actions Output

```text
Checkout Code

Setup Java

Restore Cache

Cache Hit

Compile

Tests

Build Jar
```

Notice:

```text
Dependencies NOT downloaded again
```

Build Time:

```text
30 Seconds
```

---

# How Cache Key Works

```yaml
key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
```

Example:

```text
ubuntu-maven-a12bc34d56
```

Generated from:

```text
Operating System
+
pom.xml Hash
```

---

# Scenario 1: No pom.xml Change

```text
Cache Hit
```

Workflow uses existing cache.

---

# Scenario 2: New Dependency Added

Example:

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.14.0</version>
</dependency>
```

Result:

```text
pom.xml hash changed
```

New Cache Key:

```text
ubuntu-maven-xyz789
```

GitHub creates a new cache.

---

# Verify Cache

Actions Log:

```text
Cache restored successfully
```

or

```text
Cache hit occurred
```

---

# Enterprise-Level Optimization

Instead of `actions/cache`, use built-in Maven caching.

```yaml
- name: Setup Java
  uses: actions/setup-java@v4
  with:
    distribution: temurin
    java-version: 17
    cache: maven
```

This automatically caches:

```text
~/.m2/repository
```

Workflow becomes shorter and easier to maintain.

---

# Interview Explanation

**What is Workflow Caching in GitHub Actions?**

Workflow caching is a mechanism used to store frequently used files such as Maven dependencies, npm packages, Gradle dependencies, or build artifacts between workflow runs. Instead of downloading dependencies every time, GitHub Actions restores them from cache, which significantly reduces build time and improves CI/CD performance.

**In our Java Maven project**, we cached the local Maven repository (`~/.m2/repository`) using `actions/cache`. During the first run, dependencies were downloaded and saved to cache. In subsequent runs, GitHub restored the cache based on the cache key generated from the `pom.xml` hash, resulting in much faster builds.