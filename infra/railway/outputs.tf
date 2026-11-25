output "project_id" {
  value = railway_project.gestor.id
}

output "frontend_url" {
  value = "https://${railway_service_domain.frontend.domain}"
}

output "backend_url" {
  value = "https://${railway_service_domain.backend.domain}"
}

output "grafana_url" {
  value = "https://${railway_service_domain.grafana.domain}"
}

output "grafana_admin_password" {
  value     = random_password.grafana_admin.result
  sensitive = true
}

output "jenkins_url" {
  value = "https://${railway_service_domain.jenkins.domain}"
}

output "sonarqube_url" {
  value = "https://${railway_service_domain.sonarqube.domain}"
}