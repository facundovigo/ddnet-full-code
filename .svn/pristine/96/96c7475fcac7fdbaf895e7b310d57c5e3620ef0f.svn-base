#! /bin/bash

# -------------------------------------------------------------------------------------------------------------------------------------------------------
#
# NOTAS:
# 		- correr ./get-sources.sh
#
# 		- modificar en los pom.xml las referencias a dcm4che-core, dcm4che-soundex y demas de "2.0.28-SNAPSHOT" a "2.0.28"
#                 TIP: grep -rl '2.0.28-SNAPSHOT' ./ | xargs sed -i 's/2.0.28-SNAPSHOT/2.0.28/g'
#
#		- modificar en .../dcm4jboss-build/build.xml     <property name="dcm4che2-version" value="2.0.27"/>   a  ...value="2.0.28" ...
#
#               - actualizar "libclib_jiio.so" en .../dcm4che/dcm4che14/bin con el corresponda segun el SO, sino no se verán las imágenes DICOM!
#
# -------------------------------------------------------------------------------------------------------------------------------------------------------


export JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64
export ANT_HOME=/usr/share/ant
export M2_HOME=/usr/local/apache-maven-3.2.1
export M2=$M2_HOME/bin
export PATH=/usr/lib/jvm/java-6-openjdk-amd64/bin:$M2:$PATH
export DB=psql
export SRC_ROOT=/home/rodrigo/all/projects/dev/diagnosticodigital/dcm4che

cd $SRC_ROOT/dcm4che14
cp $SRC_ROOT/support/jai_imageio-1_1/lib/libclib_jiio.so ./bin
ant

cd $SRC_ROOT/dcm4chee-arr
mvn -DskipTests install -Ddb=$DB

cd $SRC_ROOT/dcm4chee-audit 
mvn -DskipTests install 

cd $SRC_ROOT/dcm4chee-docstore
mvn -DskipTests install

cd $SRC_ROOT/dcm4chee-xds-infoset
mvn -DskipTests install

cd $SRC_ROOT/dcm4chee-web
mvn -e -Pall -Ddb=$DB -DskipTests install

cp $SRC_ROOT/build.properties $SRC_ROOT/dcm4jboss-all
cd $SRC_ROOT/dcm4jboss-all/dcm4jboss-build
ant $DB-dist
cd ./target
rm -rf dcm4chee-2.18.0-SNAPSHOT-psql
unzip -x dcm4chee-2.18.0-SNAPSHOT-psql.zip > /dev/null
cd dcm4chee-2.18.0-SNAPSHOT-psql/bin
./install_jboss.sh /home/rodrigo/all/projects/dev/stuff/java/opt/jboss-4.2.3.GA

