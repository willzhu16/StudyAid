//Hello! This is a personal project designed to streamline study sessions. When run, a relaxing youtube video plays with study music. Along with that,
//in accordance to Jun Yuh's study system, an alarm will go off every 3-5 minutes where you should stop and rest for 10 seconds. It also features the
//extended pomodoro technique in which after 45 minutes, it will notify you to take a 15 minute break. 

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javazoom.jl.player.Player;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

// MP3Player class handles MP3 playing.
// Code taken from https://www.delftstack.com/howto/java/java-play-mp3/
class MP3Player {
    private final String mp3FileToPlay;
    private Player jlPlayer;

    public MP3Player(String mp3FileToPlay) {
        this.mp3FileToPlay = mp3FileToPlay;
    }

    public void play() {
        try {
            FileInputStream fileInputStream = new FileInputStream(mp3FileToPlay);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            jlPlayer = new Player(bufferedInputStream);
        } catch (Exception e) {
            System.out.println("Problem playing mp3 file " + mp3FileToPlay);
            System.out.println(e.getMessage());
        }

        new Thread(() -> {
            try {
                jlPlayer.play();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    public void stop() {
        if (jlPlayer != null) {
            jlPlayer.close();
        }
    }
}

//Study class manages the study-break cycle and alarm playing
class Study {
    private final Random rand = new Random();
    private final String fileName = "Alarm1.mp3"; // Ensure this file exists
    private final MP3Player mp3Player = new MP3Player(fileName);
    private final Timer timer = new Timer("AlarmTimer");
    private boolean onBreak = false;
    private final int studyTime = 45; // Study period 
    private final int breakTime = 15; // Break period 

    public void startSession() {
        // Run the study cycle in a separate thread to avoid blocking the JavaFX thread
        new Thread(this::scheduleStudyCycle).start();
    }

    private void scheduleStudyCycle() {
        // Initial alarm schedule between 3-5 seconds
        scheduleRandomAlarm(3, 5);

        // Schedule the 45-second study period before a break
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("45-second interval reached, starting 15-second break...");
                onBreak = true;
                stopAlarmsDuringBreak();
            }
        }, studyTime * 60 * 1000); // Study for 45 seconds
    }

    private void stopAlarmsDuringBreak() {
        mp3Player.stop();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                onBreak = false;
                System.out.println("Break over, resuming study session...");
                scheduleStudyCycle(); // Restart the cycle after the break
            }
        }, breakTime * 60 * 1000); // Break for 15 seconds
    }

    private void scheduleRandomAlarm(int min, int max) {
        int delay = min + rand.nextInt(max - min + 1);
        long delayMillis = delay * 60 * 1000;

        TimerTask task = new TimerTask() {
            public void run() {
                if (!onBreak) {
                    System.out.println("Random alarm! Playing music...");
                    mp3Player.play();

                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            mp3Player.stop();
                            System.out.println("Stopped music.");
                            // Reschedule next alarm between 3-5 seconds
                            scheduleRandomAlarm(min, max);
                        }
                    }, 5000); // Stop music after 5 seconds
                }
            }
        };

        // Schedule the alarm
        timer.schedule(task, delayMillis);
    }
}

// YoutubeVideoPlayer class manages displaying the YouTube video in a WebView
public class YoutubeVideoPlayer extends Application {
    private final String video = "https://www.youtube.com/watch?v=OxMQjlBq6zQ&t=9530s";

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
    
        // Use an iframe to embed the YouTube video
        String videoEmbed = "<iframe width=\"840\" height=\"690\" src=\"https://www.youtube.com/embed/OxMQjlBq6zQ?autoplay=1\" " +
                            "frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
    
        // Load the iframe as HTML content in the WebView
        webView.getEngine().loadContent(videoEmbed, "text/html");
    
        webView.setPrefSize(840, 690);
        primaryStage.setScene(new Scene(webView));
        primaryStage.show();
    }
    
    
    

    public static void main(String[] args) {
        // Start the study session in a separate thread to avoid blocking JavaFX
        new Thread(() -> {
            Study session = new Study();
            session.startSession();
        }).start();

        // Launch JavaFX application
        launch(args);
    }
}
