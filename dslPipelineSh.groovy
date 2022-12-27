pipeline {
  agent any
  stages {
    stage('Build and push flask') {
      steps {
        script {
          // Build and push the first Docker image
          sh 'whoami > /tmp/who'
          sh 'docker run hello-world'
          sh 'docker build -t bflask ./flask'
          sh 'docker tag bflask:latest bflask/bflask:1.0'
          sh 'docker push bflask/bflask:1.0'
        }
      }
    }
    stage('Build and push nginx') {
      steps {
        dependsOn 'Build and push flask'
        script {
          // Build and push the second Docker image
          sh 'docker build -t bnginx -f ./nginx'
          sh 'docker tag bnginx:latest bnginx/bnginx:1.0'
          sh 'docker push bnginx/bnginx:1.0'
        }
      }
    }
  }
}
