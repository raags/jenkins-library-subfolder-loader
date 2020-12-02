#!groovy
import hudson.plugins.git.GitSCM

def call(GitSCM scm, String libraryPath, String masterNode = 'master') {
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
