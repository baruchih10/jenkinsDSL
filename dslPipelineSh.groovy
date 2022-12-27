pipeline {
  agent any
  stages {
    stage('Build and push image 1') {
      steps {
        script {
          // Build and push the first Docker image
          sh 'docker build -t image1 .'
          sh 'docker tag image1:latest myusername/image1:1.0'
          sh 'docker push myusername/image1:1.0'
        }
      }
    }
    stage('Build and push image 2') {
      dependsOn 'Build and push image 1'
      steps {
        script {
          // Build and push the second Docker image
          sh 'docker build -t image2 .'
          sh 'docker tag image2:latest myusername/image2:1.0'
          sh 'docker push myusername/image2:1.0'
        }
      }
    }
    stage('Build and push image 3') {
      dependsOn 'Build and push image 2'
      steps {
        script {
          // Build and push the third Docker image
          sh 'docker build -t image3 .'
          sh 'docker tag image3:latest myusername/image3:1.0'
          sh 'docker push myusername/image3:1.0'
        }
      }
    }
  }
}
