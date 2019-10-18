package ru.bmstu.lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Int;
import scala.Tuple2;

import java.util.Map;

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
                    float delay;
                    if ((fields[DELAY_INDEX].equals(""))) delay = 0;
                    else delay = Float.parseFloat(fields[DELAY_INDEX]);
                    float cancelled = Float.parseFloat(fields[CANCELLED_INDEX]);
                    boolean isCancelled = cancelled == 1f;
                    return new Tuple2<>(new Tuple2<>(originAirportId, destAirportId), new Flight(isCancelled, delay));
                });

        JavaPairRDD<Tuple2<Integer, Integer>, String> flightStats =
                flightsData.combineByKey(
                        p -> new FlightStat(1, p.getDelay() == 0f ? 0 : 1, p.isCancelled() ? 1 : 0, p.getDelay()),
                        (flightStat, p) -> FlightStat.addValue(flightStat, p.getDelay() != 0, p.isCancelled(), p.getDelay()),
                        FlightStat::add)
                .mapToPair(p -> new Tuple2<>(p._1, p._2.toString()));

        Map<Integer, String> airportsMap = airportsData.collectAsMap();

        final Broadcast<Map<Integer, String>> airportsBroadcasted = sc.broadcast(airportsMap);

        JavaRDD<String> result = flightStats.map(
                s -> {
                    Map<Integer, String> airportIdToNameMap = airportsBroadcasted.value();
                    Tuple2<Integer, Integer> idPair = s._1;
                    String flightStatStr = s._2;
                    return "From " + airportIdToNameMap.get(idPair._1) + " To " + airportIdToNameMap.get(idPair._2) + " Stat: "+ flightStatStr;
                });
        result.saveAsTextFile("hdfs://localhost:9000/user/lev/output");
    }
}
