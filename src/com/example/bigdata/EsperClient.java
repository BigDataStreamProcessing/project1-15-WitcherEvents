package com.example.bigdata;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import net.datafaker.Faker;
import net.datafaker.fileformats.Format;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class EsperClient {
    public static void main(String[] args) throws InterruptedException {
        int noOfRecordsPerSec;
        int howLongInSec;
        if (args.length < 2) {
            noOfRecordsPerSec = 2;
            howLongInSec = 5;
        } else {
            noOfRecordsPerSec = Integer.parseInt(args[0]);
            howLongInSec = Integer.parseInt(args[1]);
        }

        Configuration config = new Configuration();
        EPCompiled epCompiled = getEPCompiled(config);

        // Connect to the EPRuntime server and deploy the statement
        EPRuntime runtime = EPRuntimeProvider.getRuntime("http://localhost:port", config);
        EPDeployment deployment;
        try {
            deployment = runtime.getDeploymentService().deploy(epCompiled);
        }
        catch (EPDeployException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }

        EPStatement resultStatement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), "answer");

        // Add a listener to the statement to handle incoming events
        resultStatement.addListener( (newData, oldData, stmt, runTime) -> {
            for (EventBean eventBean : newData) {
                System.out.printf("R: %s%n", eventBean.getUnderlying());
            }
        });

        Faker faker = new Faker();

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + (1000L * howLongInSec)) {
            waitToEpoch();
            for (int i = 0; i < noOfRecordsPerSec; i++) {
//                Timestamp timestamp = faker.date().past(60, TimeUnit.SECONDS);
                Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                Timestamp timestamp = Timestamp.from(now);
                Timestamp past = Timestamp.from(now.minusSeconds(60));
                Timestamp timestampFromDate = Timestamp.from(
                        faker.date().between(past, timestamp)
                                .toInstant().truncatedTo(ChronoUnit.SECONDS));
                String record = Format.toJson()
                        .set("witcher", () -> faker.witcher().witcher())
                        .set("its", timestamp::toString)
                        .set("ets", timestampFromDate::toString)
                        .set("school", () -> faker.witcher().school())
                        .set("monster", () -> faker.witcher().monster())
                        .set("payment", () -> String.valueOf(faker.number().numberBetween(25, 251)))
                        .set("difficulty_level", QuestDifficulty::getQuestDifficulty)
                        .set("location", () -> faker.witcher().location())
                        .set("potion", () -> faker.witcher().potion())
                        .set("poisoning_lvl", () -> String.valueOf(faker.number().numberBetween(1, 4)))
                        .build().generate();
                runtime.getEventService().sendEventJson(record, "WitcherEvent");
            }
        }
    }

    private static EPCompiled getEPCompiled(Configuration config) {
        CompilerArguments compilerArgs = new CompilerArguments(config);

        // Compile the EPL statement
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        EPCompiled epCompiled;
        try {
            epCompiled = compiler.compile("""
                    @public @buseventtype create json schema WitcherEvent(witcher string, its string,
                                                                        ets string, school string,
                                                                        monster string, payment int,
                                                                        difficulty_level string, location string,
                                                                        potion string, poisoning_lvl int);
                    @name('answer') SELECT witcher, school, monster, payment, difficulty_level, location,
                                            potion, poisoning_lvl, its, ets
                    FROM WitcherEvent#ext_timed(java.sql.Timestamp.valueOf(its).getTime(), 3 sec);""", compilerArgs);
        }
        catch (EPCompileException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }
        return epCompiled;
    }

    static void waitToEpoch() throws InterruptedException {
        long millis = System.currentTimeMillis();
        Instant instant = Instant.ofEpochMilli(millis) ;
        Instant instantTrunc = instant.truncatedTo( ChronoUnit.SECONDS ) ;
        long millis2 = instantTrunc.toEpochMilli() ;
        TimeUnit.MILLISECONDS.sleep(millis2+1000-millis);
    }
}

