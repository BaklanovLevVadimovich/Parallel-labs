package ru.bmstu.lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.Map;

public class SparkApp {

    private static final int ORIGIN_AIRPORT_INDEX = 11;
    private static final int DEST_AIRPORT_INDEX = 14;
    private static final int DELAY_INDEX = 18;
    private static final int CANCELLED_INDEX = 19;

    private static  final String AIRPORTS_FILENAME = "L_AIRPORT_ID.csv";
    private static  final String FLIGHTS_FILENAME = "664600583_T_ONTIME_sample.csv";
    private static final String AIRPORTS_FIRST_LINE_TRIGGER = "Code,Description";
    private static final String FLIGHTS_FIRST_LINE_TRIGGER = "\"YEAR\"";
    private static final int ID_FIELD = 0;
    private static final int NAME_FIELD = 1;
    private static final int SPLIT_LIMIT = 2;
    private static final float FLIGHT_CANCELLED_FLAG = 1f;
    private static final float FLIGHT_NOT_CANCELLED_FLAG = 0f;
    private static final float FLIGHT_NOT_DELAYED_FLAG = 0f;
    private static final String COMMA = ",";
    private static final String QUOTE = "\"";
    private static final String EMPTY_STRING = "";

    public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> flightsLines = sc.textFile(FLIGHTS_FILENAME);
        JavaRDD<String> airportsLines = sc.textFile(AIRPORTS_FILENAME);

        JavaPairRDD<Integer, String> airportsData =
                airportsLines.filter(s -> !s.contains(AIRPORTS_FIRST_LINE_TRIGGER))
                .mapToPair(s -> {
                    s = removeQuotes(s);
                    String[] fields = s.split(COMMA, SPLIT_LIMIT);
                    int id = Integer.parseInt(fields[ID_FIELD]);
                    String name = fields[NAME_FIELD];
                    return new Tuple2<>(id, name);
                });

        JavaPairRDD<Tuple2<Integer, Integer>, Flight> flightsData =
                flightsLines.filter(s -> !s.contains(FLIGHTS_FIRST_LINE_TRIGGER))
                .mapToPair(s -> {
                    s = removeQuotes(s);
                    String[] fields = s.split(COMMA);
                    int originAirportId = Integer.parseInt(fields[ORIGIN_AIRPORT_INDEX]);
                    int destAirportId = Integer.parseInt(fields[DEST_AIRPORT_INDEX]);
                    float delay;
                    if ((fields[DELAY_INDEX].isEmpty())) {
                        delay = 0;
                    }  else {
                        delay = Float.parseFloat(fields[DELAY_INDEX]);
                    }
                    float cancelled = Float.parseFloat(fields[CANCELLED_INDEX]);
                    boolean isCancelled = cancelled == FLIGHT_CANCELLED_FLAG;
                    return new Tuple2<>(new Tuple2<>(originAirportId, destAirportId), new Flight(isCancelled, delay));
                });

        JavaPairRDD<Tuple2<Integer, Integer>, String> flightStats =
                flightsData.combineByKey(
                        p -> new FlightStat(1, p.getDelay() == FLIGHT_NOT_DELAYED_FLAG ? 0 : 1, p.isCancelled() ? 1 : 0, p.getDelay()),
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

    private static String removeQuotes(String line) {
        return line.replace(QUOTE, EMPTY_STRING);
    }
}
