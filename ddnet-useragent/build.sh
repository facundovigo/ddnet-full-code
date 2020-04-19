#! /bin/bash

mvn package assembly:single
cp ./target/ddnet-useragent-1.0-jar-with-dependencies.jar ./deployment/lib/ 
jarsigner -keystore ./keystore/ddnet.keystore -storepass sgh6015 ./deployment/lib/ddnet-useragent-1.0-jar-with-dependencies.jar ddnet
cp ./deployment/ddnet-useragent.jnlp ./deployment/ddnet-useragent-local.jnlp
sed -i.bak 's!codebase=".*"!codebase="."!i' ./deployment/ddnet-useragent-local.jnlp
javaws ./deployment/ddnet-useragent-local.jnlp

