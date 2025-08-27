package com.radomskyi.budgeter.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categories")
public enum Category {
    
    @Schema(description = "Recurring, unavoidable expenses that don't change much month to month. Think: rent/mortgage, utilities, insurance, loan payments, subscriptions. Goal: Keep them as low as possible relative to your income, since they reduce flexibility.")
    FIXED,

    @Schema(description = "Essential expenses you can't avoid, but they may vary in cost. Examples: groceries, transportation (gas, transit), healthcare, childcare. These are non-negotiable for living, but you may have some control (e.g., cooking at home vs. eating out)")
    NEEDS,
    
    @Schema(description = "Everything that’s non-essential / lifestyle-driven. Examples: dining out, travel, shopping, entertainment, hobbies, streaming services. These are the most flexible part of your budget—you can cut back here if you need to save or hit a financial goal.")
    WANTS
}
