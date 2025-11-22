# ----------------------------------------------------------------
# ETAPA 1: BUILD - Compila o código Java e cria o JAR
# ----------------------------------------------------------------
# Usamos uma imagem que já contém o JDK e o Maven
FROM registry.access.redhat.com/ubi9/openjdk-21:1.23 AS build


WORKDIR /usr/src/app

# Copia o pom.xml para o cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte
COPY src /usr/src/app/src

# Executa a compilação do Maven
RUN mvn package -DskipTests

# ----------------------------------------------------------------
# ETAPA 2: RUNTIME - Cria a imagem final leve para execução
# ----------------------------------------------------------------
FROM registry.access.redhat.com/ubi9/openjdk-21:1.23

ENV LANGUAGE='en_US:en'

#Copia TODO o conteúdo do 'quarkus-app'
# para o diretório de execução esperado pelo script (deployments/)
COPY --from=build /usr/src/app/target/quarkus-app /deployments/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
# Esta variável confirma o local onde o script deve procurar o JAR
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]