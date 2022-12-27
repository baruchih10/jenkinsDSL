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

def buildDockerImage(imageName, path) {
  sh 'docker build -t imageName path'
}

createAndRunJob("flaskImageBuild", """
pipeline {
  agent any
  stages {
    stage('Checkout code from Git repository') {
      steps {
        git branch: 'main', credentialsId: '70da42b3-4632-4314-bcf5-522c5866760d', url: 'https://github.com/BaruchiHalamish20/jenkinsDSL'
      }
    }
    stage('Build') {
      steps {
          echo 'Building... flaskImageBuild'
          ${buildDockerImage('bflask', './flask')}
          sh 'pwd'
          sh 'ls -l'
          sh 'docker build -t bflask ./flask'
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

