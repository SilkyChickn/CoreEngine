javadoc:
	javadoc -d docs -sourcepath src/main/java -subpackages de.coreengine

install:
	mvn clean install -D'gpg.skip' -D'arguments=-Dgpg.skip'
