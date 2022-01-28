FROM ubuntu:21.10
ENV SBT_VERSION=1.5.0

EXPOSE 8080
EXPOSE 9000
EXPOSE 9443

# Install jdk
RUN apt-get update -y
RUN apt-get install software-properties-common -y
RUN add-apt-repository ppa:openjdk-r/ppa -y
RUN apt-get update -y
RUN apt-get install default-jdk -y

# Install scala
RUN apt-get install scala -y

# Install sbt
RUN apt-get update -y
RUN apt-get install apt-transport-https curl gnupg -yqq -y
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
RUN chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
RUN apt-get update -y
RUN apt-get install sbt -y

# Play
WORKDIR '/app'
COPY play-with-logback ./
CMD ["sbt", "run"] 
