pipeline {
  agent any
  environment {
    dockerhub = credentials('dockerhub')
    registryCredential = 'dockerhub'
    dockerImage = ''
  }
  
  stages {
    stage('Login') {
			steps {
				sh 'echo $dockerhub_PSW | docker login -u $dockerhub_USR --password-stdin'
			}
		}
    
    stage('Building image') {
      steps{
        script {
          dockerImage = docker.build("$dockerhub_USR/bflask", "./flask")
          dockerImage.push()
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
