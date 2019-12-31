package com.marsofandrew.social_network_cleaner;

import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.queries.wall.WallGetFilter;

import org.apache.commons.cli.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Main {

    public static void main(String[] args) {

        Options options = configureCliOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String username = cmd.getOptionValue("username");
        String password = cmd.getOptionValue("password");
        String startDateValue = cmd.getOptionValue("since", "1970-01-01-00-00-00");
        Instant startDate = parseDate(startDateValue);
        String endDateValue = cmd.getOptionValue("till");
        Instant endDate = null;
        if (endDateValue == null) {
            endDate = Instant.now();
        } else {
            endDate = parseDate(endDateValue);
        }

        UserActor actor = Authorization.authenticate(username, password);
        if (actor == null) {
            System.out.println("Login or password are incorrect");
            return;
        }
        Cleaner cleaner = new Cleaner(actor);

        long startTime = Date.from(startDate).getTime() / 1000;
        long endTime = Date.from(endDate).getTime() / 1000;

        if (cleaner.cleanWall(entry -> entry.getValue() >= startTime && entry.getValue() <= endTime, WallGetFilter.ALL)){
            System.out.println("Posts are successfully deleted");
        } else {
            System.out.println("File to delete posts");
        }
    }

    private static Instant parseDate(String dateValue) {
        String[] parts = dateValue.split("-");
        List<Integer> timeParts = new ArrayList<>();
        Arrays.stream(parts).forEach(part -> timeParts.add(Integer.parseInt(part)));
        return Instant.parse(
                String.format("%s-%s-%sT%s:%s:%sZ", parts[0], parts[1], parts[2],
                              parts[3], parts[4], parts[5]));

    }

    private static Options configureCliOptions() {
        Options options = new Options();

        Option username = new Option("U", "username", true, "username for VK.com");
        username.setRequired(true);
        options.addOption(username);

        Option password = new Option("P", "password", true, "password for the username");
        password.setRequired(true);
        options.addOption(password);

        Option startDate = new Option("S", "since", true, "Date from which posts will be deleted. Format: " +
                "YYYY-MM-DD-hh-mm-ss (UTC time). Default: 1970-01-01-00-00-00");

        Option endDate = new Option("T", "till", true, "Date until which posts will be deleted. Format: " +
                "YYYY-MM-DD-hh-mm-ss (UTC time). Default: NOW");
        options.addOption(startDate);
        options.addOption(endDate);
        return options;
    }
}
