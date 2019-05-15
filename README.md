### phylolink   [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/phylolink.svg?branch=master)](https://travis-ci.org/AtlasOfLivingAustralia/phylolink)

## Installing for development purposes

There are two parts to get phylolink installed on your system. 
First, you need to install the dependencies. Second, running phylolink locally, for example, on intellij.

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

Install postgres on your local machine. create role with password and database according to the information given in ```phylolink-config.properties```.


## Installing the DB locally

These instructions are for Mac OSX Yosemite. Adapt them as necessary for your environment.

1. Install Postgres (e.g. using Homebrew: ```brew install postgresql```)
1. Create the data directory (e.g. /data/postgres)
1. Make sure the owner of the directory is not a super user (i.e. not root) and is the same as the user who will run the postgres server
1. Make sure the directory permissions are 0700 (e.g. ```sudo chmod 700 /data/postgres```)
1. Configure the database: ```initdb -D /data/postgres```
1. Start the server: ```postgres -D /data/postgres```
1. Create the user: ```createuser phylo -P``` (-P prompts for a password: if not specified, the user will not have a password)
1. Create the database: ```createdb phylolink```

## Installing on local virtual machine with vagrant

Ansible scripts are in the [ala-install](https://github.com/AtlasOfLivingAustralia/ala-install) repository 

```
ansible-playbook phylolink.yml -i ala-install/ansible/inventories/vagrant/phylolink-vagrant --sudo --private-key ~/.vagrant.d/insecure_private_key -u vagrant
```

## Installing on production
```
$ cd ala-install/ansible
$ ansible-playbook phylolink.yml -i ../../ansible-inventories/phylolink-prod -s --ask-sudo-pass
```
