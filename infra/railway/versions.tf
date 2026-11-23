terraform {
  required_version = ">= 1.6.0"
  required_providers {
    railway = {
      source  = "terraform-community-providers/railway"
      version = "~> 0.6.3"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }
}

# El token llega por la variable de entorno RAILWAY_TOKEN
provider "railway" {}
