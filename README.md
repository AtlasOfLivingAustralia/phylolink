### phylolink   [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/phylolink.svg?branch=master)](https://travis-ci.org/AtlasOfLivingAustralia/phylolink)

## Installing for development purposes

There are two parts to get phylolink installed on your system. First, you need to install the dependencies. Second, running phylolink locally, for example, on intellij.

Installing dependencies on local virtual machine

```
$ cd ~
$ git clone https://github.com/AtlasOfLivingAustralia/ala-install.git
$ cd ~/ala-install
$ cd vagrant/ubuntu-trusty
$ vagrant up
$ cd ../../ansible
$ ansible-playbook phylolink.yml -i inventories/vagrant/phylolink-vagrant --sudo --private-key ~/.vagrant.d/insecure_private_key -u vagrant
```
Create an entry into hosts file.
```
10.1.1.2 phylolink.vagrant1.ala.org.au
```
Save the config file as ```phylolink-config.properties``` in directory ```/data/phylolink/config/```.

## Installing on virtual machine
```
ansible-playbook phylolink.yml -i inventories/vagrant/phylolink-vagrant --sudo --private-key ~/.vagrant.d/insecure_private_key -u vagrant
```

## Installing on production
```
$ cd ala-install/ansible
$ ansible-playbook phylolink.yml -i ../../ansible-inventories/phylolink-prod -s --ask-sudo-pass
```
