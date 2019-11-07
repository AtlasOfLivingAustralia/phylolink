### phylolink   [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/phylolink.svg?branch=master)](https://travis-ci.org/AtlasOfLivingAustralia/phylolink)

## Installing for development purposes

### Install required software

Ensure the correct versions of vagrant and ansible are installed as detailed on https://github.com/AtlasOfLivingAustralia/ala-install.  As at Sep 2019 this involved:

For APT:
```
$ sudo apt-get install software-properties-common python-dev git python-pip
$ sudo pip install setuptools
$ sudo pip install -I ansible==2.7.0
```

For OSX:
```
$ sudo easy_install pip
$ sudo pip install -I ansible==2.7.0
```

Then install vagrant for Debian/Ubuntu:
```
$ sudo apt-get install virtualbox virtualbox-dkms virtualbox-qt
$ cd ~/Downloads
$ wget https://releases.hashicorp.com/vagrant/2.0.4/vagrant_2.0.4_x86_64.deb
$ sudo dpkg -i vagrant_2.0.4_x86_64.deb
$ vagrant plugin install vagrant-disksize
```

### Installing the phylolink application to the local vagrant container

```
$ cd ~
$ git clone https://github.com/AtlasOfLivingAustralia/ala-install.git
$ cd ~/ala-install/vagrant/ubuntu-xenial
```

The default configuration for the phylolink vagrant container is to assign 8GB of RAM.  If you wish to reduce this:
```
$ sudo vim ~/ala-install/vagrant/ubuntu-xenial/Vagrantfile
    and change the line v.memory = 8192.  I have had this as low as 1536 and it has been fine for a local environemnt.
```

Please note that the first execution of starting up the vagrant container, downloads the Ubuntu image which can take 20 minutes or more.
```
$ cd ~/ala-install/vagrant/ubuntu-xenial
$ vagrant up
```

The ALA ansible inventories (in the ansible/inventories/vagrant directory) refer to the VM as 'vagrant1' rather than the IP address. For this to work, you will need to add an entry to your ```/etc/hosts``` file.  The IP address of the vagrant container is defined in the ```~/ala-install/vagrant/ubuntu-xenial/Vagrantfile``` which is currently ```config.vm.network :private_network, ip: "10.1.1.4"```, so the hosts file entry is:
```
10.1.1.4  vagrant1 phylolink.vagrant1.ala.org.au
```

The ansible scripts can then be run to provision the vagrant container and deploy the phylolink application and its dependencies (including its postgres database):
```
$ cd ~/ala-install/ansible
$ ansible-playbook phylolink.yml -i inventories/vagrant/phylolink-vagrant --sudo --private-key ~/.vagrant.d/insecure_private_key -u vagrant
```
Please note you can safely ignore the error message:
```
RUNNING HANDLER [common : restart tomcat] 
fatal: [vagrant1]: FAILED! => {"changed": false, "msg": "Could not find the requested service tomcat7: host"}
```
as tomcat is not installed.  Instead tomcat is embedded in the phylolink application "fat jar".  If you want to avoid the error you will need to edit ```~/ala-install/ansible/roles/nameindex/tasks/main.yml``` and comment out the two instances of:
```
  notify:
    - restart tomcat
```

To log into the vagrant container perform the following commands:
```
$ cd ~/ala-install/vagrant/ubuntu-xenial
$ vagrant ssh
```

The phylolink application externalises its configuration into the file ```/data/phylolink/config/phylolink-config.properties``` on the vagrant container.
The webservice.apiKey property will need to be updated, please speak to a member of the project team to get a valid value.  Note this value is sensitive, which is why it is not freely available.  Once the webservice.apiKey property is update you will need to stop and start the application and then confirm its status:
```
$ sudo vim /data/phylolink/config/phylolink-config.properties
$ sudo systemctl stop phylolink
$ sudo systemctl start phylolink
$ sudo systemctl status phylolink
```

After halting and starting the vagrant container you may find that web2py doesn't start, resulting in the inability to add a new phylogenetic tree.  To fix this:
```
$ sudo vim /etc/init.d/web2py
	In the do_start() function, comment out the following 2 lines by inserting # at the start of the lines
		start-stop-daemon --stop --test --quiet --pidfile $PIDFILE \
			&& return 1
	save and exit vi (:x)
$ sudo systemctl daemon-reload
$ sudo systemctl start web2py
$ sudo systemctl status web2py
	Should report that the service is "active (running)"
```

### Connecting to the phyolink postgres db

Install a postgres client on your machine e.g. https://www.pgadmin.org/
You can then try and to connect to 
```
server: vagrant1
port: 5432
with user name and password as defined in /data/phylolink/config/phylolink-config.properties in the dataSource.username and dataSource.password properties
```
and view the database with name phyolink.  

This will most probably fail the first time.  In the error message an IP address should be listed.  Following the instructions on http://theneum.com/blog/connect-to-vagrant-postgres-database-via-pgadmin3-on-mac/, on the vagrant container ssh:
```
$ sudo vim /etc/postgresql/9.6/main/postgresql.conf
    uncomment listen_addresses and set to be = '*' 
$ sudo vim /etc/postgresql/9.6/main/pg_hba.conf
    add the line: 
	host    all             all             [IP address in error message]/16             md5
$ sudo /etc/init.d/postgresql restart    
```

### Testing that the installation of the phylolink application on the vagrant container has succeeded:
View http://phylolink.vagrant1.ala.org.au in a browser.  For the standard ansible install, this is running the phyolink grails application on the vagrant container which has been compiled as a "fat jar" with an embedded tomcat container in the location ```/opt/atlas/phylolink/phylolink.jar```.  This is started by a systemd service called phylolink (which is defined in the standard ubuntu location of ```/etc/systemd/system/phylolink.service```).  To view the status of the phylolink service run the following command:
```
$ sudo systemctl status phylolink
```

You can now exit from the vagrant container secure shell with the command:
```
$ exit
```

### Running phylolink from an IDE for rapid development

* Clone phylolink from GitHub e.g. 
```
$ cd ~
$ git clone https://github.com/AtlasOfLivingAustralia/phylolink.git
```
* Install java 1.8 if you don't already have it
* Install the grails version specified in ~/phylolink/gradle.properties.  At the time of writing that is version 3.3.2.  Following  http://grails.asia/grails-3-tutorial-setup-your-windows-development-environment/ or http://docs.grails.org/3.3.2/guide/single.html.
  * Add Gradle Plugin to your IDE if you don't already have it (for eclipse see https://www.vogella.com/tutorials/EclipseGradle/article.html)
  * Add Groovy Plugin to your IDE if you don't already have it (for eclipse see https://github.com/groovy/groovy-eclipse/wiki)
  * Import the gradle project into your IDE (e.g. File -> Import -> Gradle -> Root Folder ~/phylolink)
* Create the following directory structure: ```/data/phylolink/config```
* Copy all files in ~/ala-install/ansible/roles/phylolink/templates to /data/phylolink/config and substitute the variable values (speak to someone from the development team)
* Install the name index by 
  * Download the version of the names index as specified in the nameindex_datestamp property in ~/ala-install/ansible/inventories/vagrant/phylolink-vagrantfile from https://archives.ala.org.au/archives/nameindexes (At the time of writing the value was 20171012-lucene5 so the URL to download from is https://archives.ala.org.au/archives/nameindexes/20171012-lucene5/namematching-20171012-lucene5.tgz)
  * Unzip it to /data/lucene/namematching so that you end up with the cb, id, irmng and vernacular sub folders
  * NOTE: the name.index.location property in /data/phylolink/config/phylolink-config.properties must match the /data/lucene/namematching folder name
* Add a hosts file entry: 127.0.0.1    devt.ala.org.au
* Start the grails application:
```
$ cd ~/phylolink
$ grails run-app -port=8090
```
* To be able to view the emails sent during the approval workflow, you will have a local mail server. The config.groovy is set up to point at a mail server on port 1025. https://nilhcem.github.io/FakeSMTP/download.html is a good option, which can be run with the following command in a different terminal to terminal where you are running grails:
```
java -jar ~/fakeSMTP-2.0.jar --start-server --port 1025
```
* View http://devt.ala.org.au:8090/phylolink in a browser to test the application which is running the grails part of the application locally and consuming the web services and postgres db running in the vagrant container.

## Installing on production
```
$ cd ala-install/ansible
$ ansible-playbook phylolink.yml -i ../../ansible-inventories/phylolink-prod -s --ask-sudo-pass
```

