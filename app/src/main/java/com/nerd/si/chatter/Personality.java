package com.nerd.si.chatter;

public class Personality {
    //variables
    private int personalityNumber;
    //Fill arrays with actual content
    //private final String[] SONGS = {"Song1","Song2","Song3","Song4"};
    //private final String[] BANDS = {"Band1","Band2","Band3","Band4"};
    private final String[] MUSIC = {"Country","Bluegrass","Jazz","Indie Folk"};
    private final String[] SHOWS = {"Parks and Rec","Making a Murder","Black Mirror","Grey's Anatomy"};
    private final String[] MOVIES = {"Star Wars","The Godfather","Hot Fuzz","The Exorcist"};
    private final String[] PLACES = {"New York","the beach","my house","the slopes"};
    private final String[] SPORTS = {"baseball","swimming","endurance running","skiing"};
    private final String[] ANIMALS = {"a horse","an elephant","a dragon","a cow"};
    private final String[] FOODS = {"fruit","chocolate","pasta","popcorn"};
    private final String[] DRINKS = {"orange juice","Gatorade","iced coffee","beer"};
    private final String[] COLORS = {"yellow", "blue","purple","orange"};
    private final String[] BOOKS = {"Sherlock Holmes", "The Hobbit", "A Brief History of Time","Ready Player One"};
    private final String[] CELEB = {"Beyonce", "Elon Musk", "Gal Gadot", "Bill Gates"};
    //constructors
    public Personality() {
        personalityNumber = 0;
    }
    public Personality(int n) {
        personalityNumber = n;
    }

    //getters
    public String getColor() {
        return COLORS[personalityNumber];
    }
    /**
     public String getSong() {
     return SONGS[personalityNumber];
     }

     public String getBand() {
     return BANDS[personalityNumber];
     }**/

    public String getMusic() {
        return MUSIC[personalityNumber];
    }

    public String getShow() {
        return SHOWS[personalityNumber];
    }

    public String getMovie() {
        return MOVIES[personalityNumber];
    }

    public String getPlace() {
        return PLACES[personalityNumber];
    }

    public String getSport() {
        return SPORTS[personalityNumber];
    }

    public String getAnimal() {
        return ANIMALS[personalityNumber];
    }

    public String getFood() {
        return FOODS[personalityNumber];
    }

    public String getDrink() {
        return DRINKS[personalityNumber];
    }

    public String getBook() {
        return BOOKS[personalityNumber];
    }

    public String getCeleb() {
        return CELEB[personalityNumber];
    }
}
