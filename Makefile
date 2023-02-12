javadoc:
	mvn javadoc:javadoc -D'reportOutputDirectory=.' -D'destDir=docs'

install:
	mvn clean install -D'gpg.skip' -D'arguments=-Dgpg.skip'
