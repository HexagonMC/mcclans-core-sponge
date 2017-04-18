package nl.riebie.mcclans.economy;

/**
 * Created by k.volkers on 17-4-2017.
 */
public class Tax {

    private String name;
    private double cost;

    public Tax(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }
}
