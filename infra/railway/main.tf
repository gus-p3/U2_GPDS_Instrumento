# ---------- Secretos generados (nadie los escribe) ----------
resource "random_password" "db_root" {
  length  = 28
  special = false
}

resource "random_password" "db_app" {
  length  = 28
  special = false
}

# ---------- Proyecto ----------
resource "railway_project" "gestor" {
  name        = var.project_name
  description = "Proyecto integrador GIDS6102 - Gestor de Proyectos"
  private     = true

  default_environment = {
    name = "production"
  }
}

locals {
  env_id = railway_project.gestor.default_environment.id
}

# ---------- MariaDB ----------
resource "railway_service" "mariadb" {
  name         = "mariadb"
  project_id   = railway_project.gestor.id
  source_image = "mariadb:11.3"

  volume = {
    name       = "mariadb-data"
    mount_path = "/var/lib/mysql"
  }
}

resource "railway_variable" "db_root_password" {
  name           = "MARIADB_ROOT_PASSWORD"
  value          = random_password.db_root.result
  environment_id = local.env_id
  service_id     = railway_service.mariadb.id
}

resource "railway_variable" "db_name" {
  name           = "MARIADB_DATABASE"
  value          = var.db_name
  environment_id = local.env_id
  service_id     = railway_service.mariadb.id
}

resource "railway_variable" "db_user" {
  name           = "MARIADB_USER"
  value          = var.db_user
  environment_id = local.env_id
  service_id     = railway_service.mariadb.id
}

resource "railway_variable" "db_password" {
  name           = "MARIADB_PASSWORD"
  value          = random_password.db_app.result
  environment_id = local.env_id
  service_id     = railway_service.mariadb.id
}

# ---------- Backend ----------
resource "railway_service" "backend" {
  name               = "backend-api"
  project_id         = railway_project.gestor.id
  source_repo        = var.backend_repo
  source_repo_branch = var.repo_branch
}

resource "railway_variable" "backend_port" {
  name           = "PORT"
  value          = "8081"
  environment_id = local.env_id
  service_id     = railway_service.backend.id
}

# $${{...}} en HCL produce la referencia ${{...}} que Railway resuelve
resource "railway_variable" "backend_db_url" {
  name           = "DB_URL"
  value          = "jdbc:mariadb://$${{mariadb.RAILWAY_PRIVATE_DOMAIN}}:3306/${var.db_name}"
  environment_id = local.env_id
  service_id     = railway_service.backend.id
}

resource "railway_variable" "backend_db_user" {
  name           = "DB_USER"
  value          = "$${{mariadb.MARIADB_USER}}"
  environment_id = local.env_id
  service_id     = railway_service.backend.id
}

resource "railway_variable" "backend_db_password" {
  name           = "DB_PASSWORD"
  value          = "$${{mariadb.MARIADB_PASSWORD}}"
  environment_id = local.env_id
  service_id     = railway_service.backend.id
}

# ---------- Frontend ----------
resource "railway_service" "frontend" {
  name               = "frontend"
  project_id         = railway_project.gestor.id
  source_repo        = var.frontend_repo
  source_repo_branch = var.repo_branch
}

resource "railway_variable" "frontend_port" {
  name           = "PORT"
  value          = "8080"
  environment_id = local.env_id
  service_id     = railway_service.frontend.id
}

resource "railway_variable" "frontend_backend_url" {
  name           = "BACKEND_URL"
  value          = "http://$${{backend-api.RAILWAY_PRIVATE_DOMAIN}}:$${{backend-api.PORT}}"
  environment_id = local.env_id
  service_id     = railway_service.frontend.id
}

# ---------- Dominios publicos ----------
resource "railway_service_domain" "frontend" {
  subdomain      = "gestor-web-${var.domain_suffix}"
  environment_id = local.env_id
  service_id     = railway_service.frontend.id
}

resource "railway_service_domain" "backend" {
  subdomain      = "gestor-api-${var.domain_suffix}"
  environment_id = local.env_id
  service_id     = railway_service.backend.id
}
