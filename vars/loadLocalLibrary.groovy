#!groovy

def call(String gitURL, String branch, String credId, String libraryPath, String masterNode = 'master') {

	def scm = [
		$class: 'GitSCM',
		branches: [[name: branch ]],
		userRemoteConfigs: [[
				credentialsId: credId,
				url: gitURL]]
		]

	node(masterNode) {
		echo "Loading shared library"
    checkout scm

		// Create new git repo inside library directory
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
