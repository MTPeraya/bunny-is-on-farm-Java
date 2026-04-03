public class Calendar {
    public String[] seasons = {"Spring", "Summer", "Fall", "Winter"};
    public String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    
    public int currentSeasonIndex = 0;
    public int currentDay = 0;
    public int currentDate = 1;
    public int currentYear = 1;
    
    private long dayTimer = 0;
    private long dayDuration = 60000; // 60 seconds per day mapping
    private long lastUpdateTime;
    
    public Calendar() {
        lastUpdateTime = System.currentTimeMillis();
    }
    
    public void update() {
        long current = System.currentTimeMillis();
        long delta = current - lastUpdateTime;
        lastUpdateTime = current;
        
        dayTimer += delta;
        if (dayTimer >= dayDuration) {
            dayTimer = 0;
            advanceDay();
        }
    }
    
    public void advanceDay() {
        currentDay = (currentDay + 1) % 7;
        currentDate++;
        if (currentDate > 28) {
            currentDate = 1;
            currentSeasonIndex = (currentSeasonIndex + 1) % 4;
            if (currentSeasonIndex == 0) {
                currentYear++;
            }
        }
    }
    
    public String getCurrentSeason() {
        return seasons[currentSeasonIndex];
    }
    
    public String getCurrentDayName() {
        return daysOfWeek[currentDay];
    }
    
    public int getCurrentWeek() {
        return (currentDate - 1) / 7 + 1;
    }
    
    public String getDateString() {
        return getCurrentDayName() + " " + currentDate + ", " + getCurrentSeason() + ", Year " + currentYear;
    }
}
