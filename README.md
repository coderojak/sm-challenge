# SM Challenge

The objective of this application is to provide a resilient email API implemented as a microservice. It should provide an abstraction between two different email service providers. If one of the service provider goes down, the service can quickly failover to a different provider without affecting users. This example integrates with **Mailgun** and **SendGrid**.

## Requirements

This application requires **Java 8** and **Maven** to compile; and **Java 8** and **RabbitMQ 3** to run. The following section is a quick guide to install the requirements in MacOS and Amazon Linux.

### MacOS

#### Java 8
```
brew tap caskroom/versions
brew cask install java8
```

#### Maven
```
brew install maven
```

#### RabbitMQ 3
```
brew install rabbitmq
brew services start rabbitmq
```

### Amazon Linux

### Java 8
```
sudo yum install java-1.8.0-openjdk.x86_64
```

#### Maven
```
sudo yum install maven
```

#### RabbitMQ 3

Erlang Dependecy:

```
sudo amazon-linux-extras install epel
sudo yum install erlang
```

RabbitMQ:

```
wget http://www.rabbitmq.com/releases/rabbitmq-server/v3.6.15/rabbitmq-server-3.6.15-1.el7.noarch.rpm
sudo rpm --import http://www.rabbitmq.com/rabbitmq-signing-key-public.asc
sudo yum install rabbitmq-server-3.6.15-1.el7.noarch.rpm
sudo chkconfig rabbitmq-server on
sudo service rabbitmq-server start
```

## Compiling
Make sure to define the properties in *application.yml* under **application** section. It is left out blank intentionally in the repository.

```
https://github.com/kennethohuang/sm-challenge.git
cd sm-challenge/email
vi src/main/resources/application.yml
mvn clean install
```

## Running

Make sure that the RabbitMQ service is running and listening to port 5672 before proceeding.

Maven Spring Boot Run:

```
mvn spring-boot:run
```

Running Compiled JAR:

```
java -jar $(find target -name *.jar)
```

## Invoking
The **from** field is required. The **to** list requires at least one entry. The **cc** and **bcc** lists are optional. Either **subject** or **message** must be included. The **email** field is required but **name** is optional.

Minimal Invocation:

```
curl --request POST \
	--url http://localhost:8080/email/messages \
	--header 'Content-Type: application/vnd.email.v1+json' \
	--data '[{"from":{"email":"writer@sender.com"},"to":[{"email":"reader@receiver.com"}],"subject": "Hello World","message": "Awesome testing message text."}]'
```

Full JSON Payload Sample:

```
[
	{
		"from": { "name": "Mr. Burns", "email": "burns@mail.com" },
		"to": [ { "name": "Homer", "email": "homer@mail.com" }, { "name": "Marge", "email": "marge@mail.com" }],
		"cc": [ { "name": "Bart", "email": "bart@mail.com" }, { "name": "Lisa", "email": "lisa@mail.com" }],
		"bcc": [ { "name": "Maggie", "email": "maggie@mail.com" }, { "name": "Laddie", "email": "dog@mail.com" }],
		"subject": "The Trouble with Trillions",
		"message": "If it's a crime to love one's country, then I'm guilty. And if it's a crime to steal a trillion dollars from our government, and hand it over to communist Cuba, then I'm guilty of that too. And if it's a crime to bribe a jury, then so help me, I'll soon be guilty of that."
	}
]
```