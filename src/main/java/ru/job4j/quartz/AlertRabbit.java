package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    private static final Logger LOG = LoggerFactory.getLogger(AlertRabbit.class);
    private static int period = 0;

    public static void main(String[] args) {
        readProperties("./src/main/resources/rabbit.properties");
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(period)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        private static Connection conn;

        public static void init() {
            try (InputStream in = Rabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
                Properties config = new Properties();
                config.load(in);
                Class.forName(config.getProperty("driver-class-name"));
                conn = DriverManager.getConnection(
                        config.getProperty("url"),
                        config.getProperty("username"),
                        config.getProperty("password")
                );

            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            init();
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            store.add(System.currentTimeMillis());

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try (PreparedStatement st = conn.prepareStatement("insert into rabbits(rabbit, data) values(?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, String.valueOf(hashCode()));
                st.setTimestamp(2, Timestamp.valueOf(sdf.format(timestamp)));
                st.executeUpdate();
                ResultSet generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    System.out.println(generatedKeys.getString("rabbit") + " " + generatedKeys.getTimestamp("data"));
                }
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
    public static void readProperties(String path) {
        try (BufferedReader in = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] splitValues = line.trim().split("=");
                if (splitValues[0].equals("rabbit.interval") && splitValues[1] != null) {
                    period = Integer.parseInt(splitValues[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}