pipeline {
  agent any
  environment {
    dockerhub = credentials('dockerhub')
    registryCredential = 'dockerhub'
    dockerImage = ''
    DOCKER='docker -H unix:///var/run/docker.sock'
  }
  
  stages {
    stage('Login') {
			steps {
				sh 'echo $dockerhub_PSW | docker login -u $dockerhub_USR --password-stdin'
			}
		}
    
    stage('Build and push  flas') {
      steps{
        script {
          dockerImage = docker.build("$dockerhub_USR/bflask", "./flask")
          dockerImage.push()
        }
      }
    }
    
    
   
    stage('Build and push nginx') {
      steps{
        script {
          dockerImage = docker.build("$dockerhub_USR/bnginx", "./nginx")
          dockerImage.push()
        }
      }
    }
  }
}
