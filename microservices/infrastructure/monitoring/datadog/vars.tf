variable "datadog_api_key" {
  type        = string
  #  default     = "dev"
}
variable "datadog_app_key" {
  type        = string
  #  default     = "team"
}
variable "datadog_url" {
  type        = string
  default     = "https://api.datadoghq.eu/"
}
variable "api-service" {
  type        = string
  default     = "gw"
}
variable "solver-service" {
  type        = string
  default     = "solver"
}
variable "team" {
  type        = string
  default     = "tgedr"
}
variable "project" {
  type        = string
  default     = "microsvc-blueprint"
}
variable "notification-list" {
  type        = string
  default     = "@joao.viegas@maersk.com"
}
