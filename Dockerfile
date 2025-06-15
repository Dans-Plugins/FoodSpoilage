FROM ubuntu

# Install dependencies
RUN apt update
RUN DEBIAN_FRONTEND=noninteractive apt install -y wget git openjdk-21-jdk openjdk-21-jre

# Build server
WORKDIR /testmcserver-build
RUN wget -O BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
RUN git config --global --unset core.autocrlf || :
RUN java -jar BuildTools.jar --rev 1.21.4

# Copy plugin jar (built separately, not in this Dockerfile, this is to save time by avoiding rebuilding the plugin)
COPY ./build /testmcserver-build/FoodSpoilage/build

# Copy resources and make post-create.sh executable
COPY ./.testcontainer /resources
RUN chmod +x /resources/post-create.sh

# Run server
WORKDIR /testmcserver
EXPOSE 25565
ENTRYPOINT /resources/post-create.sh
