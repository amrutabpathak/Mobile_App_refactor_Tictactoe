package edu.neu.mad_sea.tictactoejava.bean;

public class Player {
    private int score;
    private String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public Player(String id, int score){
        this.id = id;
        this.score = score;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
