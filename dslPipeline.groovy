#!groovy

import hudson.model.*
import jenkins.model.*


import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

import hudson.model.FreeStyleProject
import hudson.tasks.ArtifactArchiver

// groovy script to run 3 jobs:
// flaskImageBuild
// njinxImageBuild
// executeEnvironments


// Declare variables for the job
def gitUrl = "https://github.com/user/repo.git"
def dockerhub_PWD = null
def dockerhub_USR = null
def verificationUrl = 'http://localhost/containers'
def stringClass = '$class'
def stringUsername = '$USERNAME'


def createAndRunJob(name, script) {
  def instance = Jenkins.getInstance()
  def job = instance.getItem(name)
  
  if ( job == null) {
    job = instance.createProject(WorkflowJob, name)
  }

  job.definition = new CpsFlowDefinition(script, true)
  job.save()
  build = job.scheduleBuild()
  println "${name} invoked"
}

createAndRunJob("flaskImage", """
pipeline {
  agent any
  environment {
    dockerhub = credentials('dockerhub')
  }
  stages {
    stage('Checkout code from Git repository') {
      steps {
        git branch: 'main', credentialsId: '70da42b3-4632-4314-bcf5-522c5866760d', url: 'https://github.com/BaruchiHalamish20/jenkinsDSL'
      }
    }
    
    stage('DockerHub Build and push') {
			steps {
        script {
          withCredentials([[$stringClass: 'UsernamePasswordMultiBinding', credentialsId: 'dockerhubc', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]){
            dockerImageFlask = docker.build("$stringUsername/bflask:latest", "./flask")
            dockerImageFlask.push()
          }
        }
        
			}
		}
     
  }
}
""")


createAndRunJob("nginxImage", """
pipeline {
  agent any
  environment {
    dockerhub = credentials('dockerhub')
  }
  stages {
    stage('Checkout code from Git repository') {
      steps {
        git branch: 'main', credentialsId: '70da42b3-4632-4314-bcf5-522c5866760d', url: 'https://github.com/BaruchiHalamish20/jenkinsDSL'
      }
    }
    
    stage('DockerHub Build and push') {
			steps {
        script {
          withCredentials([[$stringClass: 'UsernamePasswordMultiBinding', credentialsId: 'dockerhubc', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]){
            dockerImageNginx = docker.build("$stringUsername/bnginx:latest", "./nginx")
            dockerImageNginx.push()
          }
        }
			}
		}
     
  }
}
""")


createAndRunJob("jenkinsDslRunAndVerify", """
pipeline {
  agent any

  stages {
    stage('Checkout code from Git repository') {
      steps {
        git branch: 'main', credentialsId: '70da42b3-4632-4314-bcf5-522c5866760d', url: 'https://github.com/BaruchiHalamish20/jenkinsDSL'
      }
    }  
    stage('Run docker-compose') {
      steps {
        sh "docker-compose up -d"
        sh "date" 
        sh "echo 'docker-compose'"
      }
    }
    stage('Verification') {
      dependsOn 'Run docker-compose'
      steps {
        script {
          sh "date" 
          sh "echo 'verification'"
          def response = sh(script: "curl ${verificationUrl} | grep -q '404 Not Found' && echo '404' || echo '1'", returnStdout: true)
          if (response == "404") {
            println 'Failure - ${verificationUrl} not Found'
            exit 404
          } else {
            println 'Success - ${verificationUrl} Found'
          }
        }
      }
    }
  }
}

""")

