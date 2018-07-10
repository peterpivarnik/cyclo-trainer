package com.psw.cyclo;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;

public class CycloTrainer {

    private static final int BEEP_DURATION_SHORT = 500;
    private static final String BEEP_SOUND_LOCATION = "/beep-01a.wav";
    private static final int HOURS_FOR_EXERCISE = 1;

    public static void main(String[] attrs) {
        CycloTrainer cycloTrainer = new CycloTrainer();
        final Properties properties = cycloTrainer.loadProperties();
        cycloTrainer.startExercise(properties);
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        String filename = "application.properties";
        try (InputStream input = CycloTrainer.class.getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return properties;
            }
            properties.load(input);
            return properties;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void startExercise(Properties properties) {
        final int durationInitial = Integer.parseInt(properties.getProperty("beep.duration.initial"));
        final int durationExercise = Integer.parseInt(properties.getProperty("beep.duration.exercise"));
        final int durationRelax = Integer.parseInt(properties.getProperty("beep.duration.relax"));
        final LocalDateTime startTime = LocalDateTime.now();

        initialPhase();
        heatUpPhase(durationInitial);
        exercisingPhase(durationExercise, durationRelax, startTime);
        finishPhase();
    }

    private void initialPhase() {
        beep();
        sleep(Duration.ofMillis(BEEP_DURATION_SHORT));
        beep();
        sleep(Duration.ofMillis(BEEP_DURATION_SHORT));
        beep();
    }

    private void heatUpPhase(int durationInitial) {
        System.out.println("Start");
        sleep(Duration.ofMinutes(durationInitial));
        beep();
    }

    private void exercisingPhase(int durationExercise, int durationRelax, LocalDateTime startTime) {
        while (isLessThanHour(startTime)) {
            System.out.println("Go for it !!!");
            sleep(Duration.ofMillis(BEEP_DURATION_SHORT));
            beep();
            sleep(Duration.ofMinutes(durationExercise));
            beep();
            System.out.println("Relax :)");
            sleep(Duration.ofMinutes(durationRelax));
            beep();
        }
    }

    private void finishPhase() {
        System.out.println("Finish !!!");
        beep();
        sleep(Duration.ofMillis(BEEP_DURATION_SHORT));
        beep();
        sleep(Duration.ofMillis(BEEP_DURATION_SHORT));
        beep();
    }

    private boolean isLessThanHour(LocalDateTime startTime) {
        return Duration.between(startTime, LocalDateTime.now()).compareTo(Duration.ofHours(HOURS_FOR_EXERCISE)) < 0;
    }

    private void beep() {
        try {
            URL sounURL = this.getClass().getResource(BEEP_SOUND_LOCATION);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(sounURL);
            DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(inputStream);
            clip.start();
            sleep(Duration.ofMillis(BEEP_DURATION_SHORT));
            clip.stop();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    private void sleep(Duration timeToSleep) {
        try {
            Thread.sleep(timeToSleep.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

