# 메시 측정 플랫폼 센터 웹 어플리케이션.

## CentOS 7 개발 환경 설정

alternatives와 update-alternatives를 이용하여 java와 javac를 희망하는 jdk 버전으로 설정을 한다. 현 리포지토리를 JDK 12를 기준으로 진행한다.

```
yum install java-latest-openjdk-devel.x86_64 -y
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-12-openjdk-12.0.2.9-1.rolling.el7.x86_64/bin/java 1000
sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-12-openjdk-12.0.2.9-1.rolling.el7.x86_64/bin/javac 1000
alternatives --config java
alternatives --config javac
```

```Text
mvn clean package -DskipTests=true
java -jar target/webapp-0.0.1.jar
```

