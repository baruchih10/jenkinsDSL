#!groovy

import hudson.model.*
import jenkins.model.*


import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

import hudson.model.FreeStyleProject
import hudson.tasks.ArtifactArchiver

import hudson.plugins.parameterizedtrigger.*

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


def createJob(name, script) {
  def instance = Jenkins.getInstance()
  def job = instance.getItem(name)
  
  if ( job == null) {
    job = instance.createProject(WorkflowJob, name)
  }

  job.definition = new CpsFlowDefinition(script, true)
  job.save()

  println "${name} invoked"
}

def runDependendJobs(){
  
  def upstreamProject1 = Hudson.instance.getItem("flaskImage")
  def upstreamProject2 = Hudson.instance.getItem("nginxImage")
  def downstreamProject = Hudson.instance.getItem("jenkinsDslRunAndVerify")


  if (upstreamProject1 != null && upstreamProject2 != null && downstreamProject != null) {
       // trigger builds for the upstream projects
    upstreamProject1.scheduleBuild(new Cause.UserIdCause())
    upstreamProject2.scheduleBuild(new Cause.UserIdCause())

    // wait for the upstream builds to complete
    def queue = Hudson.instance.queue
    def build1 = queue.getItem(upstreamProject1)
    def build2 = queue.getItem(upstreamProject2)
    while (build1.isBuilding() || build2.isBuilding()) {
        sleep(1000)
    }

    // check the build results for the upstream projects
    def build1Result = build1.getResult()
    def build2Result = build2.getResult()

    if (build1Result == Result.SUCCESS && build2Result == Result.SUCCESS) {
        // trigger the downstream build
        downstreamProject.scheduleBuild(new Cause.UpstreamCause(build1))
    }
  }
}


createJob("flaskImage", """
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


createJob("nginxImage", """
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

createJob("jenkinsDslRunAndVerify", """
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

runDependendJobs()