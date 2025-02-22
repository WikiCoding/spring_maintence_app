# Summary
This application has 3 types of users. 
Admin, which can create all users. 
Manager, which can delete Tickets, see all Technician's Tickets (paged and sorted) and will get a notification (just a log) when a Technician completes a Ticket.
Technician, which can see their own Tickets, create their own tickets and complete their own Tickets (paged and sorted)
No particular concurrency concerns were taken into account here, therefore all Databases interactions are actually synchronous.

# Start the app
```bash
docker-compose up -d
```

# Kafka
You can view the Kafka UI at `localhost:8080` in your browser

# Use the app
First of all login, grab the token and start using it at the Authorization headers. At the root of this project there's **app.http** with a few sample requests.

# Using Redis CLI
```bash
redis-cli
keys *
HGETALL <key>

# deleting all data 
FLUSHALL
```

# To publish you image to dockerhub
1. `docker login`
2. `docker build -t wikicoding/kubernetesapp:latest .`
3. Create the repository in dockerhub
4. `docker push wikicoding/kubernetesapp:latest`

# K8s commands
1. `kubectl delete deployment webapp-deployment`
2. kubectl delete service webapp-service
3. `kubectl apply -f k8s.yml`
4. `kubectl get deployments`
5. `kubectl get services`
6. `kubectl get pods`
7. `kubectl logs webapp-deployment-d58976867-chgxz`
8. `kubectl describe pod webapp-deployment-d58976867-cxv87`
9. `minikube service list` see all running services
10. `minikube service webapp` to access the app

For private images in docker hub
```bash
kubectl create secret docker-registry my-dockerhub-secret \
  --docker-username=<your-username> \
  --docker-password=<your-password> \
  --docker-email=<your-email>
```

Then the **k8s.yml**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: webapp-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: webapp
  template:
    metadata:
      labels:
        app: webapp
    spec:
      containers:
        - name: webapp
          image: wikicoding/maintenanceapp:latest
          ports:
            - containerPort: 8080
      imagePullSecrets:
        - name: my-dockerhub-secret
---
apiVersion: v1
kind: Service
metadata:
  name: webapp-service
spec:
  type: NodePort
  selector:
    app: webapp
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
      nodePort: 30007
```