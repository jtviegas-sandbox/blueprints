#!/bin/sh

debug(){
    local __msg="$1"
    echo "\n [DEBUG] `date` ... $__msg\n"
}

info(){
    local __msg="$1"
    echo "\n [INFO]  `date` ->>> $__msg\n"
}

warn(){
    local __msg="$1"
    echo "\n [WARN]  `date` *** $__msg\n"
}

err(){
    local __msg="$1"
    echo "\n [ERR]   `date` !!! $__msg\n"
}

this_folder="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
if [ -z "$this_folder" ]; then
  this_folder=$(dirname $(readlink -f $0))
fi
parent_folder=$(dirname "$this_folder")


if [ -f "$this_folder/include.secret" ]; then
    # debug "we have and 'include.secret' file"
    . "$this_folder/include.secret"
fi

if [ -f "$this_folder/include" ]; then
    # debug "we have and 'include' file"
    . "$this_folder/include"
fi

start()
{
  kubectl create -f "$this_folder"/service.yml
  kubectl create -f "$this_folder"/deployment.yml
}

stop()
{
  kubectl delete deployment "$APP_store" "$APP_solver" "$APP_gw"
  kubectl delete svc "$SVC_store" "$SVC_solver" "$SVC_gw"
}

inspect()
{
  local _service=$1
  local _deployment=$(eval echo \$APP_$_service)
  local _svc=$(eval echo \$SVC_$_service)

  kubectl get all
  kubectl describe deployment "$_deployment"
  kubectl describe svc "$_svc"
}

forward()
{
  local _service=$1
  local _port=$(eval echo \$PORT_$_service)
  local _svc=$(eval echo \$SVC_$_service)

  kubectl port-forward "svc/$_svc" "${_port}:${SERVICE_PORT}"
}

kubeOff()
{
  kubectl delete clusterrolebinding "$DATADOG_AGENT"
  kubectl delete clusterrole "$DATADOG_AGENT"
  kubectl delete serviceaccount "$DATADOG_AGENT"
  kubectl delete ds "$DATADOG_AGENT"
  kubectl delete secret "$DATADOG_SECRET"
  minikube stop
}

kubeOn()
{
  which minikube
  if [ ! "$?" -eq "0" ] ; then err "please install minikube" && return 1; fi

  minikube status | grep apiserver | grep Running
  if [ ! "$?" -eq "0" ] ; then
    minikube start --apiserver-port=6443 --cpus=4 --memory='4096mb' --v=1
    if [ ! "$?" -eq "0" ] ; then err "please review minikube, it could not start correctly" && return 1; fi
  fi

  kubectl create secret generic "$DATADOG_SECRET" --from-literal api-key="$DATADOG_APIKEY" --from-literal app-key="$DATADOG_APPLICATION_KEY"
  kubectl create -f "$this_folder/datadog-rbac.yml"
  kubectl create -f "$this_folder/datadog-agent.yml"

}

performance()
{
  which jmeter
  if [ ! "$?" -eq "0" ] ; then err "please install jmeter" && return 1; fi

  jmeter -n -t "$this_folder/$LOAD_TEST_SPEC"
}

errortest()
{
  which jmeter
  if [ ! "$?" -eq "0" ] ; then err "please install jmeter" && return 1; fi

  jmeter -n -t "$this_folder/$LOAD_ERR_TEST_SPEC"
}

datadogOn()
{
  info "[datadogOn|in]"
  cd "$this_folder/$DATADOG_DIR"
  svn export "$MODULES_URL" "./$MODULES_DIR"
  terraform init
  terraform plan
  terraform apply -auto-approve -lock=true -lock-timeout=10m
  terraform output
  rm -rf "./$MODULES_DIR"
  cd "$_pwd"
  info "[datadogOn|out]"
}

datadogOff()
{
  info "[datadogOff|in]"
  cd "$this_folder/$DATADOG_DIR"
  svn export "$MODULES_URL" "./$MODULES_DIR"
  terraform init
  terraform destroy -auto-approve -lock=true -lock-timeout=10m
  rm -rf "./$MODULES_DIR"
  cd "$_pwd"
  info "[datadogOff|out]"
}

tfstateOn()
{
  info "[tfstateOn|in]"
  cd "$this_folder/$TFSTATE_DIR"
  svn export "$MODULES_URL" "./$MODULES_DIR"
  terraform init
  terraform plan
  terraform apply -auto-approve -lock=true -lock-timeout=10m
  grep -v ARM_ACCESS_KEY "$this_folder"/include.secret > include.secret-tmp
  echo "export ARM_ACCESS_KEY=$(terraform output storage-account-access-key)" >> include.secret-tmp
  mv include.secret-tmp "$this_folder"/include.secret
  rm -f include.secret-tmp
  rm -rf "./$MODULES_DIR"
  cd "$_pwd"
  info "[tfstateOn|out]"
}

tfstateOff()
{
  info "[tfstateOff|in]"
  cd "$this_folder/$TFSTATE_DIR"
  svn export "$MODULES_URL" "./$MODULES_DIR"
  terraform init
  terraform destroy -auto-approve -lock=true -lock-timeout=10m
  rm -rf "./$MODULES_DIR"
  cd "$_pwd"
  info "[tfstateOff|out]"
}

usage()
{
  cat <<EOM
  usage:
  setup minikube cluster for testing purposes
      $(basename $0) kube {on|off}
  start/stop all services
      $(basename $0) {start|stop}
  inspect or port-forward a specific service
      $(basename $0) {inspect|forward} {store|solver|gw}
  performance test with jmeter
      $(basename $0) performance
  load errors test with jmeter
      $(basename $0) errortest
  setup datadog sli's and slo's
      $(basename $0) datadog {on|off}
  setup terraform remote state storage on azure
      $(basename $0) tfstate {on|off}
EOM
  exit 1
}

case "$1" in
        start)
          start
            ;;
        stop)
          stop
            ;;
        inspect)
          [ "$2" != "store" ] && [ "$2" != "solver" ] && [ "$2" != "gw" ] && { usage; }
          inspect "$2"
            ;;
        forward)
          [ "$2" != "store" ] && [ "$2" != "solver" ] && [ "$2" != "gw" ] && { usage; }
          forward "$2"
            ;;
        kube)
          case "$2" in
            on)
              kubeOn
              ;;
            off)
              kubeOff
              ;;
            *)
              usage
          esac
          ;;
        datadog)
          case "$2" in
            on)
              datadogOn
              ;;
            off)
              datadogOff
              ;;
            *)
              usage
          esac
          ;;
        tfstate)
          case "$2" in
            on)
              tfstateOn
              ;;
            off)
              tfstateOff
              ;;
            *)
              usage
          esac
          ;;
        performance)
          performance
            ;;
        errortest)
          errortest
            ;;
        *)
          usage
esac



