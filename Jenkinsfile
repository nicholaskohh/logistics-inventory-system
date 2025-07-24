pipeline {
    agent any

    tools {
        maven 'Maven 3'   // Must match name in Jenkins Global Tool Configuration
        jdk 'jdk-21'      // Or whatever version name you installed in Jenkins
    }

    stages {
        stage('Build & Test') {
            steps {
                sh './mvnw clean verify'
            }
        }

        stage('Package') {
            steps {
                sh './mvnw package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }
    }
}
