pipeline {
    agent any

    tools {
        jdk 'JDK-21'
        maven 'Maven'
    }

    stages {

     stage('Checkout') {
                steps {
                    git branch: 'main',
                        url: 'https://github.com/ashishkumarsingh296/SpringBootfullcourseapp.git',
                        credentialsId: 'GITHUB-CREDS'
                }
            }
            stage('Build') {
                steps {
                    script {
                        dir('DemoRedisWithSpringBoot') {
                            bat './mvnw clean install'  // Adjust based on your project setup
                        }
                    }
                }
            }

//         stage('Checkout') {
//                 // Step 1: Checkout Latest Codde
//                         steps {
//                             git url: 'https://github.com/ashishkumarsingh296/SpringBootfullcourseapp.git', credentialsId: 'GITHUB-CREDS'
//                         }
//                     }
//         }

        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Test & Coverage') {
            steps {
                bat 'mvn test jacoco:report'
            }
        }

        stage('Publish Coverage Report') {
            steps {
                publishHTML(target: [
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Code Coverage Report'
                ])
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application...'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
