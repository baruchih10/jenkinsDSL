pipeline {
  agent any
  stages {
    stage('Build and push flask') {
      steps {
        script {
          // Build and push the first Docker image
          sh 'docker build -t bflask -f flask/Dockerfile'
          sh 'docker tag bflask:latest myusername/bflask:1.0'
          sh 'docker push myusername/bflask:1.0'
        }
      }
    }
    stage('Build and push nginx') {
      dependsOn 'Build and push image 1'
      steps {
        script {
          // Build and push the second Docker image
          sh 'docker build -t bnginx -f nginx/Dockerfile '
          sh 'docker tag bnginx:latest myusername/bnginx:1.0'
          sh 'docker push myusername/bnginx:1.0'
        }
      }
    }
  }
}
