package com.example.oop_wsiiz.models;

public record Movie(
        int id,
        String title,
        String director,
        int releaseYear,
        String startTime,
        int duration,
        double ticketPrice,
        int roomId
) {

    @Override
    public String toString() {
        return title + " (" + releaseYear + ") directed by " + director;
    }
}