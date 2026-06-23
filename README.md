
# EKS Cluster Setup on Windows with AWS CLI

Since your AWS credentials are already configured, this README gives you the full command-line flow to create an Amazon EKS cluster from a Windows machine. AWS recommends using `eksctl` for the fastest path, and you can then use `aws eks update-kubeconfig` to connect `kubectl` to the cluster [web:24][web:21].

## Prerequisites

Install these tools on your Windows machine:

- AWS CLI v2.
- `kubectl`.
- `eksctl` for easiest cluster creation.

AWS docs say you need these command-line tools to prepare, create, and manage EKS clusters [web:6][web:25]. After installation, verify each one with `aws --version`, `kubectl version --client`, and `eksctl version` [web:25][web:13].

## Step 1: Verify AWS access

Run:

```powershell
aws --version

kubectl version --client

eksctl version

aws sts get-caller-identity
```

If the command returns your account details, your credentials are working. AWS uses this CLI access as the foundation for EKS setup and cluster management [web:6][web:24].

## Step 2: Check region

Check your current default region:

```powershell
aws configure get region
```

If you need to set it, run:

```powershell
aws configure
```

or:

```powershell
aws configure set region ap-south-1
```

AWS’s EKS docs note that region selection is part of preparing the CLI environment before cluster creation [web:6][web:24].

## Step 3: Create the cluster

A simple managed-node EKS cluster example:

```powershell
eksctl create cluster `
  --name my-eks-cluster `
  --region ap-south-1 `
  --nodegroup-name worker-nodes `
  --node-type t3.medium `
  --nodes 2 `
  --nodes-min 2 `
  --nodes-max 4 `
  --managed
```

`eksctl` is the simplest command-line option for creating a new EKS cluster, and AWS documents `create-cluster` as the core action for provisioning the control plane [web:24][web:26][web:12]. The command usually takes several minutes because EKS creates the control plane and related infrastructure [web:26].

## Step 4: What gets created

When you run `eksctl create cluster`, it typically creates:

- VPC.
- Subnets.
- Security groups.
- EKS control plane.
- Managed node group.
- IAM roles.

AWS’s EKS documentation confirms that cluster creation involves the control plane plus supporting VPC resources and node connectivity [web:26][web:24].

## Step 5: Confirm cluster creation

List clusters:

```powershell
aws eks list-clusters --region ap-south-1
```

Describe the cluster:

```powershell
aws eks describe-cluster `
  --name my-eks-cluster `
  --region ap-south-1
```

`aws eks describe-cluster` is the standard way to confirm the cluster exists and inspect its status, endpoint, and configuration [web:26].

## Step 6: Configure kubectl

Update your kubeconfig so `kubectl` can talk to the cluster:

```powershell
aws eks update-kubeconfig `
  --region ap-south-1 `
  --name my-eks-cluster
```

AWS says `update-kubeconfig` writes the cluster connection info into your kubeconfig file so Kubernetes tools can access the EKS API server [web:21][web:20].

## Step 7: Verify Kubernetes access

Check nodes:

```powershell
kubectl get nodes
```

Check cluster info:

```powershell
kubectl cluster-info
```

If the nodes show `Ready`, the cluster and kubeconfig are working correctly. This is the standard verification step after connecting Kubernetes tooling to EKS [web:24][web:21].

## Step 8: Deploy a test app

Create a simple Nginx deployment:

```powershell
kubectl create deployment nginx --image=nginx
```

Expose it with a load balancer:

```powershell
kubectl expose deployment nginx `
  --port=80 `
  --type=LoadBalancer
```

Then check the service:

```powershell
kubectl get svc
```

AWS’s EKS getting-started guidance confirms that once the cluster is up, you can deploy applications to it using standard Kubernetes tooling [web:24].

## Step 9: Delete the cluster

When you are done, remove the cluster to stop charges:

```powershell
eksctl delete cluster `
  --name my-eks-cluster `
  --region ap-south-1
```

AWS notes that cluster creation is only part of the flow; cleanup is important after testing or development work [web:24][web:26].

## Production example

For a more production-style setup:

```powershell
eksctl create cluster `
  --name prod-eks `
  --region ap-south-1 `
  --version 1.33 `
  --nodegroup-name prod-workers `
  --node-type t3.large `
  --nodes 3 `
  --nodes-min 3 `
  --nodes-max 10 `
  --managed
```

This follows the same EKS creation pattern, but with larger nodes and a bigger managed node group for production-style usage [web:24][web:26].
