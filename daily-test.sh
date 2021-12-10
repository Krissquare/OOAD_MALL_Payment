#|bin/bash
## 将文件结尾从CRLF改为LF，解决了cd 错误问题
workdir_oomall=/home/lxc/project/oomall
workdir_privilege=/home/lxc/project/privilegegateway
workdir_ooad201=/home/lxc/project/ooad201
testdir=/home/testPort
time=$(date "+%Y-%m-%d--%H:%M:%S")

echo '-------------------install annotaion--------------------------'
cd $workdir_privilege
git pull
cd $workdir_privilege/annotation
mvn clean install >> /home/console.log

echo '-------------------install core--------------------------'
cd $workdir_oomall
git pull
cd $workdir_oomall/core
mvn clean install >> /home/console.log

echo '-------------------initializing ooad database-------------------------'
cd $workdir_ooad201/sql
mysql -udbuser -p12345678 -D ooad < order-bash.sql


cd $workdir_ooad201
git pull
echo '-------------------building order--------------------------'
cd $workdir_ooad201/order
mvn clean test >> /home/console.log
mvn site:site >> /home/console.log
mkdir $testdir/$time
cp -rf $workdir_ooad201/order/target/site $testdir/$time/order/


echo '-------------------building transaction--------------------------'
cd $workdir_ooad201/transaction
mvn clean test >> /home/console.log
mvn site:site >> /home/console.log
mkdir $testdir/$time
cp -rf $workdir_ooad201/transaction/target/site $testdir/$time/transaction/


cp /home/console.log $testdir/$time/
rm /home/console.log
