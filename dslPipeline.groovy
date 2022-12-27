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
def GIT_COMMIT_EMAIL = null


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

createAndRunJob("flaskImageBuild", """
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
    stage('Login') {
			steps {
        script {
          GIT_COMMIT_EMAIL = sh (
              script: 'git --no-pager show -s --format=\'%ae\'',
              returnStdout: true
          ).trim()
        echo "Git committer email: ${GIT_COMMIT_EMAIL}"
        }
        
			}
		}
    stage('Build') {
      steps {
          echo 'Building... flaskImageBuild'
          sh 'docker build -t bflask ./flask'
          sh 'docker tag bflask:latest bflask/bflask:1.0'
          sh 'docker push bflask/bflask:1.0'
      }
    }
  }
}
""")
// buildDockerImage(bflask ./flask)
// dockerImage = docker.build bflask ./flask
          // docker build -t bflask ./flask
          // docker.withRegistry( '', registryCredential ) {
          //   dockerImage.push("$BUILD_NUMBER")
          //    dockerImage.push('latest')
          // }


createAndRunJob("njinxImageBuild", """
pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        echo 'Building... njinxImageBuild'
      }
    }
  }
}
""")

createAndRunJob("executeEnvironments", """
pipeline {
  agent any
  stages {
    stage('Deploy') {
      steps {
        echo 'Deploying & running using ssh ...'
      }
    }
  }
}
""")

