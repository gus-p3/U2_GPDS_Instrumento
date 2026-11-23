variable "project_name" {
  type    = string
  default = "gestor-proyectos"
}

variable "backend_repo" {
  type        = string
  description = "usuario/repositorio del backend"
}

variable "frontend_repo" {
  type        = string
  description = "usuario/repositorio del frontend"
}

variable "repo_branch" {
  type    = string
  default = "main"
}

variable "domain_suffix" {
  type        = string
  description = "Texto unico para los subdominios (por ejemplo tus iniciales)"
}

variable "db_name" {
  type    = string
  default = "dbgestor"
}

variable "db_user" {
  type    = string
  default = "gestor"
}

variable "infra_repo" {
  type        = string
  description = "usuario/repositorio que contiene infra/monitoring"
}
