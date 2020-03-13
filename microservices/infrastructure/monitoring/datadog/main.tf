provider "datadog" {
  api_key = "${var.datadog_api_key}"
  app_key = "${var.datadog_app_key}"
  api_url = "${var.datadog_url}"
}

terraform {
  backend "azurerm" {
    storage_account_name  = "devtgedrremotestates"
    container_name        = "blueprint"
    key                   = "microsvc"
  }
}

module "monitor-api-uptime" {
  source = "./modules/datadog/monitor-uptime"
  service-name = "${var.api-service}"
  team = "${var.team}"
  project = "${var.project}"
  notification-list = "${var.notification-list}"
}

module "slo-api-uptime" {
  source = "./modules/datadog/slo-uptime"
  service-name = "${var.api-service}"
  team = "${var.team}"
  project = "${var.project}"
  monitor-id = module.monitor-api-uptime.id
}

module "monitor-api-successrate" {
  source = "./modules/datadog/monitor-sucessrate"
  service-name = "${var.api-service}"
  team = "${var.team}"
  project = "${var.project}"
  notification-list = "${var.notification-list}"
}

module "slo-api-successrate" {
  source = "./modules/datadog/slo-successrate"
  service-name = "${var.api-service}"
  team = "${var.team}"
  project = "${var.project}"
}

module "monitor-solver-uptime" {
  source = "./modules/datadog/monitor-uptime"
  service-name = "${var.solver-service}"
  team = "${var.team}"
  project = "${var.project}"
  notification-list = "${var.notification-list}"
}

module "slo-solver-uptime" {
  source = "./modules/datadog/slo-uptime"
  service-name = "${var.solver-service}"
  team = "${var.team}"
  project = "${var.project}"
  monitor-id = module.monitor-solver-uptime.id
}

module "monitor-solver-successrate" {
  source = "./modules/datadog/monitor-sucessrate"
  service-name = "${var.solver-service}"
  team = "${var.team}"
  project = "${var.project}"
  notification-list = "${var.notification-list}"
}

module "slo-solver-successrate" {
  source = "./modules/datadog/slo-successrate"
  service-name = "${var.solver-service}"
  team = "${var.team}"
  project = "${var.project}"
}
