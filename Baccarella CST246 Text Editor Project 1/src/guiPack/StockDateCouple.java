package guiPack;

import java.time.LocalDate;

public class StockDateCouple implements Comparable{ //used to store data as a single object in binary tree, uses local
	
	double stockPrice;
	LocalDate date;
	public StockDateCouple(LocalDate date, double stockPrice) {
		this.stockPrice = stockPrice;
		this.date = date;
	}
	public double getStockPrice() {
		return stockPrice;
	}
	public void setStockPrice(double stockPrice) {
		this.stockPrice = stockPrice;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	@Override
	public int compareTo(Object o){//using a generic object allowed for easier comparisons
		if(o instanceof StockDateCouple){
			return date.compareTo(((StockDateCouple)o).getDate());
		}
		if(o instanceof LocalDate){
			return date.compareTo((LocalDate)o);
		}
		throw new ClassCastException();
	}
	@Override
	public String toString(){
		return date.toString() + ", " + stockPrice;
	}

}
