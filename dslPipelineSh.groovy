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
        sh ' echo $dockerhub > /tmp/dockerhub'
        sh 'echo $dockerhub_USR > /tmp/usr'
        sh 'echo $dockerhub_PSW > /tmp/psw'
				sh 'echo $dockerhub_PSW | docker login -u $dockerhub_USR --password-stdin'
			}
		}
    
    stage('Building image') {
      steps{
        script {
          sh 'cd flask'
          dockerImage = docker.build "flask" "$dockerhub_USR/bflask" 
          sh 'cd -'
        }
      }
    }
    stage('Deploy Image') {
      steps{
        script {
          docker.withRegistry( '', dockerhub ) {
            dockerImage.push("1.0")
            dockerImage.push('latest')
          }
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
