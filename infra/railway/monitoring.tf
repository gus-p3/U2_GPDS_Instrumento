resource "random_password" "grafana_admin" {
  length  = 24
  special = false
}

# ---------- Prometheus ----------
resource "railway_service" "prometheus" {
  name               = "prometheus"
  project_id         = railway_project.gestor.id
  source_repo        = var.infra_repo
  source_repo_branch = var.repo_branch
  root_directory     = "infra/monitoring/prometheus"
  volume = {
    name       = "prometheus-data"
    mount_path = "/prometheus"
  }
}

resource "railway_variable" "prometheus_port" {
  name           = "PORT"
  value          = "9090"
  environment_id = local.env_id
  service_id     = railway_service.prometheus.id
}

# El volumen se monta como root y la imagen corre como usuario sin privilegios
resource "railway_variable" "prometheus_uid" {
  name           = "RAILWAY_RUN_UID"
  value          = "0"
  environment_id = local.env_id
  service_id     = railway_service.prometheus.id
}

# ---------- Grafana ----------
resource "railway_service" "grafana" {
  name               = "grafana"
  project_id         = railway_project.gestor.id
  source_repo        = var.infra_repo
  source_repo_branch = var.repo_branch
  root_directory     = "infra/monitoring/grafana"
  volume = {
    name       = "grafana-data"
    mount_path = "/var/lib/grafana"
  }
}

resource "railway_variable" "grafana_port" {
  name           = "PORT"
  value          = "3000"
  environment_id = local.env_id
  service_id     = railway_service.grafana.id
}

resource "railway_variable" "grafana_http_port" {
  name           = "GF_SERVER_HTTP_PORT"
  value          = "3000"
  environment_id = local.env_id
  service_id     = railway_service.grafana.id
}

resource "railway_variable" "grafana_uid" {
  name           = "RAILWAY_RUN_UID"
  value          = "0"
  environment_id = local.env_id
  service_id     = railway_service.grafana.id
}

resource "railway_variable" "grafana_admin_user" {
  name           = "GF_SECURITY_ADMIN_USER"
  value          = "admin"
  environment_id = local.env_id
  service_id     = railway_service.grafana.id
}

resource "railway_variable" "grafana_admin_password" {
  name           = "GF_SECURITY_ADMIN_PASSWORD"
  value          = random_password.grafana_admin.result
  environment_id = local.env_id
  service_id     = railway_service.grafana.id
}

resource "railway_variable" "grafana_no_signup" {
  name           = "GF_USERS_ALLOW_SIGN_UP"
  value          = "false"
  environment_id = local.env_id
  service_id     = railway_service.grafana.id
}

resource "railway_variable" "grafana_prometheus_url" {
  name           = "PROMETHEUS_URL"
  value          = "http://$${{prometheus.RAILWAY_PRIVATE_DOMAIN}}:9090"
  environment_id = local.env_id
  service_id     = railway_service.grafana.id
}

resource "railway_service_domain" "grafana" {
  subdomain      = "gestor-grafana-${var.domain_suffix}"
  environment_id = local.env_id
  service_id     = railway_service.grafana.id
}
