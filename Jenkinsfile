pipeline {
    agent any
    
    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chromium', 'firefox', 'webkit'],
            description: 'Select browser for test execution'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['test', 'staging', 'production'],
            description: 'Select environment for testing'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run tests in headless mode'
        )
        choice(
            name: 'TEST_GROUPS',
            choices: ['smoke', 'regression', 'e2e', 'negative', 'all'],
            description: 'Select test groups to execute'
        )
    }
    
    environment {
        MAVEN_HOME = tool 'Maven-3.8.6'
        JAVA_HOME = tool 'JDK-11'
        BROWSER = "${params.BROWSER}"
        ENV = "${params.ENVIRONMENT}"
        HEADLESS = "${params.HEADLESS}"
        TEST_GROUPS = "${params.TEST_GROUPS}"
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo '=== Checking out source code ==='
                    checkout scm
                }
            }
        }
        
        stage('Clean Workspace') {
            steps {
                script {
                    echo '=== Cleaning workspace ==='
                    cleanWs()
                    sh 'rm -rf test-output/'
                    sh 'rm -rf target/'
                }
            }
        }
        
        stage('Setup Environment') {
            steps {
                script {
                    echo '=== Setting up test environment ==='
                    echo "Browser: ${env.BROWSER}"
                    echo "Environment: ${env.ENV}"
                    echo "Headless: ${env.HEADLESS}"
                    echo "Test Groups: ${env.TEST_GROUPS}"
                    
                    // Create necessary directories
                    sh 'mkdir -p test-output/screenshots'
                    sh 'mkdir -p test-output/traces'
                    sh 'mkdir -p test-output/reports'
                    
                    // Set Java version
                    sh 'echo "JAVA_HOME=${JAVA_HOME}"'
                    sh 'java -version'
                    sh 'mvn -version'
                }
            }
        }
        
        stage('Install Dependencies') {
            steps {
                script {
                    echo '=== Installing Maven dependencies ==='
                    withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin"]) {
                        sh 'mvn clean install -DskipTests'
                    }
                }
            }
        }
        
        stage('Install Playwright Browsers') {
            steps {
                script {
                    echo '=== Installing Playwright browsers ==='
                    withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin"]) {
                        sh 'mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install ${BROWSER}"'
                    }
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    echo '=== Running automation tests ==='
                    
                    def testGroups = env.TEST_GROUPS
                    def mavenCommand = "mvn test"
                    
                    // Build Maven command based on test groups
                    if (testGroups == 'all') {
                        mavenCommand += " -Dgroups=\"smoke,regression,e2e,negative\""
                    } else {
                        mavenCommand += " -Dgroups=\"${testGroups}\""
                    }
                    
                    // Add system properties
                    mavenCommand += " -Dbrowser=${env.BROWSER}"
                    mavenCommand += " -Dheadless=${env.HEADLESS}"
                    mavenCommand += " -Denv=${env.ENV}"
                    
                    echo "Maven Command: ${mavenCommand}"
                    
                    withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin"]) {
                        try {
                            sh mavenCommand
                        } catch (Exception e) {
                            echo "Tests failed but continuing to archive results..."
                            currentBuild.result = 'UNSTABLE'
                        }
                    }
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                script {
                    echo '=== Generating test reports ==='
                    
                    // Generate surefire reports
                    sh 'mvn surefire-report:report'
                    
                    // Create summary report
                    sh '''
                        echo "=== Test Execution Summary ===" > test-output/execution-summary.txt
                        echo "Build Number: ${BUILD_NUMBER}" >> test-output/execution-summary.txt
                        echo "Build Date: $(date)" >> test-output/execution-summary.txt
                        echo "Browser: ${BROWSER}" >> test-output/execution-summary.txt
                        echo "Environment: ${ENV}" >> test-output/execution-summary.txt
                        echo "Test Groups: ${TEST_GROUPS}" >> test-output/execution-summary.txt
                        echo "" >> test-output/execution-summary.txt
                        
                        if [ -f target/surefire-reports/TEST-com.cepphire.tests.CeppHireEndToEndTest.xml ]; then
                            echo "Test Results:" >> test-output/execution-summary.txt
                            grep -E "(tests|failures|errors|skipped)" target/surefire-reports/TEST-*.xml | head -5 >> test-output/execution-summary.txt
                        fi
                    '''
                }
            }
        }
    }
    
    post {
        always {
            script {
                echo '=== Post-build actions ==='
                
                // Archive test results
                archiveArtifacts artifacts: 'test-output/**/*', 
                                 allowEmptyArchive: true, 
                                 fingerprint: true
                
                // Archive Maven surefire reports
                archiveArtifacts artifacts: 'target/surefire-reports/**/*', 
                                 allowEmptyArchive: true, 
                                 fingerprint: true
                
                // Archive Maven reports
                archiveArtifacts artifacts: 'target/site/**/*', 
                                 allowEmptyArchive: true, 
                                 fingerprint: true
                
                // Publish HTML reports
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'test-output',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'Extent Reports',
                    reportTitles: 'CepHire AI Test Report'
                ])
                
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site',
                    reportFiles: 'surefire-report.html',
                    reportName: 'Surefire Reports',
                    reportTitles: 'Maven Surefire Test Report'
                ])
                
                // Publish JUnit test results
                try {
                    junit 'target/surefire-reports/**/*.xml'
                } catch (Exception e) {
                    echo "No JUnit test results found or parsing failed"
                }
                
                // Display execution summary
                sh 'cat test-output/execution-summary.txt || echo "No execution summary found"'
            }
        }
        
        success {
            script {
                echo '=== Build SUCCESS ==='
                echo 'All tests passed successfully!'
                
                // Send success notification (if configured)
                // emailext (
                //     subject: "✅ Test Success: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                //     body: "All tests passed successfully. View the report: ${env.BUILD_URL}Extent_Reports/",
                //     to: "${env.CHANGE_AUTHOR_EMAIL}"
                // )
            }
        }
        
        failure {
            script {
                echo '=== Build FAILURE ==='
                echo 'Tests failed! Please check the reports for details.'
                
                // Analyze test failures
                sh '''
                    echo "=== Failure Analysis ===" > test-output/failure-analysis.txt
                    echo "Build Number: ${BUILD_NUMBER}" >> test-output/failure-analysis.txt
                    echo "Failure Date: $(date)" >> test-output/failure-analysis.txt
                    echo "" >> test-output/failure-analysis.txt
                    
                    if [ -d target/surefire-reports ]; then
                        echo "Failed Tests:" >> test-output/failure-analysis.txt
                        grep -l "failures=\"[1-9]" target/surefire-reports/*.xml | while read file; do
                            echo "File: $(basename $file)" >> test-output/failure-analysis.txt
                            grep -E "(failure|error)" "$file" | head -3 >> test-output/failure-analysis.txt
                            echo "" >> test-output/failure-analysis.txt
                        done
                    fi
                '''
                
                // Archive failure analysis
                archiveArtifacts artifacts: 'test-output/failure-analysis.txt', 
                                 allowEmptyArchive: true
                
                // Send failure notification (if configured)
                // emailext (
                //     subject: "❌ Test Failure: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                //     body: "Tests failed. Please check the report: ${env.BUILD_URL}Extent_Reports/",
                //     to: "${env.CHANGE_AUTHOR_EMAIL}"
                // )
            }
        }
        
        unstable {
            script {
                echo '=== Build UNSTABLE ==='
                echo 'Some tests failed but build marked as unstable.'
            }
        }
        
        cleanup {
            script {
                echo '=== Cleanup ==='
                
                // Clean up temporary files
                sh 'rm -f *.tmp || true'
                sh 'rm -f *.log || true'
                
                // Compress large artifacts to save space
                sh '''
                    if [ -d test-output/traces ]; then
                        tar -czf test-output/traces-${BUILD_NUMBER}.tar.gz test-output/traces/
                        rm -rf test-output/traces/
                    fi
                    
                    if [ -d test-output/screenshots ]; then
                        tar -czf test-output/screenshots-${BUILD_NUMBER}.tar.gz test-output/screenshots/
                        rm -rf test-output/screenshots/
                    fi
                '''
                
                echo 'Cleanup completed.'
            }
        }
    }
}
