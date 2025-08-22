pipeline {
    agent any

    tools {
        // Automatically install and configure Maven and JDK
        maven 'Maven 3.9.11' // Match this to your configured Maven tool in Jenkins
        jdk 'Java 8'       // Match this to your configured JDK tool in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                // Get the code from your repository
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                script {
                    // This command executes all tests, including your accessibility tests
                    sh 'mvn clean test'
                }
            }
        }
    }
    post {
        // These steps run after the main build and test stages
        always {
            echo 'Archiving Surefire and Axe-core reports...'
            // Archive the JUnit test results
            junit 'target/surefire-reports/*.xml'
            // Archive the custom HTML and JSON reports
            archiveArtifacts artifacts: 'target/a11y-html-reports/*.html, target/a11y-json-reports/*.json', fingerprint: true
            echo 'Reports archived.'
        }
    }
}