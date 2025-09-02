package com.radomskyi.budgeter.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tags for classifying expenses")
public enum Tag {
    
    @Schema(description = "Food and groceries")
    FOOD,

    @Schema(description = "Bars and restaurants")
    BARS_AND_RESTAURANTS,
    
    @Schema(description = "Transportation and travel expenses")
    TRANSPORT,
    
    @Schema(description = "Entertainment and leisure expenses")
    ENTERTAINMENT,
    
    @Schema(description = "Shopping and retail expenses")
    SHOPPING,
    
    @Schema(description = "Healthcare and medical expenses")
    HEALTH,
    
    @Schema(description = "Education and learning expenses")
    EDUCATION,
    
    @Schema(description = "Housing and accommodation expenses")
    HOUSING,

    @Schema(description = "For those fancy sneakers")
    CLOTHING,
    
    @Schema(description = "Utility and service expenses")
    UTILITIES,
    
    @Schema(description = "Insurance")
    INSURANCE,

    @Schema(description = "Pet expenses")
    PETS,

    @Schema(description = "Subscriptions and memberships")
    SUBSCRIPTIONS,

    @Schema(description = "Sports and hobbies")
    SPORTS_AND_HOBBIES,

    @Schema(description = "For you")
    PERSONAL_CARE,

    @Schema(description = "For the loved ones")
    GIFTS,

    @Schema(description = "Donations and charity")
    DONATIONS,

    @Schema(description = "Banking and Taxes")
    BANKING_AND_TAXES,

    @Schema(description = "Travel expenses")
    TRAVEL,

    @Schema(description = "Fun stuff")
    VICES,
    
    @Schema(description = "Other miscellaneous expenses")
    OTHER
}
