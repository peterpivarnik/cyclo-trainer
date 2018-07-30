package com.psw.cyclo;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CycloTrainer {

    private static final int HOURS_FOR_EXERCISE = 1;
    private static final String BEEP_SOUND_LOCATION = "/beep-01a.wav";
    private static final int BEEP_DURATION_SHORT = 500;
    private static final int BEEP_DURATION_INITIAL = 15;
    private static final int BEEP_DURATION_EXERCISE = 2;
    private static final int BEEP_DURATION_RELAX = 3;
    private static final List<Integer> DEFAULT_INTERVALS = Arrays.asList(BEEP_DURATION_INITIAL, BEEP_DURATION_EXERCISE, BEEP_DURATION_RELAX);
    private static final String NUMBER_REGEX = "[0-9]+";

    public static void main(String[] attrs) {
        List<Integer> intervals = checkAttrs(attrs);
        CycloTrainer cycloTrainer = new CycloTrainer();
        cycloTrainer.startExercise(intervals);
    }

    private static List<Integer> checkAttrs(String[] attrs) {
        if (attrs.length != 3) {
            return DEFAULT_INTERVALS;
        }
        if (isNumber(attrs)) {
            return Stream.of(attrs)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        } else {
            return DEFAULT_INTERVALS;
        }
    }

    private static boolean isNumber(String[] attrs) {
        return Stream.of(attrs).allMatch(attr -> attr.matches(NUMBER_REGEX));
    }


    private void startExercise(List<Integer> intervals) {
        final LocalDateTime startTime = LocalDateTime.now();

        initialPhase();
        heatUpPhase(intervals.get(0));
        exercisingPhase(intervals.get(1), intervals.get(2), startTime);
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
        sleep(Duration.ofMillis(BEEP_DURATION_SHORT));
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

