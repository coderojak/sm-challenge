#SM Challenge

The application should provide an abstraction between two different email service providers. If one of the service provider goes down, the service can quickly failover to a different provider without affecting the service user. This example integrates with **Mailgun** and **SendGrid**.

This application requires **Java 8** and **Maven** to compile; and **Java 8** and **RabbitMQ 3** to run. The following section is a quick guide to install the requirements in MacOS and Amazon Linux.

##Requirements

###MacOS

####Java 8
```
brew tap caskroom/versions
brew cask install java8
```

####Maven
```
brew install maven
```

####RabbitMQ 3
```
brew install rabbitmq
brew services start rabbitmq
```

###Amazon Linux

###Java 8
```
sudo yum install java-1.8.0-openjdk.x86_64
```

####Maven
```
sudo yum install maven
```

####RabbitMQ 3

Erlang Dependecy:

```
wget http://dl.fedoraproject.org/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm
wget http://rpms.famillecollet.com/enterprise/remi-release-6.rpm
sudo rpm -Uvh remi-release-6*.rpm epel-release-6*.rpm
sudo yum install -y erlang
```

RabbitMQ:

```
wget http://www.rabbitmq.com/releases/rabbitmq-server/v3.2.2/rabbitmq-server-3.2.2-1.noarch.rpm
sudo rpm --import http://www.rabbitmq.com/rabbitmq-signing-key-public.asc
sudo yum install rabbitmq-server-3.2.2-1.noarch.rpm
sudo chkconfig rabbitmq-server on
sudo service rabbitmq-server start
```

##Compiling
Make sure to define the properties in *application.yml* under `application`.

```
https://github.com/kennethohuang/sm-challenge.git
cd sm-challenge/email
# define properties:
vi src/main/resources/application.yml
mvn clean install
```

##Running

```
java -jar sm-challenge.jar
```

##Testing
FROM is required, TO list requires at least one entry. CC and BCC lists are optional. SUBJECT and MESSAGE are also optional. Email NAME is optional.

```
curl --request POST \
	--url http://localhost:8080/email/messages \
	--header 'Content-Type: application/json' \
	--data '[{"from":{"name":"Jack","email":"jack@company.com"},"to":[{"name":"Rose","email":"rose@user.com"},{"name":"Andy","email":"andy@user.com"}],"cc":[{"name":"Chau","email":"chau@user.com"},{"name":"Wei","email":"wei@user.com"}],"bcc":[{"name":"Anil","email":"anil@user.com"},{"name":"Sumit","email":"sumit@user.com"}],"subject": "Hello World","message": "Today is cruel. Tomorrow is crueller. And the day after tomorrow is beautiful. -Jack Ma"}]'
```