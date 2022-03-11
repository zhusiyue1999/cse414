SELECT R.restaurant, R.date FROM MyRestaurants AS R WHERE date('now', '-3 month') > date(date) and R.vegetarian = 1;
