  #!/bin/bash
  export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
  export PATH=$JAVA_HOME/bin:$PATH
  ./mvnw clean package -DskipTests
