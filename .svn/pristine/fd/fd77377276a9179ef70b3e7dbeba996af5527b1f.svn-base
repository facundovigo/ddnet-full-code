Instrucciones para preparar el JAR de DDNET UserAgent, listo para distribución:
-------------------------------------------------------------------------------

	cd ~/all/projects/dev/diagnosticodigital/stuff/svntropezon/diagnosticodigital/ddnet/src/ddnet-useragent 
	mvn package assembly:single
	cp ./target/ddnet-useragent-1.0-jar-with-dependencies.jar ./deployment/lib/ 
	jarsigner -keystore ./keystore/ddnet.keystore ./target/ddnet-useragent-1.0-jar-with-dependencies.jar ddnet

