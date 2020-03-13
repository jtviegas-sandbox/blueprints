# microservices blueprint

## preliminary notes
  * solution, bash scripts, created in _macOS_ environment;
  * requirements:
    * minikube (v1.7.2) + kubectl (client: v1.17.3, server: v1.17.2) + docker engine community (v19.03.5);
  * the experiment can be conducted by using the script `infrastructure/run.sh` :

## how to run the experiment
  * create an `infrastructure/include.secret` file with the datadog account keys:
    ```
    DATADOG_APIKEY=db1XZXZXZXZXZXZXZXZX
    DATADOG_APPLICATION_KEY=d39XZXZXZXZXZXZXZXZX
    export TF_VAR_datadog_api_key=$DATADOG_APIKEY
    export TF_VAR_datadog_app_key=$DATADOG_APPLICATION_KEY
    # ARM_ACCESS_KEY will be created automatically with tfstate creation
    # if you use an already created storage account then edit this 
    export ARM_ACCESS_KEY=XPTO
    ``` 
  * setup minikube single kubernetes cluster;
    * delete any existent cluster: `minikube delete` ;
    * use the `infrastructure/run.sh kube on` to setup a new cluster, in the process we are :
      * creating rbac (role based access control) and a service account for the `datadog agent`;
      * creating the datadog `secrets`;
      * creating the `datadog daemon agent` in the cluster node;

  * deploy the services with `infrastructure/run.sh start`; 
    * wait until all the pods are `Running`
    * The services are:
      * `store` - a single replica of a local DynamoDb database (simple solution for testing purposes);
      * `solver` - scalable service, in the present experiment with 3 replicas, service with a single endpoint that receives the knapsack problems, creates a task in the store, using a wrapped service that, of which implementation the solver should not know about, connects to the store-service, responding with the task id and then solves it in the background thread pool, updating the state of the task when finished;
      * `gw` - scalable service with just 1 replica here, front service to the experiment:
        * we can inspect its swagger definitions, invoke `infrastructure/run.sh forward gw`:
          ...we can now open the `swagger UI` on the browser: http://localhost:9030/swagger-ui.html
          ...the same can be made to the `solver`, invoking `infrastructure/run.sh forward solver`, and check its `swagger UI` on port 9020;
  * we can now test the main use case:
    * send a problem to the api:
    * with the `taskId` (remember to urlencode the `|` to `%7C`) we can inspect its state, if it is already solved:
  * there is a load test, created with `jmeter`, that creates 120 threads in 1 minute, and each thread loops for 30x storing a problem, retrieving its solution by id, and then retrieving all the existent status related all the tasks created so far. 
    * the test can be kicked off with `infrastructure/run.sh loadtest`
    * there is also a load test that creates 406 errors: `infrastructure/run.sh errortest`
  * we can now also inspect the services performance, metrics and traces, among other info, and setup reliability monitoring in datadog:
    * first setup terraform remote state persistence if we don't already have it:
      * `infrastructure/run.sh tfstate on`
    * `infrastructure/run.sh datadog on`
    * we should now filter monitors and SLO's with the label `tgedr`, also we have live metrics, tracing, metrics, services, etc...
  * to clean the system we can now invoke:
    * `infrastructure/run.sh stop`
    * `infrastructure/run.sh kube off`
    * `infrastructure/run.sh datadog off`
    * if we don't need the terraform remote state persistence then `infrastructure/run.sh tfstate off`
  
## some notes regarding real world requirements for further developments

  * persistence - this solution is simplified for testing purposes, a real solution could be a DBaaS, with scalability, redundancy, and eventually geographic distribution;
  * rework of unfinished tasks - this could be attained with further development, the solver threads could have a timeout and there could be an extra solver handling stalled tasks;
  * stateManager usage - even if the idea is to isolate state management from its consumers, so that they just inject it and don't know anything about its implementation, the solver should use it an a separate thread while notifying changes of state while solving the task;
  * although there is static code analysis integrated in maven build (`pmd`), normally also we would have a bit  more of unit and integration tests, and test code coverage;



