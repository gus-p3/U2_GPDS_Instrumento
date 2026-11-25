# ---------- Base de datos PostgreSQL para SonarQube ----------
resource "random_password" "sonar_db" {
  length  = 24
  special = false
}

resource "railway_service" "sonar_db" {
  name         = "sonar-db"
  project_id   = railway_project.gestor.id
  source_image = "postgres:16-alpine"
  volume = {
    name       = "sonar-db-data"
    mount_path = "/var/lib/postgresql/data"
  }
}

resource "railway_variable" "sonar_db_name" {
  name           = "POSTGRES_DB"
  value          = "sonar"
  environment_id = local.env_id
  service_id     = railway_service.sonar_db.id
}

resource "railway_variable" "sonar_db_user" {
  name           = "POSTGRES_USER"
  value          = "sonar"
  environment_id = local.env_id
  service_id     = railway_service.sonar_db.id
}

resource "railway_variable" "sonar_db_password" {
  name           = "POSTGRES_PASSWORD"
  value          = random_password.sonar_db.result
  environment_id = local.env_id
  service_id     = railway_service.sonar_db.id
}

# ---------- SonarQube ----------
resource "railway_service" "sonarqube" {
  name         = "sonarqube"
  project_id   = railway_project.gestor.id
  source_image = "sonarqube:community"
}

resource "railway_variable" "sonar_port" {
  name           = "PORT"
  value          = "9000"
  environment_id = local.env_id
  service_id     = railway_service.sonarqube.id
}

resource "railway_variable" "sonar_es_disable" {
  name           = "SONAR_ES_BOOTSTRAP_CHECKS_DISABLE"
  value          = "true"
  environment_id = local.env_id
  service_id     = railway_service.sonarqube.id
}

resource "railway_variable" "sonar_jdbc_url" {
  name           = "SONAR_JDBC_URL"
  value          = "jdbc:postgresql://$${{sonar-db.RAILWAY_PRIVATE_DOMAIN}}:5432/sonar"
  environment_id = local.env_id
  service_id     = railway_service.sonarqube.id
}

resource "railway_variable" "sonar_jdbc_user" {
  name           = "SONAR_JDBC_USERNAME"
  value          = "$${{sonar-db.POSTGRES_USER}}"
  environment_id = local.env_id
  service_id     = railway_service.sonarqube.id
}

resource "railway_variable" "sonar_jdbc_password" {
  name           = "SONAR_JDBC_PASSWORD"
  value          = "$${{sonar-db.POSTGRES_PASSWORD}}"
  environment_id = local.env_id
  service_id     = railway_service.sonarqube.id
}

resource "railway_service_domain" "sonarqube" {
  subdomain      = "gestor-sonar-${var.domain_suffix}"
  environment_id = local.env_id
  service_id     = railway_service.sonarqube.id
}

# ---------- Jenkins ----------
resource "railway_service" "jenkins" {
  name               = "jenkins"
  project_id         = railway_project.gestor.id
  source_repo        = var.infra_repo
  source_repo_branch = var.repo_branch
  root_directory     = "infra/ci/jenkins"
  volume = {
    name       = "jenkins-data"
    mount_path = "/var/jenkins_home"
  }
}

resource "railway_variable" "jenkins_port" {
  name           = "PORT"
  value          = "8080"
  environment_id = local.env_id
  service_id     = railway_service.jenkins.id
}

resource "railway_variable" "jenkins_uid" {
  name           = "RAILWAY_RUN_UID"
  value          = "0"
  environment_id = local.env_id
  service_id     = railway_service.jenkins.id
}

resource "railway_variable" "jenkins_sonar_url" {
  name           = "SONAR_HOST_URL"
  value          = "http://$${{sonarqube.RAILWAY_PRIVATE_DOMAIN}}:9000"
  environment_id = local.env_id
  service_id     = railway_service.jenkins.id
}

resource "railway_service_domain" "jenkins" {
  subdomain      = "gestor-jenkins-${var.domain_suffix}"
  environment_id = local.env_id
  service_id     = railway_service.jenkins.id
}