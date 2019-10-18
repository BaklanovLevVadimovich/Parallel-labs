package ru.bmstu.lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class SparkApp {

    private final int ORIGIN_AIRPORT_INDEX = 
    private final int DEST_AIRPORT_INDEX =
    private final int DELAY_INDEX =
    private final int CANCELLED_INDEX =

    public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> flightsLines = sc.textFile("664600583_T_ONTIME_sample.csv");
        JavaRDD<String> airportsLines = sc.textFile("L_AIRPORT_ID.csv");

        JavaPairRDD<Integer, String> airportsData =
                airportsLines.filter(s -> !s.contains("Code,Description"))
                .mapToPair(s -> {
                    s = s.replace("\"", "");
                    String[] fields = s.split(",", 2);
                    int id = Integer.parseInt(fields[0]);
                    String name = fields[1];
                    return new Tuple2<>(id, name);
                });

        JavaPairRDD<Tuple2<Integer, Integer>, Flight> flightsData =
                flightsLines.filter(s -> !s.contains("\"YEAR\""))
                .mapToPair(s -> {
                    s = s.replace("\"", "");
                    String[] fields = s.split(",");

                });
    }
}
