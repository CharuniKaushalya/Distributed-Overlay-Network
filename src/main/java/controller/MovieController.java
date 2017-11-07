package main.java.controller;

import main.java.view.app;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Created by Thilini on 10/24/2017.
 */
public class MovieController {

    final static private Logger logger = Logger.getLogger(MovieController.class);
    private static MovieController instance;
    private List<String> movies = new ArrayList<String>();

    private MovieController(String filepath) {
        BasicConfigurator.configure();
        this.movies = getMovies(filepath);
        /*
        List<String> fileNames = new ArrayList<>();
        fileNames.add("Adventures of Tintin");
        fileNames.add("Jack and Jill");
        fileNames.add("Glee");
        fileNames.add("The Vampire Diarie");
        fileNames.add("King Arthur");
        fileNames.add("Windows XP");
        fileNames.add("Harry Potter");
        fileNames.add("Kung Fu Panda");
        fileNames.add("Lady Gaga");
        fileNames.add("Twilight");
        fileNames.add("Windows 8");
        fileNames.add("Mission Impossible");
        fileNames.add("Turn Up The Music");
        fileNames.add("Super Mario");
        fileNames.add("American Pickers");
        fileNames.add("Microsoft Office 2010");
        fileNames.add("Happy Feet");
        fileNames.add("Modern Family");
        fileNames.add("American Idol");
        fileNames.add("Hacking for Dummies");
        
        Collections.shuffle(fileNames);

        Random rand = new Random();
        int num = rand.nextInt(3) + 3;
        for (int i = 0; i < num; i++){
            this.movies.add(fileNames.get(i));
        }
        */
    }

    public static MovieController getInstance(String filepath) {
        if (instance == null) {
            synchronized (MovieController.class) {
                if (instance == null) {
                    instance = new MovieController(filepath);
                }
            }
        }

        return instance;
    }

    private List<String> getMovies(String filepath) {
        List<String> fileNames = new ArrayList<>();
        List<String> movies = new ArrayList<String>();

        InputStreamReader in = null;
        try {
            in = new InputStreamReader(this.getClass().getResourceAsStream(filepath), "UTF-8");
            Scanner s = new Scanner(in).useDelimiter("\n");

            while (s.hasNext()) {
                fileNames.add(s.next());
            }
            Collections.shuffle(fileNames);

            Random rand = new Random();
            int num = rand.nextInt(3) + 3;
            for (int i = 0; i < num; i++){
                movies.add(fileNames.get(i));
            }
            return movies;
        } catch (NullPointerException e) {
            logger.error(e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
        return null;
    }

    public List<String> getNodeMovies() {
        return this.movies;
    }

    public List<String> searchMovies(String query){
        List<String> foundList = new ArrayList<String>();
        String otherQuery = null;

        String thisQuery =  query.toLowerCase().replaceAll(" ", "_");

        if (query != null && !query.trim().equals("")) {

            for (String movie : movies) {

                otherQuery = "_"+movie.toLowerCase().replaceAll(" ","_")+"_";

                System.out.println(thisQuery);
                System.out.println(otherQuery);
                if (otherQuery.contains(thisQuery)) {
                    foundList.add(movie.replaceAll(" ","_"));
                }
            }
        }
        return foundList;
    }

}
