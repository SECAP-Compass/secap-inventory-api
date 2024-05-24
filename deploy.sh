kubectl apply -f psql.deployment.yaml -n persistence

docker image rm secap-inventory:latest
docker build -t secap-inventory:latest .

minikube image rm secap-inventory:latest
minikube image load secap-inventory:latest

kubectl apply -f .k8s/deployment.yaml -n secap-compass