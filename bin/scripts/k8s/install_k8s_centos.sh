#!/usr/bin/env bash


hostnamectl set-hostname $1
exec bash
setenforce 0
sed -i --follow-symlinks 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/sysconfig/selinux
echo '1' > /proc/sys/net/bridge/bridge-nf-call-iptables
modprobe br_netfilter
firewall-cmd --permanent --add-port=6443/tcp
firewall-cmd --permanent --add-port=2379-2380/tcp
firewall-cmd --permanent --add-port=10250/tcp
firewall-cmd --permanent --add-port=10251/tcp
firewall-cmd --permanent --add-port=10252/tcp
firewall-cmd --permanent --add-port=10255/tcp
firewall-cmd --reload

#create repo file
cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://packages.cloud.google.com/yum/repos/kubernetes-el7-x86_64
enable=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://packages.cloud.google.com/yum/doc/yum-key.gpg
        https://packages.cloud.google.com/yum/doc/rpm-package-key.gpg
EOF

#install k8s and docker
yum install kubeadm docker -y

#remaining config
systemctl restart docker && systemctl enable docker
systemctl  restart kubelet && systemctl enable kubelet

#for k8s master
kubeadm init
mkdir -p $HOME/.kube
cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
chown $(id -u):$(id -g) $HOME/.kube/config
# deploy network and kb network
export kubever=$(kubectl version | base64 | tr -d '\n')
kubectl apply -f
#verify status
kubectl get nodes
kubectl  get pods  --all-namespaces


#for k8s slaves
#grab the token that when run kubeadm init
kubeadm join --token a3bd48.1bc42347c3b35851 192.168.1.30:6443