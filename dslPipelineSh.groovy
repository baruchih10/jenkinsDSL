pipeline {
  agent any
  environment {
    dockerhub = credentials('dockerhub')
  }
  
  stages {
    stage('Login') {
			steps {
				sh 'echo $dockerhub_PWD | docker login -u $dockerhub_USR --password-stdin'
			}
		}
    stage('Build and push flask') {
      steps {
        script {
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
          sh 'docker build -t bnginx -f ./nginx'
          sh 'docker tag bnginx:latest bnginx/bnginx:1.0'
          sh 'docker push bnginx/bnginx:1.0'
        }
      }
    }
  }
}
