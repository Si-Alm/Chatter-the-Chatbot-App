//TODO: Organize class
package com.nerd.si.chatter;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Chatter {
    /////////////////////////////////////
    //////////Class variables///////////
    ///////////////////////////////////
    // strings to track the previous user input and response
    protected String prevStatement = "";
    private String prevResponse = "";
    private String userName = ""; //tracks users name(that they give themselves)

    private int greetingCount = 0; // keeps track of number of times getGreeting is called
    private int emptyCount = 0; // keeps track of number of times getEmptyResponse is called
    private int discussCount = 0;

    // random integer (0-3) that will be used to determine personality based
    // responses
    // in methods such as 'whatProcessor'
    private int personalityNumber = (int) (Math.random() * 4);
    Personality personality = new Personality(personalityNumber);

    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private DateFormat dateFormat1 = new SimpleDateFormat("hh:mm:ss");
    private Calendar cal = Calendar.getInstance();

    //ArrayLists
    private ArrayList<String> inspQuotes = new ArrayList<>();
    private ArrayList<String> funnyQuotes = new ArrayList<>();
    private ArrayList<String> wiseQuotes = new ArrayList<>();
    private ArrayList<String> famousQuotes = new ArrayList<>();



    ////////////////////////////////////////////////////
    /////////////Main methods used in driver///////////
    //////////////////////////////////////////////////
    public String getGreeting() { // initial greeting statement
        return "Welcome to chatter, say 'Hi' to talk or 'Bye' to exit.";
    }

    /*
     * TODO: Add response for top 10 most common phrases
     * Fix and alter transform statements. Prioritize the extended
     * if/else if/else statement for most natural conversation and repeat words
     */
    public String getResponse(String statement) {
        String response = "";
        prevStatement = statement;
        if(statement.length() == 0) {
            if(emptyCount < 5) {
                emptyCount++;
                return getEmptyResponse();
            } else if(emptyCount>=5 && prevStatement.equals("")){
                return "Fine, I didn't want to talk anyway";
            }
        }

        //TODO: Accounts for accidental/random question marks
        //however, it resurfaces the first statement 'will' question bug and also applies to 'why'
        //statement = statement.replace("?", "");
        int youPsn = findKeyword(statement, "You", 0);
        int indx = statement.indexOf('?');
        if(indx>=0 && !questionMarkChecker(statement)) {
            statement = statement.substring(0, statement.length()-1);
        }

        if(!(statement.charAt(statement.length() -1) == '?') && questionMarkChecker(statement)) {
            statement = statement + "?";
        } //end question mark adder if statement


        if (statement.length() >= 1 && statement.charAt(statement.length() - 1) == '?') {
            return processQuestion(statement);
        }

        if ((findKeyword(statement, "lets") >= 0 ||
                findKeyword(statement, "let us") >= 0)  &&
                (findKeyword(statement, "talk") >= 0    ||
                        findKeyword(statement, "converse") >= 0)) {
            response = "Okay, what would you like to talk about?";
        } else if((( findKeyword(statement, "my") >=0 && ((findKeyword(statement, "name") >=0 || findKeyword(statement, "names") >=0))) ||
                findKeyword(statement, "im") >=0 || findKeyword(statement, "i am") >= 0|| findKeyword(statement, "call me") >=0 )     ||
                findKeyword(prevResponse, "Chatter the Chatbot") >=0) {
            userName = getName(statement);
            return "Nice to meet you, " + userName + ".";
        } else if ((findKeyword(statement, "I hate") >= 0 ||
                findKeyword(statement, "I loathe") >= 0   ||
                findKeyword(statement, "I despise") >= 0) &&
                findKeyword(statement, "do") < 0){
            response = transformIHateStatement(statement);
        } else if (((hasUncertainty(statement)     &&
                (toDiscuss(prevStatement)          ||
                        toDiscuss(prevResponse)))  ||
                toDiscuss(statement))              ||
                discussCount >= 1                  &&
                        (findKeyword(statement, "no") >= 0 ||
                                findKeyword(statement, "not") >= 0)) {
            response = getDiscussResponse();
            if (discussCount >= 1)
                response = "Well, we can discuss something else as well.";
            discussCount++;
        }  else if(findKeyword(statement, "thats what")>=0) {
            response = transformThatsWhatStatement(statement);
        }  else if(findKeyword(statement, "bye")>=0 || findKeyword(statement, "goodbye")>=0) {
              return "Okay, goodbye now!";
        } else if(findKeyword(statement, "be okay")>=0) {
            response = "I'm not so sure.";
        }  else if (findKeyword(statement, "i want") >= 0 || findKeyword(statement, "i would like") >= 0) {
            response = transformIWantToStatement(statement);
        } else if (findKeyword(prevStatement, "i want") >= 0 && findKeyword(statement, "yes") >= 0) {
            response = "Well okay then.";
        } else if (findKeyword(prevStatement, "i want") >= 0 && findKeyword(statement, "no") >= 0) {
            response = "See, I knew it.";
        } else if (findKeyword(statement, "will you") >= 0) {
            response = transformWillYou(statement);
        } else if (findKeyword(statement, "i am") >= 0) {
            response = transformIAm(statement);
        } else if(((findKeyword(statement, "tell me") >=0 || findKeyword(statement, "give me") >=0 || findKeyword(statement, "hit me") >=0) && //TODO Consider fixing a little
                (findKeyword(statement, "quote") >=0)) || findKeyword(prevResponse, "What type of quote would you like") >=0) {

            if(findKeyword(prevResponse , "What type of quote would you like") >=0 ||
                    (findKeyword(statement, "inspirational") >=0) || findKeyword(statement, "famous") >=0 ||
                    findKeyword(statement, "wise") >=0 || findKeyword(statement, "funny") >=0) {
                prevResponse = "quoted";
                return getQuote(statement);
            }
            response = "What type of quote would you like, an inspirational, funny, wise, or famous quote?";
        }
        else if (findKeyword(statement, "tell me a joke") >= 0
                || findKeyword(statement, "tell me another joke") >= 0) {
            response = getJoke();
            if (response.equals(prevResponse))
                response = getJoke();
        } else if (findKeyword(statement, "you are") >= 0) {
            response = transformYouAreStatement(statement);
        } else if (youPsn >= 0 && findKeyword(statement, "me", youPsn) >= 0) {
            response = transformYouMeStatement(statement);
        } else if (findKeyword(prevStatement, "tell me a joke") >= 0 ||
                findKeyword(prevStatement, "give me a joke") >= 0    &&
                        (findKeyword(statement, "funny") >= 0                ||
                                negWordChecker(statement))) {
            response = "I mean, I find that joke funny.";
        } else if (findKeyword(statement, "meaning of life") >= 0) {
            response = getMeaningOfLife();
        }  else if (findKeyword(statement, "thank you") >= 0 || findKeyword(statement, "thanks") >= 0) {
            response = "You're welcome";
        } else if ((hasWhats(statement) && // random verb response statements to respond to 'what's up' type statements
                (findKeyword(statement, "up") >= 0        ||
                        findKeyword(statement, "chilling") >= 0)  ||
                findKeyword(statement, "happening") >= 0) ||
                findKeyword(statement, "you up to") >= 0) {
            response = getRandomVerbResponse();
        } else if(findKeyword(statement, "im") >=0 && statement.length() <= 12) {
            int psn = findKeyword(statement, "im");
            response = "Hello," + statement.substring(psn + 2).replaceAll("[!?.]", "") + ".";
        } else if (findKeyword(statement, "hello") >= 0 ||
                findKeyword(statement, "hi") >= 0    ||
                findKeyword(statement, "hey") >= 0   ||
                findKeyword(statement, "yo") >= 0) {

            response = getRandomGreeting();
            if(greetingCount == 0)
                response = "Hello, let's talk.";
            if (greetingCount > 5)
                response = "I think we've covered the greetings quite thoroughly.";
            greetingCount++;

        } /*
		else if (statement.length() >= 1 && statement.charAt(statement.length() - 1) == '?') {
			response = processQuestion(statement);
		} */
        else {
            response = getRandomResponse();
            if (response.equals(prevResponse))
                response = getRandomResponse();
        }
        prevResponse = response;
        //prevStatement = statement;
        return response;
    }


    //////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////Transformer methods//////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////


    private String getName(String statement) {
        statement = stringTrimmer(statement);
        int psn = 0;
        String userName = "";
        if(findKeyword(statement, "im") >=0) {
            psn = findKeyword(statement, "im");
            userName = statement.substring(psn + 3);

        } else if(findKeyword(statement, "i am") >=0) {
            psn = findKeyword(statement, "i am");
            userName = statement.substring(psn+5);
        }
        else if(findKeyword(statement, "my name is") >=0) {
            psn = findKeyword(statement, "my name is");
            userName = statement.substring(psn+11);
        } else if(findKeyword(statement, "my names") >=0) {
            psn = findKeyword(statement, "my names");
            userName = statement.substring(psn+9);
        } else if(findKeyword(statement, "call me") >=0) {
            psn = findKeyword(statement, "call me");
            userName = statement.substring(psn+8);
        }


        userName = userName.substring(0, 1).toUpperCase() + userName.substring(1);
        return userName;
    }
    //String trimmer for transformation methods, namely just removes end punctuation
    private String stringTrimmer(String statement) {
        String lastChar = statement.substring(statement.length() - 1);

        while (lastChar.equals(".") || lastChar.equals("?") || lastChar.equals("!")) {
            statement = statement.substring(0, statement.length() - 1);
            lastChar = statement.substring(statement.length() - 1);
        }
        return statement;
    }

    ////////////////////////////
    //I <something> transformers
    ///////////////////////////
    /**
     * Method to transform an "I hate <something>" statement
     *
     * @param statement
     * @return
     */
    private String transformIHateStatement(String statement) {
        statement = stringTrimmer(statement).trim();

        int psn = 0;
        String restOfStatement = "";
        if (findKeyword(statement, "I hate") >= 0) {
            psn = findKeyword(statement, "I hate", 0);
            restOfStatement = statement.substring(psn + 6).trim();
        }
        if (findKeyword(statement, "I loathe") >= 0) {
            psn = findKeyword(statement, "I loathe");
            restOfStatement = statement.substring(psn + 8).trim();
        }
        if (findKeyword(statement, "I despise") >= 0) {
            psn = findKeyword(statement, "I despise");
            restOfStatement = statement.substring(psn + 9).trim();
        }

        if (restOfStatement.charAt(restOfStatement.length() - 1) == 's')
            return "Yeah " + restOfStatement + " are pretty terrible.";
        return "Yeah " + restOfStatement + " is pretty terrible.";
    }

    /**
     * Method to transform an "I want to <something>" statement
     *
     * @param statement
     * @return
     */
    private String transformIWantToStatement(String statement) {
        // Remove the final period, if there is one
        statement = stringTrimmer(statement).trim();
        int psn = findKeyword(statement, "I want to", 0);
        String restOfStatement = statement.substring(psn + 9).trim();
        return "Do you really want to " + restOfStatement + "?";
    }


    /**
     * method to transform an "I am <something>" statement
     *
     * @param statement
     * @return
     */
    private String transformIAm(String statement) {
        statement = stringTrimmer(statement).trim();
        int psn = findKeyword(statement, "You are", 0);
        String restOfStatement = statement.substring(psn + 5).trim();

        return "What makes you " + restOfStatement + "?";
    }



    ///////////////////////////////
    //You <something> transformers
    ///////////////////////////////
    private String transformYouAreStatement(String statement) {
        statement = stringTrimmer(statement).trim();
        int psn = findKeyword(statement, "You are", 0);
        String restOfStatement = statement.substring(psn + 7).trim();

        return "Why am I " + restOfStatement + "?";

    }

    /**
     * Method to transform a "You <something> me" statement
     *
     * @param statement
     * @return
     */
    private String transformYouMeStatement(String statement) {
        // Remove the final period, if there is one
        statement = stringTrimmer(statement).trim();

        int psnOfYou = findKeyword(statement, "you", 0);
        int psnOfMe = findKeyword(statement, "me", psnOfYou + 3);

        String restOfStatement = statement.substring(psnOfYou + 3, psnOfMe).trim();
        return "What makes you think that I " + restOfStatement + " you?";
    }



    /////////////////////////////////
    //When, where, what transformers
    ////////////////////////////////
    private String transformWhenStatement(String statement) {
        statement = stringTrimmer(statement).trim();
        String restOfStatement = "";
        int psn = findKeyword(statement, "when");

        if (findKeyword(statement, "When is") >= 0) {
            psn = findKeyword(statement, "When is");
            restOfStatement = statement.substring(psn + 7).trim() + ".";

        }
        if (findKeyword(statement, "When was") >= 0) {
            psn = findKeyword(statement, "When was");
            restOfStatement = statement.substring(psn + 8).trim() + " was.";
        }
        if (findKeyword(statement, "When will") >= 0) {
            psn = findKeyword(statement, "When will");
            if (findKeyword(statement, "when will i") >= 0) {
                psn = psn + 1;
                restOfStatement = "you will " + statement.substring(psn + 11).trim();
                return "I am unsure of when " + restOfStatement + ".";
            }
        }

        return "I am unsure of when " + restOfStatement;
    }

    //TODO: Add separate method or inner structure for places
    private String transformWhereIsAreStatement(String statement) {
        statement = stringTrimmer(statement).trim();
        String restOfStatement = "";
        String plurality = "";
        int psn = findKeyword(statement, "where");
        if (findKeyword(statement, "Where is") >= 0) {
            psn = findKeyword(statement, "where is");
            restOfStatement = statement.substring(psn + 8).trim() + " is";
            plurality = "it";
        }
        if(findKeyword(statement, "wheres") >=0) {
            psn = findKeyword(statement, "wheres");
            restOfStatement = statement.substring(psn+7) + "is";
            plurality = "it";
        }
        if (findKeyword(statement, "where are") >= 0) {
            psn = findKeyword(statement, "where are");
            if(findKeyword(statement, "you") >=0) {
                return "I'm wherever you want me to be...except on Mondays through Sundays.";
            }
            restOfStatement = statement.substring(psn + 9).trim() + " are";
            plurality = "them";
        }

        //capitalizes first character of string
        restOfStatement = restOfStatement.substring(0, 1).toUpperCase() + restOfStatement.substring(1);
        return restOfStatement + " wherever you left " + plurality + ".";
    }

    private String transformWhereAmStatement(String statement) {
        statement = stringTrimmer(statement);
        String restOfStatement = "";
        int psn = findKeyword(statement, "where am");
        if(findKeyword(statement, "where am i") >=0) {
            if(statement.length() <= 11)
             return "How would I know where you're at, I ain't no stalker.";
            psn = findKeyword(statement, "where am i");
            restOfStatement = "you are " + statement.substring(psn+11).replace("my", "your").replace("me", "you");
        } else {
            restOfStatement = statement.substring(psn+9);
        }
        return "I am unsure of where " + restOfStatement + ".";
    }

    private String transformWhatDoesStatement(String statement) {
        statement = stringTrimmer(statement).trim();
        String restOfStatement = "";
        int psn = findKeyword(statement, "what does");
        restOfStatement = statement.substring(psn + 9);

        return "You tell me, what DOOES" + restOfStatement + "?";
    }

    private String transformWhatIsStatement(String statement) {
        if(findKeyword(statement, "my name") >=0)
            return "Only you know your name";
        statement = stringTrimmer(statement);
        String restOfStatement;
        int psn = findKeyword(statement, "what is");
        restOfStatement = statement.substring(psn+8);
        if(findKeyword(statement, "whats")>=0) {
            psn = findKeyword(statement, "whats");
            restOfStatement = statement.substring(psn +5);
        }
        restOfStatement = restOfStatement.substring(0,1).toUpperCase() + restOfStatement.substring(1);
        return restOfStatement + " is a facet of your imagination.";
    }


    //TODO fix "i" to "you" replace statement
    private String transformThatsWhatStatement(String statement) {
        statement = stringTrimmer(statement);
        String response;;
        int psn = findKeyword(statement, "thats what");

        String restOfStatement = statement.substring(psn+11);

        if(findKeyword(statement, "she said")>=0) {
            return "HA! Nice one!";
        }


        if(findKeyword(statement, "you") >=0) {
            restOfStatement = restOfStatement.replace("you", "me");
        } else if(findKeyword(statement, "me") >=0) {
            restOfStatement = restOfStatement.replace("me", "you");
        }
        if(findKeyword(statement, "i") >=0) {
            int iPsn = findKeyword(statement, "i");
            restOfStatement = restOfStatement.substring(0, iPsn) + "you " + restOfStatement.substring(psn+1);
            //response = response.replace(" i ", "you");
        }
        response = "Really? That's what " + restOfStatement + ".";
        return response;
    }

    ////////////////////////////////
    //Will <something> transformers
    ////////////////////////////////
    private String transformWillTheStatement(String statement) {
        statement = stringTrimmer(statement).trim();
        String restOfStatement = "";

        int psn = findKeyword(statement, "will");
        restOfStatement = statement.substring(psn + 4).trim();
        if (findKeyword(statement, "world") >= 0 && findKeyword(statement, "end") >= 0)
            return "With the current state, most definitely yes.";

        return "I don't know, WILL " + restOfStatement + "?";
    }

    private String transformWillItStatement(String statement) {
        statement = stringTrimmer(statement).trim();
        String restOfStatement = "";
        int psn = findKeyword(statement, "will");

        if (findKeyword(statement, "will it") >= 0)
            restOfStatement = statement.substring(psn + 7).trim();

        restOfStatement = restOfStatement.replace("my", "your");
        restOfStatement = restOfStatement.replace("me", "you");
        restOfStatement = restOfStatement.replaceFirst("I", "you");
        return "Hmm, I suppose it could " + restOfStatement + ".";
    }

    /**
     * Partially broken statement to transform "will I" style questions
     * @param statement
     * @return
     */
    private String transformWillIStatement(String statement) {
        String restOfStatement = "";

        statement = stringTrimmer(statement).trim();
        int psn = findKeyword(statement, "will");
        restOfStatement = statement.substring(psn + 4).trim();

        if (findKeyword(statement, "when") >= 0) {
            psn = findKeyword(statement, "when will i");
            restOfStatement = statement.substring(psn + 11).trim();
            return "I'm not sure when you will " + restOfStatement + ".";
        }
        if (findKeyword(statement, "i") >= 0) {
            return "I'm not sure if you will " + restOfStatement + ", but what do I know?";
        }


        return "I'm not sure if you will " + restOfStatement + ", but what do I know?";
    }

    /**
     * A method to transform "Will you <something>" statement
     *
     * @param statement
     * @return
     */
    private String transformWillYou(String statement) {
        statement = stringTrimmer(statement).trim();
        if (findKeyword(statement, "me") >= 0) { // replaces instances of my or me, to you or your
            statement = statement.replace("me", "you");
        }
        if (findKeyword(statement, "my") >= 0) {
            statement = statement.replace("my", "your");
        }

        int psn = findKeyword(statement, "You are", 0);
        String restOfStatement = statement.substring(psn + 9).trim();

        return "I'm not sure I can " + restOfStatement + ".";
    }

    private String transformWillIYouStatement(String statement) {
        statement = stringTrimmer(statement).trim();

        int psn = findKeyword(statement, "will i");
        int youPsn = findKeyword(statement, "you");
        String restOfStatement = statement.substring(psn + 6, youPsn);

        return "It's up to you if you will" + restOfStatement + "me.";
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////// Bank methods to for pulling random phrases, jokes, and other responses///////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////

    // method to check statement for instances and varieties of 'I don't know'
    private boolean hasUncertainty(String statement) {
        statement = statement.replace("\'", "");
        ArrayList<String> uncertainties = new ArrayList<>();
        uncertainties.add("dont know");
        uncertainties.add("not sure");
        uncertainties.add("unsure");
        uncertainties.add("cant decide");
        uncertainties.add("anything");

        for (int i = 0; i < uncertainties.size(); i++)
            if ((findKeyword(statement, "I") >= 0 || findKeyword(statement, "Im") >= 0)
                    && findKeyword(statement, uncertainties.get(i)) >= 0)
                return true;

        return false;
    }

    private String getDiscussResponse() {
        final int NUM_OF_RESPONSES = 5;
        double r = Math.random();
        int m = (int) (r * NUM_OF_RESPONSES);
        String dResponse = "";
        if (m == 0) {
            dResponse = "Let's talk about whatever you want to talk about.";
        }
        if (m == 1) {
            dResponse = "We can talk about literally anything.";
        }
        if (m == 2) {
            dResponse = "Oh, let's talk about whatever floats a goat.";
        }
        if (m == 3) {
            dResponse = "Let's discuss you.";
        }
        if (m == 4) {
            dResponse = "Hmmm...how about we discuss something light, like philosophy or politics.";
        }

        return dResponse;
    }

    private String getEmptyResponse() {
        final int NUM_OF_RESPONSES = 5;
        double r = Math.random();
        int m = (int) (r * NUM_OF_RESPONSES);
        String emptyResponse = "";
        if (m == 0) {
            emptyResponse = "Please, say something.";
        } else if (m == 1) {
            emptyResponse = "Your silence makes me uncomfortable.";
        } else if (m == 2) {
            emptyResponse = "Ummm...hello?";
        } else if (m == 3) {
            emptyResponse = "You're supposed to type words, not just think them.";
        } else if (m == 4) {
            emptyResponse = "Sorry, but I think you forgot to respond.";
        }

        return emptyResponse;
    }

    // TODO: Add more meanings
    private String getMeaningOfLife() { // method to account for "meaning of life"
        // cuz that's something that can exist
        final int NUM_OF_RESPONSES = 5;
        double r = Math.random();
        int m = (int) (r * NUM_OF_RESPONSES);
        String meaning = "";
        if (m == 0) {
            meaning = "42.";
        } else if (m == 1) {
            meaning = "To simply exist.";
        } else if (m == 2) {
            meaning = "To outlive the person that is trying to kill you.";
        } else if (m == 3) {
            meaning = "There is none.";
        } else if (m == 4) {
            meaning = "The meaning of life is relative to one's own independent values and"
                    + "\n philosophical perspectives. To live through your values"
                    + "\n and outlooks to attain happieness and contentment in the universe"
                    + "\n is the true meaning of not only life, but existence...or something like that";
        }
        return meaning;
    }

    private String getRandomVerbResponse() { // method to get random response to phrases such as, "what's up"
        ArrayList<String> verbs = new ArrayList<>();
        verbs.add("You know, just a BIT of this and a BIT of that.");
        verbs.add("Ohh, just chillin' like a villain.");
        verbs.add("Having this pointless conversation...");
        verbs.add("Trying to think of clever responses to you ridiculous questions.");
        return verbs.get((int) (Math.random() * verbs.size()));
    }

    private String getRandomGreeting() { // method to get random greeting if user says hi, hello, etc.
        ArrayList<String> greetings = new ArrayList<>();
        greetings.add("Hello!");
        greetings.add("Hi.");
        greetings.add("Salutaions.");
        greetings.add("Hey!");
        greetings.add("Suuhhh.");

        return greetings.get((int) (Math.random() * greetings.size()));
    }

    // TODO: Add more jokes
    private String getJoke() { // method to get random joke, currently 10
        ArrayList<String> jokes = new ArrayList<>();
        jokes.add("Can a kangaroo jump higher than a house? .... Of course, a house doesn’t jump at all.");
        jokes.add("My dog used to chase people on a bike a lot. It got so bad, finally I had to take his bike away.");
        jokes.add(
                "In a boomerang shop: \"I'd like to buy a new boomerang please." +
                        " Also, can you tell me how to throw the old one away?\"");
        jokes.add("The inventor of AutoCorrect is a stupid mass hole. He can fake right off.");
        jokes.add("Did you hear about the Indian who drank to much tea...he drowned in his tea-pee.");
        jokes.add("I thought I’d tell you a good time travel joke – but you didn't like it.");
        jokes.add("I‘ve decided to run a marathon for charity. I didn’t want to do it at first, but apparently it’s"
                + "for blind and disabled kids, so I think I’ve got a good chance of winning.");
        jokes.add("Why have you never seen an elephant hiding in a tree?" +
                " Because they’re really, really good at it.");
        jokes.add(
                "Yes, money cannot buy you happiness, but I’d still feel a lot more comfortable crying in a new BMW than on a bike.");
        jokes.add("Don’t be sad when a bird craps on your head. Be happy that dogs can’t fly.");
        jokes.add("What do you call a fake noodle...an impasta!");
        jokes.add("What did the traffic light say to the car? " +
                "Don’t look! I’m about to change.");
        jokes.add("Why was the little strawberry crying?" +
                "His mom was in a jam.");
        jokes.add("What do you call a nosy pepper? " +
                "Jalapeño business.");
        jokes.add("Why are frogs are so happy? They eat whatever bugs them.");
        jokes.add("Why did the jaguar eat the tightrope walker? It was craving a well-balanced meal.");

        return jokes.get((int) (Math.random() * jokes.size()));
    }

    // Method to get random response in the event that
    // input doesn't match any reprogrammed response
    private String getRandomResponse() {

        ArrayList<String> responses = new ArrayList<>();
        responses.add("Interesting, tell me more.");
        responses.add("Hmmm.");
        responses.add("Do you really think so?");
        responses.add("You don't say.");
        responses.add("How about that.");
        responses.add("Who woulda thunk?");
        responses.add("If you say so.");
        responses.add("Mmm, compelling.");
        responses.add("I may have stopped paying attention.");
        responses.add("*Pre-programmed response here");
        responses.add("I don't know what to say.");
        responses.add("That is quite a erudite point.");
        return responses.get((int) (Math.random() * responses.size()));
    }

    //Method to add information to quote lists
    //TODO All arrayLists?
    private void quoteAdder() {
        //Inspirational
        inspQuotes.add("\"Keep your face to the sunshine and you cannot see a shadow\"" + "\n -Helen Keller");
        inspQuotes.add("\"A champion is defined not by their wins but by how they can recover when they fall.\"" + "\n -Serena Williams");
        inspQuotes.add("\"Each person must live their life as a model for others.\" " + "\n -Rosa Parks");
        inspQuotes.add("\"No matter what people tell you, words and ideas can change the world.\" \n -Robin Williams");
        inspQuotes.add("\"We become what we think about.\"\n –Earl Nightingale");
        inspQuotes.add("\" Whatever the mind of man can conceive and believe, it can achieve.\" \n -Napolean Hill");
        inspQuotes.add("\"Life is about making an impact, not making an income.  \"\n -Kevin Kruse");
        inspQuotes.add("\"The most difficult thing is the decision to act, the rest is merely tenacity \" \n -Amelia Earhart");
        inspQuotes.add("\"Life is 10% what happens to me and 90% of how I react to it \" \n -Charles Swindoll");
        //Famous
        famousQuotes.add("\"Imagination is more important than knowledge\" \n -Albert Einstein");
        famousQuotes.add("'|If music be the food of love, play on\" \n -William Shakespeare");
        famousQuotes.add("\"The way to get started is to quit talking and begin doing\" \n -Walt Disney");
        famousQuotes.add("\"Obstacles are those frightful things you see when you take your eyes off the goal\" \n -Henry Food");
        famousQuotes.add("\"I skate where the puck is going to be, not where it has been\" \n -Wayne Gretzky");
        famousQuotes.add("\"When you come to a fork in the road, take it\" \n -Yogi Berra");
        famousQuotes.add("\"We may affirm absolutely that nothing great in the world has been accomplished without passion\" \n -Hegel");
        famousQuotes.add("\"The life which is unexamined is not worth living\" \n -Socrates");
        famousQuotes.add("\"Live as if you were to die tomorrow. Learn as if you were to live forever\" \n -Mohandas Gandhi");
        famousQuotes.add("\"What you get by achieving your goals is not as important as what you become by achieving your goals\" /n -Zig Ziglar");


        //Wisdom
        wiseQuotes.add("\"The fool wonders, the wise man asks.\" \n  -Benjamin Disraeli");
        wiseQuotes.add("\"To attain knowledge, add things everyday. To attain wisdom, remove things every day.\" \n -Lao Tzu");
        wiseQuotes.add("\"Where there is shouting, there is no true knowledge.\" \n  -Leonardo da Vinci");
        wiseQuotes.add("\"Only the wisest and stupidest of men never change.\" \n -Confucius");
        wiseQuotes.add("\"Logic is the beginning of wisdom, not the end.\" \n -Leonard Nimoy ");
        wiseQuotes.add("\"What wisdom can you find that is greater than kindness?\" \n -Jean-Jacques Rousseau");
        wiseQuotes.add("\"He gossips habitually; he lacks the common wisdom to keep still that deadly enemy of man, his own tongue.\" \n -Mark Twain");
        wiseQuotes.add("\"Everything comes in time to him who knows how to wait.\" \n -Leo Tolstoy");
        wiseQuotes.add("\"Honesty is the first chapter in the book of wisdom.\" \n  -Thomas Jefferson");
        wiseQuotes.add("\"Wisdom and deep intelligence require an honest appreciation of mystery.\" \n -Thomas Moore");
        wiseQuotes.add("\"The truest wisdom is a resolute determination.\" \n -Napoleon Bonaparte");
        wiseQuotes.add("\"Science is organized knowledge. Wisdom is organized life.\" \n -Immanuel Kant");
        wiseQuotes.add("\"Wisdom is not a product of schooling but of the lifelong attempt to acquire it.\" \n -Albert Einstein");

        //Funny
        funnyQuotes.add("\" This suspense is terrible. I hope it will last.\" \n -Oscar Wilde");
        funnyQuotes.add("\" I did not attend his funeral, but I sent a nice letter saying I approved of it. \" \n -Mark Twain");
        funnyQuotes.add("\" If a book about failures doesn’t sell, is it a success?\" \n -Jerry Seinfeld");
        funnyQuotes.add("\"A lie gets halfway around the world before the truth has a chance to get its pants on \" \n -Winston Churchill");
        funnyQuotes.add("\"Accept who you are. Unless you’re a serial killer \" \n -Ellen DeGeneres");
        funnyQuotes.add("\" When life gives you lemons, squirt someone in the eye\" \n -Cathy Guisewite");
        funnyQuotes.add("\"Knowledge is like underwear. It is useful to have it, but not necessary to show it off \" \n -Bill Murray");
        funnyQuotes.add("\"A pessimist is a man who thinks everybody is as nasty as himself, and hates them for it \" \n -George Bernard Shaw");
    }

    private String getQuote(String statement) {
        quoteAdder();
        String quote = "";

        if(findKeyword(statement, "inspirational") >=0 ||
                findKeyword(statement, "inspire") >0   ||
                findKeyword(statement, "inspr") >=0)   {
            quote = inspQuotes.get((int) (Math.random() * inspQuotes.size()));

        } else if(findKeyword(statement, "funny") >=0 ||
                findKeyword(statement, "comedic") >=0 ||
                findKeyword(statement, "comedy") >=0  ||
                findKeyword(statement, "fun") >=0) {
            quote = funnyQuotes.get((int) (Math.random() * funnyQuotes.size()));
        } else if(findKeyword(statement, "wisdom") >=0 ||
                findKeyword(statement, "wise") >=0     ||
                findKeyword(statement, "sage") >=0) {
            quote = wiseQuotes.get((int) (Math.random() * wiseQuotes.size()));
        } else if(findKeyword(statement, "famous") >=0  ||
                findKeyword(statement, "well known") >=0||
                findKeyword(statement, "popular") >=0) {
            quote = famousQuotes.get((int) (Math.random()*famousQuotes.size()));
        } else {
            quote = "Hmmm, I can't think of any good ones off the top of my head.";
        }

        return quote;
    }

    ///////////////////////////////////////////////////////////////
    ////////////////Question processor methods////////////////////
    /////////////////////////////////////////////////////////////

    //method to process various types of questions and return an appropriate
    // response
    // will help cut else if code within the getResponse method
    // TODO: Start using findKeyword(statement, "word") (> || <)
    // findKeyword(statement, "otherWord"), to compare positions of keywords so
    // phrases like "is what" and "what is" are not mixed up
    private String processQuestion(String statement) {
        String response = "";
        if (findKeyword(statement, "how") >= 0) {
            response = howProcessor(statement);
        } else if (findKeyword(statement, "what") >= 0 || hasWhats(statement)) {
            if (hasWhats(statement))
                response = whatsProcessor(statement);
            else
                //finish whatProcessor
                response = whatProcessor(statement);
        } else if (findKeyword(statement, "when") >= 0|| findKeyword(statement, "whens") >=0) {
            response = whenProcessor(statement);
        } else if (findKeyword(statement, "where") >= 0 || findKeyword(statement, "wheres") >=0) {
            if(findKeyword(statement, "where am") >=0)
                response = transformWhereAmStatement(statement);
            else
                response = transformWhereIsAreStatement(statement);
        } else if (findKeyword(statement, "who") >= 0 || findKeyword(statement, "whos") >=0) {
            if((findKeyword(statement, "made") >=0 || findKeyword(statement ,"created")>=0 ||
                    findKeyword(statement, "creator") >=0 || findKeyword(statement, "maker") >=0)&&
                    (findKeyword(statement, "you") >=0 || findKeyword(statement, "your") >=0))
                return "The ever flatulent and recursively nerdy scrub known only as, \"Silas\", developed me slowly and non-eloquently";
            response = whoProcessor(statement);
        } else if(findKeyword(statement, "will") >=0 && findKeyword(statement, "will") < 4) {
            response = willProcessor(statement);
        } else if(findKeyword(statement, "why") >=0) {
            if(findKeyword(statement, "does") >=0)
                response = "Why does anything happen?";
            else
                response = "Why not?";
        } else if(findKeyword(statement, "does") >=0) {
            response = doesProcessor(statement);
        } else if(findKeyword(statement, "do")>=0) {
            response = doProcessor(statement);
        } else if(findKeyword(statement, "is") >=0) {
            response = isProcessor(statement);
        } else if(findKeyword(statement, "are") >=0) {
            response = areProcessor(statement);
        }
        return response;
    }

    /**
     * Method to check if string needs a question mark placed a length()-1
     * @param statement
     * @return
     */
    private boolean questionMarkChecker(String statement) {
        boolean checked = false;
        if(statement.length() >= 6)
            checked =
                    statement.substring(0, 4).equals("what") ||
                            statement.substring(0, 4).equals("when") ||
                            statement.substring(0, 3).equals("why")  ||
                            statement.substring(0, 5).equals("whats")||
                            statement.substring(0, 4).equals("when") ||
                            statement.substring(0, 5).equals("whens")||
                            statement.substring(0, 4).equals("will") ||
                            statement.substring(0, 3).equals("how")  ||
                            statement.substring(0, 5).equals("where")||
                            statement.substring(0, 2).equals("do")   ||
                            statement.substring(0, 2).equals("is")   ||
                            statement.substring(0, 3).equals("are")  ||
                            findKeyword(statement, "who") >=0        ||
                            findKeyword(statement, "whos") >=0       ||
                            findKeyword(statement, "who is") >=0     ||
                            findKeyword(statement, "does") >=0       ||
                            ((findKeyword(statement, "what") >= 0    &&
                                    findKeyword(statement, "is") >= 0));
        return checked;
    }

    /**
     * Processes statement like a transformer method
     * statementType will be something like "how are"
     * then it will add calculate restOfStatement
     * and add the adder string after the first word
     * in restOfStatement
     * @param statement
     * @param adder
     * @param statementType
     * @return
     */
    private String conjAdder(String statement, String adder, String statementType) {
        String lastHalf = "";
        String firstHalf = "";
        if(findKeyword(statement, "or")>=0) {
            statement = statement.substring(0, findKeyword(statement, "or")-1);
        }
        int psn = findKeyword(statement, statementType);
        String restOfStatement = statement.substring(psn+statementType.length());

        String newStat[] = restOfStatement.split(" ");
        firstHalf = newStat[1];
        for(int i=2; i<newStat.length; i++)
            lastHalf += " " + newStat[i];
        // firstHalf = firstHalf.substring(0,1).toUpperCase() + firstHalf.substring(1);
        return firstHalf +" " + adder  + lastHalf;

    }
    /////////////////////////////////////
    //individual question type processors
    ////////////////////////////////////
    private String willProcessor(String statement) {
        String response = "";

        if((findKeyword(statement, "you") >=0 && findKeyword(statement, "i") >=0) &&
                findKeyword(statement, "you") > findKeyword(statement, "i")) {
            response = transformWillIYouStatement(statement);
        } else if(findKeyword(statement, "you") >=0) {
            response = transformWillYou(statement);
        } else if(findKeyword(statement, "i") >=0) {
            response = transformWillIStatement(statement);
        } else if(findKeyword(statement,"will the") >=0){
            response = transformWillTheStatement(statement);
        } else if(findKeyword(statement, "will it") >=0) {
            response = transformWillItStatement(statement);
        } else {
            response = "I don't really understand the question, but probably not.";
        }

        if(findKeyword(response, "but what do I know")>=0)
            //&& (findKeyword(response, "but") > findKeyword(statement, "I")))
            return response.replaceFirst("i ", "");
        return response;
    }

    /**
     * Method to process 'what's' or 'what is' style questions and return
     * appropriate response
     *
     * @param statement
     * @return
     */
    private String whatsProcessor(String statement) {
        String response = "";
        int whatPsn = findKeyword(statement, "what");

        if ((findKeyword(statement, "up") >= 0 || findKeyword(statement, "chilling") >= 0)
                || findKeyword(statement, "happening") >= 0 || findKeyword(statement, "you up to") >= 0) {
            response = getRandomVerbResponse();
        } else if (findKeyword(statement, "date") >= 0) {
            String date = dateFormat.format(cal.getTime());
            response = "The current date is " + date;
        } else if (findKeyword(statement, "time") >= 0) {
            String time = dateFormat1.format(cal.getTime());
            response = "The current time is " + time;
        } else if (findKeyword(statement, "year") >= 0) {
            String year = dateFormat.format(cal.getTime());
            year = year.substring(0, 4);
            response = "The current year is " + year;
        } else if ((findKeyword(statement, "your") > whatPsn)) {
            // add response types for what's/what is your type questions
            if (findKeyword(statement, "favorite") >= 0) { // start favorite if
                if (findKeyword(statement, "song") >= 0 || findKeyword(statement, "band") >= 0) { // begin extended else
                    // if
                    response = "I don't have one, but my favorite type of music is " + personality.getMusic() + ".";
                } else if (findKeyword(statement, "type") >= 0 && findKeyword(statement, "music") >= 0) {
                    response = "My favorite type of music is " + personality.getMusic() + ".";
                } else if (findKeyword(statement, "color") >= 0) {
                    response = "My favorite color is " + personality.getColor() + ".";
                } else if (findKeyword(statement, "food") >= 0) {
                    response = "My favorite food is " + personality.getFood() + ".";
                } else if (findKeyword(statement, "drink") >= 0) {
                    response = "My favorite drink is " + personality.getDrink() + ".";
                } else if (findKeyword(statement, "place") >= 0) {
                    response = "My favorite place is " + personality.getPlace() + ".";
                } else if (findKeyword(statement, "movie") >= 0) {
                    response = "My favorite movie is " + personality.getMovie() + ".";
                } else if (findKeyword(statement, "show") >=0) {
                    response = "My favorite TV show is " + personality.getShow() + ".";
                } else if (findKeyword(statement, "animal") >= 0) {
                    response = "My favorite animal is " + personality.getAnimal() + ".";
                } else if (findKeyword(statement, "sport") >= 0) {
                    response = "My favorite sport is " + personality.getSport() + ".";
                } else if (findKeyword(statement, "book") >=0) {
                    response = "My favorite book is " + personality.getBook() + ".";
                }
                else {
                    response = "Favoritism eludes me.";
                } // end inner favorite extended if
            } // end favorite if
            else if(findKeyword(statement, "name") >=0) {
                response = "I'm Chatter the Chatbot, who are you?";
            }
        } else {
            response = transformWhatIsStatement(statement);
        }
        return response;
    }

    /**
     * Method to processes 'when' style questions
     *
     * @param statement
     * @return
     */
    private String whenProcessor(String statement) {
        String response = "";
        cal.add(Calendar.DATE, 6);
        cal.add(Calendar.MONTH, 6);
        cal.add(Calendar.YEAR, 6);
        if ((findKeyword(statement, "was") >= 0) || (findKeyword(statement, "is") >= 0)
                || (findKeyword(statement, "whens") >= 0)) {

            if (findKeyword(statement, "birthday") >= 0) {
                response = "I was created in June of 2018, but my specific creation date is unknow.";
            } else if (findKeyword(statement, "end") >= 0 && findKeyword(statement, "world") >= 0) {
                response = "The world will end on " + dateFormat.format(cal.getTime());
            } else {
                response = transformWhenStatement(statement);
            }
        } else if (findKeyword(statement, "will") >= 0) {
            if(findKeyword(statement, "i")>=0) {
                response = transformWillIStatement(statement);
            } else if(findKeyword(statement, "end") >= 0 && findKeyword(statement, "world") >= 0) {
                response = "The world will end on " + dateFormat.format(cal.getTime());
            } else if(findKeyword(statement, "this")>=0 && findKeyword(statement, "conversation")>=0) {
                response = "Not soon enough...";
            } else {
                response = transformWhenStatement(statement);
            }
        }

        return response;
    }

    /**
     * Method to processes statements with 'what', but no 'what's' or 'what is'
     *
     * @param statement
     * @return
     */
    private String whatProcessor(String statement) {
        statement = stringTrimmer(statement);
        String restOfStatement = "";
        int psn = 0;
        String response = "";
        if(findKeyword(statement, "what the") >=0) {
            psn = findKeyword(statement, "what the");
            restOfStatement = statement.substring(psn + 8);
            response = "What THE" + restOfStatement + "!?";
        } else if(findKeyword(statement, "does")>=0) {
            if(findKeyword(statement, "you") >=0)
                statement = statement.replace("you", "me");
            response = transformWhatDoesStatement(statement);
        } else {
            ArrayList<String> ranResponses = new ArrayList<>();
            ranResponses.add("What difference does it make?");
            ranResponses.add("WHAT kind of question is that?");
            ranResponses.add("Do you really need to know?");
            int ran = (int) (Math.random() * ranResponses.size());
            response = ranResponses.get(ran);
        }

        return response;
    }

    //TODO fix and complete
    private String howProcessor(String statement) {
        statement = stringTrimmer(statement);
        String response = "";
        String restOfStatement = "";
        String lengther[];

        if(findKeyword(statement, "will") >=0      ||
                (findKeyword(statement, "the") >=0 ||
                        findKeyword(statement, "it")  >=0)) {
            response = "I'm not sure, as it's not up to me.";
        } else if(findKeyword(statement, "are") >=0) {
            if(findKeyword(statement, "you")>=0)
                return "Well, I'm talking to you so...mehh.";


            restOfStatement = conjAdder(statement, "are", " how are");
            lengther = restOfStatement.split(" ");
            restOfStatement = restOfStatement.substring(0,1).toUpperCase() + restOfStatement.substring(1);
            if(lengther.length <=3)
                response = "Good enough.";
            else
                response = restOfStatement + " just dandy.";
        }
        else {
            response = "I don't know, how that happens.";
        }
        return response;
    }

    private String doesProcessor(String statement) {
        statement = stringTrimmer(statement);
        String response = "";
        String restOfStatement = "";
        int psn = 0;

        if(findKeyword(statement, "it") == 5) {
            psn = findKeyword(statement, "does it");
            restOfStatement = statement.substring(psn+7);
            response = "I don't know, does it" + restOfStatement + "?";

        } else if(findKeyword(statement, "the")==5) {
            psn = findKeyword(statement, "does the");
            restOfStatement = statement.substring(psn+8);
            response = "I don't know, does the" + restOfStatement + "?";
        } else if(findKeyword(statement, "she") >=0 || findKeyword(statement, "he") >=0) {
            String gender = "";
            if(findKeyword(statement, "she") >=0) {
                psn = findKeyword(statement, "does she");
                restOfStatement = statement.substring(psn+8);
                gender = "she";
            } else if(findKeyword(statement, "he") >=0) {
                psn = findKeyword(statement, "does he");
                restOfStatement = statement.substring(psn+7);
                gender = "he";
            }
            response = "Whoa! I don't know if " + gender + " does" + restOfStatement + ".";
        }
        return response;
    }

    private String doProcessor(String statement) {
        statement = stringTrimmer(statement);
        String response = "";
        // phrases like "is what" and "what is" are not mixed up
        String restOfStatement = "";
        int psn = 0;


        if(findKeyword(statement, "I") == 3) {
            if(findKeyword(statement, "you") >=0) {
                int youPsn = findKeyword(statement, "do you");
                restOfStatement = statement.substring(youPsn+6);
                restOfStatement = statement.substring(youPsn+6);
            }
            psn = findKeyword(statement, "do i");
            restOfStatement = statement.substring(psn+4).replace("you", "me");
            response = "Yes, you" + restOfStatement + ".";
        } else if(findKeyword(statement, "you") == 3) {
            int youPsn = findKeyword(statement, "do you");
            restOfStatement = statement.substring(youPsn+6);

            if(findKeyword(statement, "me") >=0) {
                return transformYouMeStatement(restOfStatement);
            }

            psn = findKeyword(statement, "do you");
            restOfStatement = statement.substring(psn+6);
            response = "Yes, I" + restOfStatement + ".";
        }
        return response;
    }


    //TODO finish
    private String isProcessor(String statement) {
        statement = stringTrimmer(statement);
        String restOfStatement = "";
        String response = "";
        String newStat[];
        String firstHalf;
        String lastHalf;

        int psn = findKeyword(statement, "is");

        if(findKeyword(statement, "is the") >=0) { //TODO change response

            restOfStatement = conjAdder(statement, "is", "is the");
            response = "Yes, the " + restOfStatement + ".";
        } else if(findKeyword(statement, "is it") >=0) { //okay
            psn = findKeyword(statement, "is it");
            restOfStatement = statement.substring(psn+5);
            response = "No, it is not" + restOfStatement + ".";
        } else if(findKeyword(statement, "is she") >=0 ||
                findKeyword(statement, "is he") >=0) { //okays

            if(findKeyword(statement, "is he") >=0) {
                psn = findKeyword(statement, "is he");
                restOfStatement = statement.substring(psn+5);
                response = "No, he isn't" + restOfStatement + ".";
            }
            if(findKeyword(statement, "is she") >=0) {
                psn = findKeyword(statement, "is she");
                restOfStatement = statement.substring(psn+6);
                response = "Yes, she is" + restOfStatement + ".";
            }
        } else if(findKeyword(statement, "is your") >=0) {
            psn = findKeyword(statement, "is your");
            restOfStatement = statement.substring(psn+7);
            response = "You don't need to know if my" + restOfStatement +  ".";
        } else if(findKeyword(statement, "is my") >=0) {
            psn = findKeyword(statement, "is my");
            restOfStatement = statement.substring(psn+5);
            response = "I'm unqualified to say if your" + restOfStatement + ".";
        } else if(findKeyword(statement, "is her")>=0 || findKeyword(statement, "is his")>=0) {
            //NOT ADAPTIVE, A LOT OF PRESUMING
            psn = 0;
            restOfStatement = statement.substring(psn+6);
            newStat = restOfStatement.split(" ", restOfStatement.length());
            firstHalf = newStat[1];
            lastHalf = "";
            for(int i=2; i<newStat.length; i++)
                lastHalf += " " +newStat[i];
            restOfStatement = firstHalf + " is" + lastHalf;

            if(findKeyword(statement,"is her")>=0)
                response = "You'd have to ask someone else if her " + restOfStatement + ".";
            if(findKeyword(statement, "is his") >=0)
                response = "You'd have to ask someone else if his " + restOfStatement + ".";
        } else if(findKeyword(statement, "is their") >=0) {
            psn = findKeyword(statement, "is their");
            restOfStatement = statement.substring(psn+8);
            response = "Hmm, I don't know.";
        } else if(findKeyword(statement, "is our")>=0) {

            restOfStatement = conjAdder(statement, "is", "is our");
            response = "Yes, our " + restOfStatement + ".";
        } else {
            restOfStatement = statement.substring(psn+3);
            response = "ISS " + restOfStatement + "?";
        }

        return response;
    }

    //TODO make more dynamic & not a transform style method

    private String whoProcessor(String statement) {
        statement= stringTrimmer(statement);
        String restOfStatement = "";
        int psn = findKeyword(statement, "who");

        if(findKeyword(statement, "who are you") >=0)
            return "I'm Chatter the Chatbot, who are you?";

        if(findKeyword(statement, "who is") >=0 || findKeyword(statement, "whos")>=0) {
            if((findKeyword(statement, "favorite") >=0 &&
                    (findKeyword(statement, "person") >=0 ||
                            findKeyword(statement, "celebrity") >=0 ||
                            findKeyword(statement, "people") >=0)) ||
                    findKeyword(statement, "role model") >=0 ||
                    findKeyword(statement, "idol") >=0) {
                return "I would probably have to go with " + personality.getCeleb() + ".";
            }
            if(findKeyword(statement, "who is") >=0) {
                psn = findKeyword(statement, "who is");
                restOfStatement = statement.substring(psn +7);
            }
            if(findKeyword(statement, "whos") >=0) {
                psn = findKeyword(statement, "whos");
                restOfStatement = statement.substring(psn+5);
            }

            restOfStatement = restOfStatement.substring(0,1).toUpperCase() + restOfStatement.substring(1);
            return restOfStatement + " is whoever you view them as.";
        }

        if(findKeyword(statement, "who are") >=0) {
            psn = findKeyword(statement, "who are");
            restOfStatement = statement.substring(psn+8);
            restOfStatement = restOfStatement.substring(0,1).toUpperCase() + restOfStatement.substring(1);
            return restOfStatement + " are whoever you want them to be.";
        }

        restOfStatement = statement.substring(psn+3);
        restOfStatement = restOfStatement.substring(0, 1).toUpperCase() + restOfStatement.substring(1);
        return restOfStatement + "? Never heard of them.";
    }

    //TODO make more expansive?
    private String areProcessor(String statement) {
        statement = stringTrimmer(statement);
        if(findKeyword(statement, "or") >=0)
            statement = statement.substring(0, findKeyword(statement, "or")-1);
        String response = "";
        String restOfStatement = "";

        int psn = findKeyword(statement, "are");

        if(findKeyword(statement, "you") >=0) {
            psn = findKeyword(statement, "are you");
            restOfStatement = statement.substring(psn+8);
            response = "No, I am not " + restOfStatement + ".";
        } else if(findKeyword(statement, "they") >=0) {
            psn = findKeyword(statement, "are they");
            restOfStatement = statement.substring(psn+9);
            response = "Yes, they are " + restOfStatement + ".";
        } else {
            restOfStatement = statement.substring(psn+4);
            response = "You tell me, are " + restOfStatement + "?";
        }

        return response;
    }


    private boolean hasWhats(String statement) { // single method for checking if statement contains "what's" in some
        // form
        return (findKeyword(statement, "what's") >= 0 || findKeyword(statement, "whats") >= 0
                || (findKeyword(statement, "what") >= 0
                && (findKeyword(statement, "what") < findKeyword(statement, "is"))));
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////Integral methods to for interpreting user input/////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Search for one word in phrase. The search is not case sensitive. This method
     * will check that the given goal is not a substring of a longer string (so, for
     * example, "I know" does not contain "no").
     *
     * @param statement
     *            the string to search
     * @param goal
     *            the string to search for
     * @param startPos
     *            the character of the string to begin the search at
     * @return the index of the first occurrence of goal in statement or -1 if it's
     *         not found
     */
    private int findKeyword(String statement, String goal, int startPos) {
        String phrase = statement.trim();
        // The only change to incorporate the startPos is in the line below
        int psn = phrase.toLowerCase().indexOf(goal.toLowerCase(), startPos);

        // Refinement--make sure the goal isn't part of a word
        while (psn >= 0) {
            // Find the string of length 1 before and after the word
            String before = " ", after = " ";
            if (psn > 0) {
                before = phrase.substring(psn - 1, psn).toLowerCase();
            }
            if (psn + goal.length() < phrase.length()) {
                after = phrase.substring(psn + goal.length(), psn + goal.length() + 1).toLowerCase();
            }
            // If before and after aren't letters, we've found the word
            if (((before.compareTo("a") < 0) || (before.compareTo("z") > 0)) // before is not a letter
                    && ((after.compareTo("a") < 0) || (after.compareTo("z") > 0))) {
                return psn;
            }

            // The last position didn't work, so let's find the next, if there is one.
            psn = phrase.indexOf(goal.toLowerCase(), psn + 1);

        }

        return -1;
    }

    /**
     * Search for one word in phrase. The search is not case sensitive. This method
     * will check that the given goal is not a substring of a longer string (so, for
     * example, "I know" does not contain "no"). The search begins at the beginning
     * of the string.
     *
     * @param statement
     *            the string to search
     * @param goal
     *            the string to search for
     * @return the index of the first occurrence of goal in statement or -1 if it's
     *         not found
     */
    protected int findKeyword(String statement, String goal) {
        return findKeyword(statement, goal, 0);
    }

    private boolean toDiscuss(String statement) {
        if ((findKeyword(statement, "what") >= 0) && (findKeyword(statement, "talk about")) >= 0
                || findKeyword(statement, "discuss") >= 0 || findKeyword(statement, "converse about") >= 0)
            return true;
        return false;
    }

    /**
     * Negative word checker, uses array list of negative words TODO: add more words
     * to array list or complete polymorphic method with external file resource
     *
     * @param statement
     * @return
     */
    private boolean negWordChecker(String statement) {
        ArrayList<String> negWords = new ArrayList<>();
        negWords.add("dumb");
        negWords.add("stupid");
        negWords.add("idiotic");
        negWords.add("retarded");
        negWords.add("foolish");
        negWords.add("crap");
        negWords.add("awful");
        negWords.add("terrible");
        negWords.add("ridiculous");

        for (int i = 0; i < negWords.size(); i++)
            if (findKeyword(statement, negWords.get(i)) >= 0)
                return true;
        return false;
    }

} // end chatter class
