pipeline {
    agent any

    tools {
        maven 'Maven 3'
        jdk 'jdk-21'
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
