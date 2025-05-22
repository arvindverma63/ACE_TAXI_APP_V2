package com.app.ace_taxi_v2.Models.AddressIO;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AutocompleteResponse {

    @SerializedName("suggestions")
    private List<Suggestion> suggestions;

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }
}
