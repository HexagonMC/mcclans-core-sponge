package nl.riebie.mcclans.player;

/**
 * TODO create API interface
 * <p>
 * Created by Kippers on 18-4-2017.
 */
public class EconomyStats {

    private double deposit;
    private double withdraw;
    private double tax;
    private double debt;

    public EconomyStats(double deposit, double withdraw, double tax, double debt) {
        this.deposit = deposit;
        this.withdraw = withdraw;
        this.tax = tax;
        this.debt = debt;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public double getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(double withdraw) {
        this.withdraw = withdraw;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }
}
