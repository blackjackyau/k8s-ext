
## K8s Operator in Spring Boot
- Refers to [medium page](https://ysyau.medium.com/extending-kubernetes-the-java-way-b1de58f688b8) for detailed write up

## Minikube
- install minikube from https://minikube.sigs.k8s.io/docs/start/
- `minikube start`
- `minikube dashboard`

## Local Setup
- deploy the `AirCond` CRD to minikube. from k8s folder `kubectl apply -f airconds.com.blax-v1.yml` 
- As the `AirCond` CRD is a `Namespaced` type, create a namespace for testing `kubectl create namespace operator-test`
- Run the application with `bootRun`
- from the logs `Registered reconciler: 'aircondreconciler' for resource: 'class com.blax.k8s.operator.crd.AirCond' for namespace(s): [all namespaces]` should be observed indicating the operator is registered successfully.
- create the aircond object in the operator-test ns. `kubectl apply -f aircond.yml`
- you should see the Reconciling is kept kicking in every 30 seconds from the log.
- navigate the `operator-test` namespace, observe the currentTemp under the AirCond object status, the temperature should go down every 30s until it reaches the desired temp. Notice the same applies to the configmap object.

## Deploy the operator in Minikube
- deploy the `AirCond` CRD to minikube. from k8s folder `kubectl apply -f airconds.com.blax-v1.yml`
- As the `AirCond` CRD is a `Namespaced` type, create a namespace for testing `kubectl create namespace operator-test` 
- trigger `bootBuildImage` from the gradle task to build the image locally `docker.io/library/k8s-ext:0.0.1-SNAPSHOT`
- load the image into minikube `minikube image load docker.io/library/k8s-ext:0.0.1-SNAPSHOT`
- As Operator in POD will need the necessary RBAC to operate, we need to setup the necessary roles and service account. `kubectl apply -f service-account.yaml -n operator-test`  
- and finally the operator deployment `kubectl apply -f deployment.yaml -n operator-test`
- from the logs `Registered reconciler: 'aircondreconciler' for resource: 'class com.blax.k8s.operator.crd.AirCond' for namespace(s): [all namespaces]` should be observed indicating the operator is registered successfully.
- create the aircond object in the operator-test ns. `kubectl apply -f aircond.yml`
- you should see the Reconciling is kept kicking in every 30 seconds from the log.
- navigate the `operator-test` namespace, observe the currentTemp under the AirCond object status, the temperature should go down every 30s until it reaches the desired temp. Notice the same applies to the configmap object.