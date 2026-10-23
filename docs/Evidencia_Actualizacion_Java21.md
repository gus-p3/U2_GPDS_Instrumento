# Evidencia de Actualización y Upgrade a Java 21 en Microservicios y Jenkins

**Asignatura:** Gestión del Proceso de Desarrollo de Software (GIDS6102 - Unidad 2)  
**Estudiante:** Gustavo Mendoza  
**Fecha:** 21 de Octubre de 2026  

---

## 1. Ligas de Repositorios en GitHub (Listos para Utilizarse)

* **Repositorio BackEnd (`Ejercicio5MyAPIGestor`):**  
  [https://github.com/gus-p3/GIDS6012_U2_DPDS_BackEnd.git](https://github.com/gus-p3/GIDS6012_U2_DPDS_BackEnd.git)
* **Repositorio FrontEnd (`Ejercicio5Cliente`):**  
  [https://github.com/gus-p3/GIDS6012_U2_DPDS_FrontEnd.git](https://github.com/gus-p3/GIDS6012_U2_DPDS_FrontEnd.git)
* **Repositorio Integral del Instrumento:**  
  [https://github.com/gus-p3/U2_GPDS_Instrumento.git](https://github.com/gus-p3/U2_GPDS_Instrumento.git)

---

## 2. Configuración del Servidor Jenkins (JDK 21 y Maven 3.9.x)

Para dar soporte a la compilación y pruebas de proyectos basados en Java 21, se aplicó la configuración correspondiente en el entorno de Integración Continua (Jenkins):

1. **Entorno / Runtime de Jenkins:**
   * Contenedor e imagen base: `jenkins/jenkins:jdk21`
   * Versión de Java en el host de ejecución: `OpenJDK Runtime Environment Temurin-21.0.12.1+1 LTS`
2. **Configuración de JDK en Jenkins (`Manage Jenkins -> Tools -> JDK installations`):**
   * **Nombre (Name):** `JDK21`
   * **JAVA_HOME:** `/opt/java/openjdk`
3. **Configuración de Maven en Jenkins (`Manage Jenkins -> Tools -> Maven installations`):**
   * **Nombre (Name):** `M3`
   * **Versión seleccionada:** `3.9.0` (o `3.9.6+` con opción "Instalar automáticamente desde Apache").
   * *Justificación:* Maven 3.9.x proporciona soporte completo para el estándar de Java 21, compatibilidad con plugins actualizados y estabilidad frente al compilador `javac` con release 21.

### Declaración en Jenkinsfile / Declarative Pipeline:
```groovy
pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'M3'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', 
                    credentialsId: 'github-credentials', 
                    url: 'https://github.com/gus-p3/GIDS6012_U2_DPDS_BackEnd.git'
            }
        }
        stage('Test & Coverage') {
            steps {
                sh 'mvn clean test jacoco:report'
            }
        }
    }
}
```

---

## 3. Evidencia de Archivos `pom.xml` (Estado Normal vs. Estado Actualizado)

### 3.1. Microservicio 1: BackEnd (`Ejercicio5MyAPIGestor`)

#### A) Archivo `pom.xml` en su estado normal (Original - Java 11 / Spring Boot 2.7.17)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.7.17</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.mx.cbtis</groupId>
	<artifactId>Ejercicio5MyAPIGestor</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Ejercicio5MyAPIGestor</name>
	<description>Proyecto My API Rest</description>
	<properties>
		<java.version>11</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.mariadb.jdbc</groupId>
			<artifactId>mariadb-java-client</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

#### B) Archivo `pom.xml` con su actualización (Actualizado - Java 21 / Spring Boot 3.3.4 + Plugins de Calidad y Pruebas)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.3.4</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.mx.cbtis</groupId>
	<artifactId>Ejercicio5MyAPIGestor</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Ejercicio5MyAPIGestor</name>
	<description>Proyecto My API Rest</description>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.mariadb.jdbc</groupId>
			<artifactId>mariadb-java-client</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>

			<!-- SonarQube Scanner Plugin -->
			<plugin>
				<groupId>org.sonarsource.scanner.maven</groupId>
				<artifactId>sonar-maven-plugin</artifactId>
				<version>5.5.0.6356</version>
			</plugin>

			<!-- JaCoCo Plugin para reporte de cobertura de código -->
			<plugin>
				<groupId>org.jacoco</groupId>
				<artifactId>jacoco-maven-plugin</artifactId>
				<version>0.8.12</version>
				<executions>
					<execution>
						<goals>
							<goal>prepare-agent</goal>
						</goals>
					</execution>
					<execution>
						<id>report</id>
						<phase>test</phase>
						<goals>
							<goal>report</goal>
						</goals>
					</execution>
				</executions>
			</plugin>

			<!-- Maven Surefire Plugin para ejecución de pruebas unitarias -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>3.2.5</version>
			</plugin>
		</plugins>
	</build>

</project>
```

---

### 3.2. Microservicio 2: FrontEnd (`Ejercicio5Cliente`)

#### A) Archivo `pom.xml` en su estado normal (Original - Java 11 / Spring Boot 2.7.17)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.7.17</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.mx.cbtis</groupId>
	<artifactId>Ejercicio5Cliente</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Ejercicio5Cliente</name>
	<description>Interfaz del Gestor de Proyectos</description>
	<properties>
		<java.version>11</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		
		<dependency>
		    <groupId>org.webjars</groupId>
		    <artifactId>bootstrap</artifactId>
		    <version>5.1.3</version> 
		</dependency>

	</dependencies>
	
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
	
</project>
```

#### B) Archivo `pom.xml` con su actualización (Actualizado - Java 21 / Spring Boot 3.3.4 + Plugins de Calidad y Pruebas)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.3.4</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.mx.cbtis</groupId>
	<artifactId>Ejercicio5Cliente</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Ejercicio5Cliente</name>
	<description>Interfaz del Gestor de Proyectos</description>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		
		<dependency>
		    <groupId>org.webjars</groupId>
		    <artifactId>bootstrap</artifactId>
		    <version>5.3.3</version> 
		</dependency>

	</dependencies>
	
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>

			<!-- SonarQube Scanner Plugin -->
			<plugin>
				<groupId>org.sonarsource.scanner.maven</groupId>
				<artifactId>sonar-maven-plugin</artifactId>
				<version>5.5.0.6356</version>
			</plugin>

			<!-- JaCoCo Plugin para reporte de cobertura de código -->
			<plugin>
				<groupId>org.jacoco</groupId>
				<artifactId>jacoco-maven-plugin</artifactId>
				<version>0.8.12</version>
				<executions>
					<execution>
						<goals>
							<goal>prepare-agent</goal>
						</goals>
					</execution>
					<execution>
						<id>report</id>
						<phase>test</phase>
						<goals>
							<goal>report</goal>
						</goals>
					</execution>
				</executions>
			</plugin>

			<!-- Maven Surefire Plugin para ejecución de pruebas unitarias -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>3.2.5</version>
			</plugin>
		</plugins>
	</build>
	
</project>
```

---

## 4. Documento Explicativo: ¿Qué cambió en la actualización?

| Componente / Elemento | Estado Original (Antes) | Estado Actualizado (Después) | Justificación Técnica del Cambio |
| :--- | :--- | :--- | :--- |
| **Versión de Java (`<java.version>`)** | `11` (Java 11 LTS) | `21` (Java 21 LTS) | Actualización del bytecode generado a la versión de soporte LTS 21, permitiendo funciones avanzadas de la JVM y compatibilidad con Spring Boot 3.x. |
| **Spring Boot Parent (`spring-boot-starter-parent`)** | `2.7.17` | `3.3.4` | Spring Boot 2.7 no soporta el runtime de Java 21. La actualización a Spring Boot 3.3.4 brinda soporte nativo a Java 21 y Jakarta EE 10. |
| **Migración de Namespaces (`javax.*` a `jakarta.*`)** | `javax.persistence.*` / `javax.validation.*` | `jakarta.persistence.*` / `jakarta.validation.*` | Requerimiento estricto de Spring Boot 3+ y Jakarta EE 10 para todas las entidades JPA y anotaciones de validación de modelos. |
| **SonarQube Maven Plugin (`sonar-maven-plugin`)** | No presente | `5.5.0.6356` | Permite el análisis estático de código fuente, escaneo de vulnerabilidades, deudas técnicas y bugs mediante SonarQube / SonarCloud. |
| **JaCoCo Plugin (`jacoco-maven-plugin`)** | No presente | `0.8.12` | Habilita la instrumentación del código (`prepare-agent`) y la generación de reportes (`report`) del porcentaje de cobertura de código (code coverage) tras ejecutar los tests. |
| **Surefire Plugin (`maven-surefire-plugin`)** | Heredado por defecto | `3.2.5` | Garantiza la ejecución estable de pruebas unitarias sobre Java 21 e integración con el agente de cobertura de JaCoCo. |
| **Dependencia de Validación (`Ejercicio5Cliente`)** | No explícita | `spring-boot-starter-validation` | Spring Boot 3.x separa el soporte de validación a su propio starter; se incorpora para validar formularios en el controlador web. |
| **WebJars Bootstrap** | `5.1.3` | `5.3.3` | Actualización de componentes visuales y compatibilidad de estilos. |
| **Entorno CI/CD Jenkins** | Contenedor Jenkins JDK 17 | Contenedor Jenkins JDK 21 + Maven 3.9.0 | Configuración de las herramientas globales (`JDK21` y `M3`) para permitir compilaciones limpias de proyectos en Java 21. |
