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
// def gitUrl = "https://github.com/user/repo.git"

def deleteJob(name) {
  def jenkins = Jenkins.getInstance()
  def item = jenkins.getItem(name)

  if (item != null) {
    jenkins.delete(item)
    println "Project $name deleted successfully!"
  } else {
    println "Error: Project $name does not exist."
  }
}

def createJob(name, script) {
  def instance = Jenkins.getInstance()
  def job = instance.createProject(WorkflowJob, name)
  job.definition = new CpsFlowDefinition(script, true)
  job.save()
}

deleteJob("flaskImageBuild")
deleteJob("njinxImageBuild")
deleteJob("executeEnvironments")

createJob("flaskImageBuild", """
pipeline {
  agent any
  stages {
    stage('Build') {
        echo 'Building... flaskImageBuild'
      }
    }
  }
}
""")

createJob("njinxImageBuild", """
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

createJob("executeEnvironments", """
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

