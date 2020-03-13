provider "azurerm" {
  version = "=2.0.0"
  features {}
}


module "remote-state" {
  source = "./modules/azure/tf-remote-state-v2"
  env = "${var.env}"
  team = "${var.team}"
  project = "${var.project}"
  app = "${var.app}"
  storage-account-name = "${var.storage-account-name}"
  storage-container-name = "${var.storage-container-name}"
  resource-group-name = "${var.resource-group}"
  resource-group-region = "${var.region}"
}

output "storage-account-access-key" {
  value = module.remote-state.storage-account-access-key
}

