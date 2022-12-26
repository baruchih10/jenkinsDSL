// groovy script to run 3 jobs:
// flaskImageBuild
// njinxImageBuild
// executeEnvironments


// Declare variables for the job
def jobName = "my-pipeline-job"
def gitUrl = "https://github.com/user/repo.git"


import hudson.model.*
import jenkins.model.*

def createJob(name, script) {
  def instance = Jenkins.getInstance()
  def job = instance.createProject(PipelineJob, name)
  job.definition = new CpsFlowDefinition(script, true)
  job.save()
}

createJob("flaskImageBuild", """
pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
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

