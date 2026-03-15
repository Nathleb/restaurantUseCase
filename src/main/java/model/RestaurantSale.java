package model;

import java.time.LocalDate;
import java.util.List;

import model.restaurant.Meal;

public record RestaurantSale(String customerName, LocalDate date, List<Meal> meals) {}
