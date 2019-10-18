mvn package &&
hadoop fs -rm -r -f output &&
spark-submit --class ru.bmstu.lab3.SparkApp --master yarn-client --num-executors 3  target/lab-1.0-SNAPSHOT.jar
rm -rf output &&
hadoop fs -copyToLocal output
