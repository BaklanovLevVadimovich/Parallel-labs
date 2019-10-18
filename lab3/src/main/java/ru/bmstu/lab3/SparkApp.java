package ru.bmstu.lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkApp {

    public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> flightsLines = sc.textFile("664600583_T_ONTIME_sample.csv");
        JavaRDD<String> airportsLines = sc.textFile("L_AIRPORT_ID.csv");

        JavaRDD<Integer, String> airportsData = airportsLines.mapToPair()
    }
}
