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
		echo "Loading local shared library"
		try {
 			checkout scm
 		}catch(Exception e){
			deleteDir()
 			checkout scm
 		}


		// Create new git repo inside jenkins subdirectory
		sh("""cd ./$libraryPath && \
				(rm -rf .git || true) && \
				git init && \
				git add --all && \
				git commit -m init
		""")
		def repoPath = sh(returnStdout: true, script: 'pwd').trim() + "/$libraryPath"

		library identifier: 'local-lib@master', 
				retriever: modernSCM([$class: 'GitSCMSource', remote: "$repoPath"]), 
				changelog: false

		echo "Done loading shared library"
	}
}
