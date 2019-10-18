package ru.bmstu.lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Int;
import scala.Tuple2;

public class SparkApp {

    private static final int ORIGIN_AIRPORT_INDEX = 11;
    private static final int DEST_AIRPORT_INDEX = 14;
    private static final int DELAY_INDEX = 18;
    private static final int CANCELLED_INDEX = 19;

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
                    int originAirportId = Integer.parseInt(fields[ORIGIN_AIRPORT_INDEX]);
                    int destAirportId = Integer.parseInt(fields[DEST_AIRPORT_INDEX]);
                    float delay = Float.parseFloat(fields[DELAY_INDEX]);
                    float cancelled = Float.parseFloat(fields[CANCELLED_INDEX]);
                    boolean isCancelled = cancelled == 1f;
                    return new Tuple2<>(new Tuple2<>(originAirportId, destAirportId), new Flight(originAirportId, destAirportId, isCancelled, delay));
                });

        JavaPairRDD<Tuple2<Integer, Integer>, FlightStat> flightStats =
                flightsData.combineByKey(
                        p -> new FlightStat(1, p.getDelay() == 0f ? 0 : 1, p.isCancelled() ? 1 : 0, p.getDelay()),
                        
                )
    }
}
