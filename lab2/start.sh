mvn package &&
hadoop fs -rm -r -f output &&
export HADOOP_CLASSPATH=target/lab2-1.0-SNAPSHOT.jar &&
hadoop ru.bmstu.lab2.AirportJoinApp L_AIRPORT_ID.csv 664600583_T_ONTIME_sample.csv output &&
rm -rf output &&
hadoop fs -copyToLocal output
