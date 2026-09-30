pipeline {
    agent any

    options {
        // Automatically stop the build if it takes longer than 15 minutes
        timeout(time: 15, unit: 'MINUTES')
        // Keep the last 10 builds to save disk space on the Jenkins controller
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        // Stage 1: Checkout source code from GitHub
        stage('Checkout') {
            steps {
                echo '============================================================'
                echo 'Stage 1: Checking out source code from Git repository'
                echo '============================================================'
                checkout scm
            }
        }

        // Stage 2: Compile the Java application using Maven
        stage('Build') {
            steps {
                echo '============================================================'
                echo 'Stage 2: Compiling Java source code with Maven'
                echo '============================================================'
                script {
                    if (isUnix()) {
                        sh 'mvn compile'
                    } else {
                        bat 'mvn compile'
                    }
                }
            }
        }

        // Stage 3: Execute automated tests and publish JUnit results
        stage('Test') {
            steps {
                echo '============================================================'
                echo 'Stage 3: Running automated unit and integration tests'
                echo '============================================================'
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
            post {
                always {
                    echo 'Publishing JUnit test results from Maven Surefire reports...'
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: false
                }
            }
        }

        // Stage 4: Package application into an executable Spring Boot JAR
        stage('Package') {
            steps {
                echo '============================================================'
                echo 'Stage 4: Packaging application into Spring Boot executable JAR'
                echo '============================================================'
                script {
                    // Tests were already executed and verified in Stage 3
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                    } else {
                        bat 'mvn package -DskipTests'
                    }
                }
            }
            post {
                success {
                    echo 'Archiving Spring Boot JAR artifact in Jenkins...'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: false
                }
            }
        }

        // Stage 5: Generate and archive a human-readable build summary
        stage('Build Summary') {
            steps {
                echo '============================================================'
                echo 'Stage 5: Generating Build Summary Report'
                echo '============================================================'
                script {
                    if (isUnix()) {
                        sh '''
                            echo "============================================================" > build_log.txt
                            echo "CampusCart - Jenkins CI Build Summary" >> build_log.txt
                            echo "============================================================" >> build_log.txt
                            echo "Project Name     : CampusCart - Online College Stationery Store" >> build_log.txt
                            echo "Build Number     : ${BUILD_NUMBER}" >> build_log.txt
                            echo "Job Name         : ${JOB_NAME}" >> build_log.txt
                            echo "Git Branch       : ${GIT_BRANCH}" >> build_log.txt
                            echo "Git Commit       : ${GIT_COMMIT}" >> build_log.txt
                            echo "Java Version     : $(java -version 2>&1 | head -n 1)" >> build_log.txt
                            echo "Maven Version    : $(mvn -version 2>&1 | head -n 1)" >> build_log.txt
                            echo "Test Execution   : PASSED (Surefire JUnit reports published)" >> build_log.txt
                            echo "Packaging Status : SUCCESS (target/campuscart-1.0.0.jar generated)" >> build_log.txt
                            echo "Pipeline Status  : SUCCESSFUL" >> build_log.txt
                            echo "Date             : $(date)" >> build_log.txt
                            echo "============================================================" >> build_log.txt
                            cat build_log.txt
                        '''
                    } else {
                        bat '''
                            @echo off
                            echo ============================================================ > build_log.txt
                            echo CampusCart - Jenkins CI Build Summary >> build_log.txt
                            echo ============================================================ >> build_log.txt
                            echo Project Name     : CampusCart - Online College Stationery Store >> build_log.txt
                            echo Build Number     : %BUILD_NUMBER% >> build_log.txt
                            echo Job Name         : %JOB_NAME% >> build_log.txt
                            echo Git Branch       : %GIT_BRANCH% >> build_log.txt
                            echo Git Commit       : %GIT_COMMIT% >> build_log.txt
                            for /f "delims=" %%i in ('java -version 2^>^&1') do (echo Java Version     : %%i >> build_log.txt & goto :next1)
                            :next1
                            for /f "delims=" %%i in ('mvn -version 2^>^&1') do (echo Maven Version    : %%i >> build_log.txt & goto :next2)
                            :next2
                            echo Test Execution   : PASSED (Surefire JUnit reports published) >> build_log.txt
                            echo Packaging Status : SUCCESS (target/campuscart-1.0.0.jar generated) >> build_log.txt
                            echo Pipeline Status  : SUCCESSFUL >> build_log.txt
                            echo Date             : %DATE% %TIME% >> build_log.txt
                            echo ============================================================ >> build_log.txt
                            type build_log.txt
                        '''
                    }
                }
                echo 'Archiving build_log.txt as a build artifact...'
                archiveArtifacts artifacts: 'build_log.txt', allowEmptyArchive: false
            }
        }
    }

    post {
        success {
            echo '============================================================'
            echo 'Jenkins CI Pipeline for CampusCart completed SUCCESSFULLY!'
            echo 'Artifacts and Test Reports are available on the build page.'
            echo '============================================================'
        }
        failure {
            echo '============================================================'
            echo 'Jenkins CI Pipeline for CampusCart FAILED!'
            echo 'Check the console logs and test reports above for details.'
            echo '============================================================'
        }
    }
}
