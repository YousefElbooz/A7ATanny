package model;
public class Visitor {
    protected String name;
    protected String password;
    protected int id;
    protected String type;
    private int hoursReserved;
    private int hoursRewarded;
    private double penaltyFees;
    private double totalFees;
    private double payedFees;

    public Visitor(String name, String password, int id, String type, int hoursReserved) {
        this.name = name;
        this.password = password;
        this.id = id;
        this.type = type;
        this.penaltyFees = 0;
        this.hoursReserved = 0;
        this.hoursRewarded=0;
        this.totalFees = 0;
        this.payedFees=0;
        this.hoursReserved = hoursReserved;

    }

    // Overloaded constructor where hoursReserved defaults to 0
    public Visitor(String name, String password, int id, String type) {
        this(name, password, id, type, 0); // Calls the first constructor and passes 0 for hoursReserved
    }

    public String getName(){
        return this.name;
    }

    public String getPassword(){
        return this.password;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getHoursReserved() {
        return hoursReserved;
    }

    public void setHoursReserved(int hoursReserved) {
        this.hoursReserved = hoursReserved;
    }

    public int getHoursRewarded() {
        return this.hoursRewarded;
    }

    public void setHoursRewarded(int hoursRewarded) {
        this.hoursRewarded = hoursRewarded;
    }

    public double getPenaltyFees() {
        return penaltyFees;
    }

    public void setPenaltyFees(double penaltyFees) {
        this.penaltyFees = penaltyFees;
    }

    public double getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(double RoomFees) {
        this.totalFees = RoomFees+this.penaltyFees;
    }

    public void incrementHoursReserved(int hours) {
        this.hoursReserved += hours;
    }

    public void addRewardHours(int rewardHours){
        this.hoursRewarded +=rewardHours;
    }

    public void decrementHoursReserved(int hours) {
        this.hoursReserved -= hours;
    }

    public void decrementHoursRewarded(int hours) {
        this.hoursRewarded -= hours;
    }

    public void displayDetails(){
        System.out.println("Name: " + name + " ID: " + id + " Type: " + type + " Hours Reserved: "+ hoursReserved + " Hours Rewarded: "+ this.hoursRewarded+" TotalFees: "+this.totalFees+ " Penalty Fees: "+ penaltyFees+ " Payed Fees: "+ payedFees );
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void payTotalFees(){
        this.payedFees+=totalFees;
        this.penaltyFees=0;
        this.totalFees=0;
    }

    public void payPenaltyFees(){
        this.payedFees+=penaltyFees;
        this.penaltyFees=0;
    }

    public double getPayedFees() {
        return payedFees;
    }

    public void setPayedFees(double payedFees) {
        this.payedFees = payedFees;
    }

}
