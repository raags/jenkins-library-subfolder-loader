#!groovy

def call(String url, String credId, String libraryPath, String masterNode = 'master') {
	
	def scm = [
		$class: 'GitSCM',
		branches: [[name: "master" ]],
		userRemoteConfigs: [[
				credentialsId: credId,
				url: url]]
		]

	node(masterNode) {
		echo "Loading shared library"
		try {
 			checkout scm
 		}catch(Exception e){
			deleteDir()
 			checkout scm
 		}

		def repoPath = sh(returnStdout: true, script: 'pwd').trim()

		library identifier: 'local-lib@master', 
				retriever: modernSCM([$class: 'GitSCMSource', remote: "$repoPath"]), 
				changelog: false

		echo "Done loading shared library"
	}
}
