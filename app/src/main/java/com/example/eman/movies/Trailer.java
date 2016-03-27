package com.example.eman.movies;

public class Trailer {
    private String trailerName;
    private String trailerKey;

    public Trailer(String trailerName, String trailerKey) {
        this.trailerName = trailerName;
        this.trailerKey = trailerKey;
    }

    public String getTrailerName() {
        return this.trailerName;
    }

    public String getTrailerKey() {
        return this.trailerKey;
    }
}
