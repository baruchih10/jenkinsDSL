#!groovy

import hudson.model.*
import jenkins.model.*


import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
// import org.jenkinsci.plugins.workflow.job.PipelineJob
import org.jenkinsci.plugins.workflow.job.WorkflowJob

// import hudson.model.WorkflowJob;
import hudson.model.FreeStyleProject
import hudson.tasks.ArtifactArchiver

// groovy script to run 3 jobs:
// flaskImageBuild
// njinxImageBuild
// executeEnvironments


// Declare variables for the job
// def gitUrl = "https://github.com/user/repo.git"

// def createJob(name) {
//   def instance = Jenkins.getInstance()
//   def job = instance.createProject(FreeStyleProject, name)
//   job.addPublisher(new ArtifactArchiver("*.txt", "", false, false))
//   job.save()
// }


def createJob(name, script) {
  def instance = Jenkins.getInstance()
  def job = instance.createProject(WorkflowJob, name)
  job.definition = new CpsFlowDefinition(script, true)
  job.save()
}

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

